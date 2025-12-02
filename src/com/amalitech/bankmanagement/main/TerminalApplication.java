package com.amalitech.bankmanagement.main;

import com.amalitech.bankmanagement.main.base.Account;
import com.amalitech.bankmanagement.main.base.Customer;
import com.amalitech.bankmanagement.main.domain.*;
import com.amalitech.bankmanagement.main.manager.AccountManager;
import com.amalitech.bankmanagement.main.manager.TransactionManager;
import com.amalitech.bankmanagement.main.service.BankingService;
import com.amalitech.bankmanagement.main.util.DataSeeder;
import com.amalitech.bankmanagement.main.util.DisplayUtil;
import com.amalitech.bankmanagement.main.util.ValidationUtil;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

public class TerminalApplication {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final BankingService BANKING_SERVICE = new BankingService(new AccountManager(), new TransactionManager());

    public static void start() {
        // Populate the program with already existing customer accounts
        // defined within seed method
        DataSeeder seeder = new DataSeeder(BANKING_SERVICE);
        seeder.seed();

        boolean userIsActive = true;

        while(userIsActive) {
            try {
                DisplayUtil.displayMainMenu();

                int userSelection = readInt("Select an option (1-5)", 1, 5);
                System.out.println();

                switch(userSelection) {
                    case 1:
                        handleAccountCreationFlow();
                        break;
                    case 2:
                        handleAccountListingFlow();
                        break;
                    case 3:
                        handleTransactionFlow();
                        break;
                    case 4:
                        handleTransactionListingFlow();
                        break;
                    case 5:
                        userIsActive = false;
                        break;
                    default:
                        DisplayUtil.displayNotice("Wrong number selection");
                }
            } catch (Exception e) {
                DisplayUtil.displayNotice(e.getMessage());
            }
        }
    }

    public static void handleAccountCreationFlow() {
        DisplayUtil.displayHeading("Account Creation");

        Customer newCustomer = createCustomerFlow();

        System.out.println();
        System.out.println("Account type:");
        System.out.println("1. Savings Account (Interest: 3.5%, Min Balance: $500)");
        System.out.println("2. Checking Account (Overdraft: $1,000, Monthly Fee: $10)");
        System.out.println();

        Account newAccount = createAccountFlow(newCustomer);

        System.out.println();

        showUserAccount(newAccount);

        System.out.println();
    }

    private static Customer createCustomerFlow() {
        String customerName = readNonEmptyString(
                "Enter customer name", ValidationUtil::validateName);

        int customerAge = readInt("Enter customer age", 1, 120);

        String customerContact = readNonEmptyString(
                "Enter customer contact", ValidationUtil::validatePhoneNumber);

        String customerAddress = readNonEmptyString(
                "Enter customer address", ValidationUtil::validateAddress);

        System.out.println();
        System.out.println("Customer type:");
        System.out.println("1. Regular Customer (Standard banking services)");
        System.out.println("2. Premium Customer (Enhanced benefits, min balance $10,000)");
        System.out.println();

        int customerType = readInt("Select type (1-2)", 1, 2);

        return customerType == 1
                ? new RegularCustomer(customerName, customerAge, customerContact, customerAddress)
                : new PremiumCustomer(customerName, customerAge, customerContact, customerAddress);
    }

    private static Account createAccountFlow(Customer customer) {
        int accountType = readInt("Select type (1-2)", 1, 2);

        double initialDeposit = readDouble("Enter initial deposit amount", 0);

        Account newAccount;
        if(accountType == 1) {
            newAccount = BANKING_SERVICE.createSavingsAccount(customer, initialDeposit);
        } else {
            newAccount = BANKING_SERVICE.createCheckingAccount(customer, initialDeposit);
        }

        return newAccount;
    }

    private static void showUserAccount(Account account) {
        System.out.println("Account Created successfully!");
        if(account instanceof SavingsAccount) {
            DisplayUtil.displayNewSavingsAccount((SavingsAccount) account);
        } else if (account instanceof CheckingAccount) {
            DisplayUtil.displayNewCheckingAccount((CheckingAccount) account);
        }
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + ": ");

            String input = SCANNER.nextLine();

            try {
                int value = Integer.parseInt(input);
                if(value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static double readDouble(String prompt, double min) {
        while (true) {
            System.out.print(prompt + ": ");

            String input = SCANNER.nextLine();

            try {
                double value = Double.parseDouble(input);
                if(value < min) {
                    System.out.println("Please enter a number more than " + min + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }

        }
    }

    private static String readNonEmptyString(String prompt, Function<String, String> validator) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = SCANNER.nextLine().trim();

            String errorMessage = validator.apply(input);

            // The validator returns null if the input is valid
            // so this is the case where there is no error message
            if(errorMessage == null) {
                return input;
            }

            System.out.println(errorMessage);
        }
    }

    public static void handleAccountListingFlow() {
        System.out.println("ACCOUNT LISTING");

        Account[] allAccounts = BANKING_SERVICE.viewAllAccounts();
        double totalBalance = BANKING_SERVICE.getTotalBankBalance();

        DisplayUtil.displayAccountListing(allAccounts);

        System.out.println();

        System.out.println("Total Accounts: " + allAccounts.length);
        System.out.println("Total Bank Balance: " + DisplayUtil.displayAmount(totalBalance));
        System.out.println();
    }

    public static void handleTransactionFlow() {
        DisplayUtil.displayHeading("Process Transaction");

        System.out.println();

        Account customerAccount = handleAccountValidationFlow();

        Transaction newTransaction = handleTransactionTypeFlow(customerAccount);

        handleTransactionConfirmation(customerAccount, newTransaction);
    }

    private static Account handleAccountValidationFlow() {
        String accountNumber = readNonEmptyString("Enter Account Number", ValidationUtil::validateAccountNumber);

        Account customerAccount = BANKING_SERVICE.getAccountByNumber(accountNumber);

        System.out.println("Account Details:");
        DisplayUtil.displayAccountDetails(customerAccount);

        System.out.println();

        return customerAccount;
    }

    private static Transaction handleTransactionTypeFlow(Account customerAccount) {
        System.out.println("Transaction type:");
        System.out.println("1. Deposit \n2. Withdrawal");
        System.out.println();

        int transactionType = readInt("Select type (1-2)", 1, 2);

        double transactionAmount = readDouble("Enter amount", 0);

        System.out.println();

        return transactionType == 1
                ? BANKING_SERVICE.processDeposit(customerAccount, transactionAmount)
                : BANKING_SERVICE.processWithdrawal(customerAccount, transactionAmount);
    }

    private static void handleTransactionConfirmation(Account customerAccount, Transaction newTransaction) {
        DisplayUtil.displayHeading("Transaction Confirmation");

        DisplayUtil.displayTransaction(newTransaction);

        System.out.println();

        boolean isConfirmed = readYesOrNo("Confirm transaction? (Y/N)");

        if (isConfirmed) {
            boolean isSuccessful = BANKING_SERVICE.confirmTransaction(customerAccount, newTransaction);

            if (isSuccessful) {
                System.out.println("Transaction completed successful!");
            } else {
                System.out.println("Transaction failed. Please try again.");
            }
        } else {
            System.out.println("Transaction not confirmed. Aborting.");
        }
    }

    private static boolean readYesOrNo(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");

            String input = SCANNER.nextLine();

            switch (input.toLowerCase()) {
                case "y":
                    return true;
                case "n":
                    return false;
                default:
                    System.out.println("Invalid input: Please enter Y or N.");
            }
        }
    }

    public static void handleTransactionListingFlow() {
        DisplayUtil.displayHeading("View Transaction history");

        String accountNumber = readNonEmptyString(
                "Enter Account Number", ValidationUtil::validateAccountNumber);

        Account customerAccount = BANKING_SERVICE.getAccountByNumber(accountNumber);

        DisplayUtil.displayAccountDetails(customerAccount);

        Transaction[] customerTransactions = BANKING_SERVICE.getTransactionsByAccount(accountNumber);

        if (customerTransactions.length == 0) {
            DisplayUtil.displayNotice("No transactions recorded for this account.");
        } else {
            DisplayUtil.displayMultipleTransactions(customerTransactions);
            displayTransactionTotals(customerTransactions);
        }
    }

    private static void displayTransactionTotals(Transaction[] transactions) {
        String accountNumber = transactions[0].getAccountNumber();

        double totalDeposits = BANKING_SERVICE.getTotalDeposit(accountNumber);
        double totalWithdrawals = BANKING_SERVICE.getTotalWithdrawals(accountNumber);

        double netChange = totalDeposits - totalWithdrawals;

        System.out.println("Total Transactions: " + transactions.length);
        System.out.println("Total Deposits: " + DisplayUtil.displayAmount(totalDeposits));
        System.out.println("Total Withdrawals: " + DisplayUtil.displayAmount(totalWithdrawals));
        System.out.println("Net Change: " + DisplayUtil.displayAmount(netChange));
    }
}
