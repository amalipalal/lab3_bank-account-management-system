package utils;

import models.Account;
import models.CheckingAccount;
import models.Customer;
import models.Transaction;
import models.enums.TransactionType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DisplayUtil {
    private static final int DISPLAY_STROKE_LENGTH = 110;
    public static final String COLUMN_FORMAT_ACCOUNT_ROW = "| %-15s | %-25s | %-25s | %-15s | %-10s |%n";
    public static final String NEW_CHECKING_ACCOUNT_FORMAT = "Account Number: %s%n" +
            "Customer: %s%n" +
            "Account Type: %s%n" +
            "Initial Balance: %s%n" +
            "Overdraft Limit: %s%n" +
            "Monthly Fee: %s%s%n" +
            "Status: %s%n";
    public static final String NEW_SAVINGS_ACCOUNT_FORMAT = "Account Number: %s%n" +
            "Customer: %s%n" +
            "Account Type: %s%n" +
            "Initial Balance: %s%n" +
            "Interest Rate: %s%n" +
            "Minimum Balance: %s%n" +
            "Status: %s%n";

    public static void displayMainMenu() {
        System.out.println();
        System.out.println("1. Create Account");
        System.out.println("2. View Accounts");
        System.out.println("3. Process Transaction");
        System.out.println("4. View Transaction History");
        System.out.println("5. Exit");
        System.out.println();
    }

    public static void displayNewAccount(Account account) {
        System.out.println(account.displayNewAccountDetails());
    }

    private static String displayCustomerDetails(Customer customer) {
        return customer.getName() + " (" + customer.getCustomerType() + ")";
    }

    public static String displayAmount(double amount) {
        return String.format("$%,.2f", amount);
    }

    public static String displayDecimal(double decimal) {
        return String.format("%.1f%%", decimal);
    }

    public static String generateMonthlyFeeMetadata (CheckingAccount account) {
        Customer customer = account.getCustomer();

        if(account.getMonthlyFee() == 0)
            return " (" + "WAIVED - " + customer.getCustomerType() + " Customer )";

        return " (" + "NOT WAIVED - " + customer.getCustomerType() + " Customer )";
    }

    public static String formatStatus(String status) {
        return status.substring(0, 1).toUpperCase() + status.substring(1);
    }

    public static void displayAccountDetails(Account account) {
        Customer customer = account.getCustomer();

        System.out.println("Customer: " + customer.getName());
        System.out.println("Account type: " + account.getAccountType());
        System.out.println("Current Balance: " + displayAmount(account.getBalance()));
    }

    public static void displayAccountListing(Account[] accounts) {
        System.out.println("-".repeat(DISPLAY_STROKE_LENGTH));
        System.out.printf(COLUMN_FORMAT_ACCOUNT_ROW, "ACC NO", "CUSTOMER NAME", "TYPE", "BALANCE", "STATUS");
        System.out.println("-".repeat(DISPLAY_STROKE_LENGTH));

        for (Account account : accounts) {
            System.out.print(account.displayAccountDetails());
            System.out.println("-".repeat(DISPLAY_STROKE_LENGTH));
        }
    }

    public static void displayTransaction(Transaction transaction) {
        double previousAccountBalance = computePreviousBalance(transaction);

        System.out.println("Transaction ID: " + transaction.getTransactionId());
        System.out.println("Account: " + transaction.getAccountNumber());
        System.out.println("Type: " + transaction.getTransactionType());
        System.out.println("Amount: " + displayAmount(transaction.getAmount()));
        System.out.println("Previous Balance: " + displayAmount(previousAccountBalance));
        System.out.println("New Balance: " + displayAmount(transaction.getBalanceAfter()));
        System.out.println("Date/Time: " + displayTimestamp(transaction.getTimestamp()));
    }

    private static double computePreviousBalance (Transaction transaction) {
        if (transaction.getTransactionType() == TransactionType.WITHDRAWAL) {
            return transaction.getBalanceAfter() + transaction.getAmount();
        } else if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
            return transaction.getBalanceAfter() - transaction.getAmount();
        } else {
            return 0;
        }
    }

    public static void displayMultipleTransactions(Transaction[] transactions) {
        String columnFormat = "| %-15s | %-20s | %-10s | %-15s | %-15s |%n";

        System.out.println("-".repeat(DISPLAY_STROKE_LENGTH));
        System.out.printf(columnFormat, "TXN ID", "DATE/TIME", "TYPE", "AMOUNT", "BALANCE");
        System.out.println("-".repeat(DISPLAY_STROKE_LENGTH));

        for (Transaction transaction : transactions) {
            String dateTime = displayTimestamp(transaction.getTimestamp());
            String type = transaction.getTransactionType().toString().toUpperCase();

            String amountSign = type.equalsIgnoreCase("Deposit") ? "+" : "-";
            String amount = amountSign + displayAmount(transaction.getAmount());
            String balance = displayAmount(transaction.getBalanceAfter());

            System.out.printf(columnFormat, transaction.getTransactionId(), dateTime, type, amount, balance);
            System.out.println("-".repeat(DISPLAY_STROKE_LENGTH));
        }
    }

    private static String displayTimestamp(String timestamp) {
        Instant instant = Instant.parse(timestamp);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
        ZonedDateTime localDateTime = instant.atZone(ZoneId.systemDefault());
        return localDateTime.format(dateTimeFormatter);
    }

    public static void displayNotice(String message) {
        System.out.println("-".repeat(DISPLAY_STROKE_LENGTH));
        System.out.println(message);
        System.out.println("-".repeat(DISPLAY_STROKE_LENGTH));
        System.out.println();
    }

    public static  void displayHeading(String title) {
        System.out.println(title.toUpperCase());
        System.out.println("-".repeat(DISPLAY_STROKE_LENGTH));
    }
}
