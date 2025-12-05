package handlers;

import config.AppConfig;
import models.*;
import models.enums.CustomerType;
import models.exceptions.InsufficientFundsException;
import models.exceptions.OverdraftExceededException;
import services.BankingService;
import utils.DisplayUtil;
import utils.InputReader;
import utils.ValidationUtil;

public class AccountFlowHandler {

    private final BankingService bankingService;
    private final InputReader input;

    public AccountFlowHandler(BankingService bankingService, InputReader input) {
        this.bankingService = bankingService;
        this.input = input;
    }

    /// Outline user flow for creating an account, from customer creation
    /// to account type selection to initial deposit
    public void handleAccountCreationFlow() {
        DisplayUtil.displayHeading("Account Creation");

        Customer newCustomer = createCustomerFlow();

        displayAccountTypeOptions();

        Account newAccount;
        try {
            newAccount = createAccountFlow(newCustomer);
        } catch (OverdraftExceededException | InsufficientFundsException e) {
            DisplayUtil.displayNotice(e.getMessage());
            return;
        }

        System.out.println();

        showUserAccount(newAccount);

        System.out.println();
    }

    private Customer createCustomerFlow() {
        String customerName = this.input.readNonEmptyString(
                "Enter customer name", ValidationUtil::validateName);

        int customerAge = this.input.readInt("Enter customer age", 1, 120);

        String customerContact = this.input.readNonEmptyString(
                "Enter customer contact", ValidationUtil::validatePhoneNumber);

        String customerAddress = this.input.readNonEmptyString(
                "Enter customer address", ValidationUtil::validateAddress);

        displayCustomerTypeOptions();

        int customerType = this.input.readInt("Select type (1-2)", 1, 2);

        return customerType == 1
                ? new RegularCustomer(customerName, customerAge, customerContact, customerAddress)
                : new PremiumCustomer(customerName, customerAge, customerContact, customerAddress);
    }

    private void displayCustomerTypeOptions() {
        System.out.println();
        System.out.println("Customer type:");
        System.out.println("1. Regular Customer (Standard banking services)");
        System.out.println("2. Premium Customer (Enhanced benefits, min balance $10,000)");
        System.out.println();
    }

    private void displayAccountTypeOptions() {
        System.out.println();
        System.out.println("Account type:");
        System.out.println("1. Savings Account (Interest: 3.5%, Min Balance: $500)");
        System.out.println("2. Checking Account (Overdraft: $1,000, Monthly Fee: $10)");
        System.out.println();
    }

    private Account createAccountFlow(Customer customer) throws OverdraftExceededException, InsufficientFundsException {
        int accountType = this.input.readInt("Select type (1-2)", 1, 2);

        double initialDeposit = getInitialDeposit(customer, accountType);

        Account newAccount = switch (accountType) {
            case 1 -> this.bankingService.createSavingsAccount(customer);
            case 2 -> this.bankingService.createCheckingAccount(customer);
            default -> throw new IllegalArgumentException("Invalid account type");
        };

        // record initial deposit as a transaction for audit
        Transaction firstTransaction = this.bankingService.processDeposit(newAccount, initialDeposit);
        this.bankingService.confirmTransaction(newAccount, firstTransaction);

        return newAccount;
    }

    private double getInitialDeposit(Customer customer, int accountType) {
        double initialDeposit;
        double minimumDeposit = getMinimumDeposit(customer, accountType);

        do {
            initialDeposit = this.input.readDouble("Enter initial deposit amount (minimum GHS " + minimumDeposit + ")", 0);
            if (initialDeposit < minimumDeposit) {
                System.out.println("Initial deposit must be at least $ " + minimumDeposit);
            }
        } while (initialDeposit < minimumDeposit);

        return initialDeposit;
    }

    private double getMinimumDeposit(Customer customer, int accountType) {
        if (customer.getCustomerType() == CustomerType.PREMIUM) {
            return AppConfig.MINIMUM_INITIAL_DEPOSIT_PREMIUM;
        }
        return (accountType == 1)
                ? AppConfig.MINIMUM_INITIAL_DEPOSIT_SAVINGS
                : AppConfig.MINIMUM_INITIAL_DEPOSIT_CHECKING;
    }

    private void showUserAccount(Account account) {
        System.out.println("Account Created successfully!");
        DisplayUtil.displayNewAccount(account);
    }

    public void handleAccountListingFlow() {
        System.out.println("ACCOUNT LISTING");

        Account[] allAccounts = this.bankingService.viewAllAccounts();
        double totalBalance = this.bankingService.getTotalBankBalance();

        DisplayUtil.displayAccountListing(allAccounts);

        System.out.println();

        System.out.println("Total Accounts: " + allAccounts.length);
        System.out.println("Total Bank Balance: " + DisplayUtil.displayAmount(totalBalance));
        System.out.println();
    }
}
