package models;

import config.AppConfig;
import interfaces.Transactable;
import models.enums.AccountType;
import models.enums.TransactionType;
import models.exceptions.InsufficientFundsException;
import models.exceptions.InvalidAmountException;
import utils.DisplayUtil;

public class SavingsAccount extends Account implements Transactable {
    private final double INTEREST_RATE = AppConfig.INTEREST_RATE_SAVINGS_ACCOUNT;
    private final double MINIMUM_BALANCE = AppConfig.MINIMUM_INITIAL_DEPOSIT_SAVINGS;

    public SavingsAccount(Customer customer, double balance, String status) {
        super(customer, balance, status);
    }

    @Override
    public String displayAccountDetails() {
        String columnFormat = DisplayUtil.COLUMN_FORMAT_ACCOUNT_ROW;

        String mainRow = getMainRowDisplay(columnFormat);
        String extraRow = getExtraRowDisplay(columnFormat);

        return mainRow + extraRow;
    }

    private String getMainRowDisplay(String columnFormat) {
        String accountNumber = this.getAccountNumber();
        String customerName = this.getCustomer().getName();
        String accountType = this.getAccountType().toString();
        String balance = DisplayUtil.displayAmount(this.getBalance());
        String status = this.getStatus();

        return String.format(columnFormat,
                accountNumber, customerName, accountType, balance, status);
    }

    private String getExtraRowDisplay(String columnFormat) {
        String interestPercentage = DisplayUtil.displayDecimal(this.getINTEREST_RATE() * 100);
        String minimumBalance = DisplayUtil.displayAmount(this.getMINIMUM_BALANCE());

        String interestRateDisplay = "Interest Rate: " + interestPercentage;
        String minimumBalanceDisplay = "Min Balance: " + minimumBalance;

        return String.format(columnFormat, "", interestRateDisplay, minimumBalanceDisplay, "", "");
    }

    @Override
    public String displayNewAccountDetails() {
        Customer customer = getCustomer();

        return String.format(
                DisplayUtil.NEW_SAVINGS_ACCOUNT_FORMAT,
                getAccountNumber(),
                customer.displayCustomerDetails(),
                getAccountType(),
                DisplayUtil.displayAmount(getBalance()),
                DisplayUtil.displayDecimal(getINTEREST_RATE() * 100),
                DisplayUtil.displayAmount(getMINIMUM_BALANCE()),
                DisplayUtil.formatStatus(getStatus())
        );
    }

    @Override
    public boolean processTransaction(double amount, TransactionType type) throws InsufficientFundsException{
        if(amount <= 0) return false;

        switch (type) {
            case TransactionType.DEPOSIT:
                super.deposit(amount);
                break;
            case TransactionType.WITHDRAWAL:
                withdraw(amount);
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException{

        if(amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive");
        }

        double currentAccountBalance = super.getBalance();

        double newAccountBalance = currentAccountBalance - amount;
        if(newAccountBalance < 0) {
            throw new InvalidAmountException("Withdrawal amount exceeds available balance");
        } else if (newAccountBalance < MINIMUM_BALANCE) {
            throw new InsufficientFundsException("Withdrawal not allowed: balance is at minimum");
        }

        super.setBalance(newAccountBalance);
    }

    public double calculateInterest() {
        return super.getBalance() * this.INTEREST_RATE;
    }

    public AccountType getAccountType() {
        return AccountType.SAVINGS;
    }

    public double getINTEREST_RATE() {
        return this.INTEREST_RATE;
    }

    public double getMINIMUM_BALANCE() {
        return this.MINIMUM_BALANCE;
    }
}
