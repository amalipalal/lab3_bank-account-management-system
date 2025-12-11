package services;

import interfaces.DataStorageService;
import models.*;
import models.enums.AccountType;
import models.enums.CustomerType;
import models.enums.TransactionType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileStorageService implements DataStorageService {

    private final String accountsFile;
    private final String transactionsFile;

    private record CustomerData(String name, int age, String contact, String address) {}
    private record AccountInput(
            AccountType type, String number, Customer customer, double balance, String status, double monthlyFee){}

    private static final Map<CustomerType, Function<CustomerData, Customer>> CUSTOMER_FACTORY = Map.of(
            CustomerType.PREMIUM, data -> new PremiumCustomer(
                    data.name, data.age(), data.contact(), data.address()),
            CustomerType.REGULAR, data -> new RegularCustomer(
                    data.name(), data.age(), data.contact(), data.address())
    );

    private static final Map<AccountType, Function<AccountInput, Account>> ACCOUNT_FACTORY = Map.of(
            AccountType.SAVINGS, in -> new SavingsAccount(
                    in.number(), in.customer(), in.balance(), in.status()),
            AccountType.CHECKING, in -> {
                CheckingAccount acc = new CheckingAccount(
                        in.number(), in.customer(), in.balance(), in.status());
                acc.setMonthlyFee(in.monthlyFee);
                return acc;
            }
    );

    public FileStorageService(
            String accountsFile,
            String transactionsFile
    ) {
        this.accountsFile = accountsFile;
        this.transactionsFile = transactionsFile;
    }

    @Override
    public Map<String, Account> loadAccounts() throws IOException {
        Path path = Paths.get(this.accountsFile);
        if(Files.notExists(path)) return new HashMap<>();

        try(Stream<String> lines = Files.lines(path)) {
            return  lines
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .map(this::parseAccountLine)
                    .collect(Collectors.toMap(
                            Account::getAccountNumber,
                            Function.identity(),
                            (existing, duplicate) -> {
                                throw new IllegalStateException("Duplicate account number:" + existing.getAccountNumber());
                            },
                            HashMap::new
                    ));
        }
    }

    private Account parseAccountLine(String line) {
        String[] columns = line.split(",");

        final int EXPECTED_COLS = 10;
        if(columns.length < EXPECTED_COLS) {
            throw new IllegalArgumentException("Invalid account row (expected" + EXPECTED_COLS + " columns:" + "line");
        }

        AccountType accountType = AccountType.valueOf(columns[0].toUpperCase());
        String accountNumber = columns[1];
        CustomerType customerType = CustomerType.valueOf(columns[2].toUpperCase());
        String customerName = columns[3];
        int customerAge = parseInt(columns[4], "CustomerAge", line);
        String customerContact = columns[5];
        String customerAddress = columns[6];
        double accountBalance = parseDouble(columns[7], "AccountBalance", line);
        String accountStatus = columns[8];
        double monthlyFee = parseDouble(columns[9], "MonthlyFee", line);

        Customer customer = createCustomer(customerType, customerName, customerAge, customerContact, customerAddress);

        AccountInput input = new AccountInput(
                accountType, accountNumber, customer, accountBalance, accountStatus, monthlyFee);

        return createAccount(input);
    }

    private static int parseInt(String value, String fieldName, String line) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw  new IllegalArgumentException(
                    "Invalid integer for " + fieldName + ": " + value + "in line: " + line);
        }
    }

    private static double parseDouble(String value, String fieldName, String line) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw  new IllegalArgumentException(
                    "Invalid double for " + fieldName + ": " + value + "in line: " + line);
        }
    }

    private Customer createCustomer(CustomerType type, String name, int age, String contact, String address) {
        CustomerData data = new CustomerData(name, age, contact, address);
        Function<CustomerData, Customer> factory = CUSTOMER_FACTORY.get(type);
        if(factory == null) throw new IllegalArgumentException("Unsupported CustomerType: " + type);
        return factory.apply(data);
    }

    private Account createAccount(AccountInput input) {
        Function<AccountInput, Account> factory = ACCOUNT_FACTORY.get(input.type());
        if(factory == null) throw new IllegalArgumentException("Unsupported AccountType: " + input.type());
        return factory.apply(input);
    }

    @Override
    public void saveAccounts(List<Account> accounts) throws IOException {
        Path path = Paths.get(this.accountsFile);

        List<String> lines = new ArrayList<>();
        // Provides column structure that would be ignored when reading due to '#'
        lines.add("#AccountType,AccountNumber,CustomerType,CustomerName,CustomerAge,CustomerContact,CustomerAddress,AccountBalance,AccountStatus,MonthlyFee");

        accounts.forEach(account -> lines.add(account.toCsv()));
        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public Map<String, List<Transaction>> loadTransactions() throws IOException {
        Path path = Paths.get(this.transactionsFile);
        Map<String, List<Transaction>> transactions = new HashMap<>();

        if (Files.notExists(path)) return transactions;

        Set<String> seenIds = new HashSet<>();
        try (Stream<String> lines = Files.lines(path)) {
            lines.map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .forEach(line -> {
                        Transaction transaction = parseTransactionLine(line);
                        // Skip duplicate transactions
                        if(seenIds.add(transaction.getTransactionId()))
                            addToTransactions(transactions, transaction);
                    });
        }
        return transactions;
    }

    private void addToTransactions(Map<String, List<Transaction>> transactions, Transaction transaction) {
        transactions
                .computeIfAbsent(transaction.getAccountNumber(), k -> new ArrayList<>())
                .add(transaction);
    }

    private Transaction parseTransactionLine(String line) {
        String[] cols = line.split(",", -1);
        final int EXPECTED = 5;
        if (cols.length < EXPECTED) {
            throw new IllegalArgumentException(
                    "Invalid transaction row (expected " + EXPECTED + " columns): " + line);
        }

        cols = Arrays.stream(cols).map(String::trim).toArray(String[]::new);

        String txnId = cols[0];
        TransactionType txnType = TransactionType.valueOf(cols[1].toUpperCase());
        String accountNumber = cols[2];
        double amount = parseDouble(cols[3], "amount", line);
        double balanceAfter = parseDouble(cols[4], "balanceAfter", line);
        String timestamp = cols[5];

        return new Transaction(txnId, txnType, accountNumber, amount, balanceAfter, timestamp);
    }

    @Override
    public void saveTransactions(List<Transaction> transactions) throws IOException {

    }
}