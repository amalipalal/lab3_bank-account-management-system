package models;

import config.AppConfig;
import models.enums.AccountType;
import models.exceptions.InvalidAmountException;
import models.exceptions.OverdraftExceededException;
import utils.DisplayUtil;

public class CheckingAccount extends Account {
    private final double OVERDRAFT_LIMIT = AppConfig.OVERDRAFT_LIMIT_CHECKING_ACCOUNT;
    private double monthlyFee;

    public CheckingAccount(String accountNumber, Customer customer, double balance, String status) {
        super(accountNumber, customer, balance, status);
        this.monthlyFee = 10;
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
        String status = DisplayUtil.formatStatus(this.getStatus());

        return String.format(columnFormat,
                accountNumber, customerName, accountType, balance, status);
    }

    private String getExtraRowDisplay(String columnFormat) {
        String monthlyFee = DisplayUtil.displayAmount(this.monthlyFee);
        String overdraftLimit = DisplayUtil.displayAmount(this.OVERDRAFT_LIMIT);

        String overDraftDisplay = "Overdraft Limit: " + overdraftLimit;
        String monthlyFeeDisplay = "Monthly Fee: " + monthlyFee;

        return String.format(columnFormat, "", overDraftDisplay, monthlyFeeDisplay, "", "");
    }

    @Override
    public String displayNewAccountDetails() {
        Customer customer = getCustomer();
        String monthlyFeeMetadata = DisplayUtil.generateMonthlyFeeMetadata(this);

        return String.format(
                DisplayUtil.NEW_CHECKING_ACCOUNT_FORMAT,
                getAccountNumber(),
                customer.displayCustomerDetails(),
                getAccountType(),
                DisplayUtil.displayAmount(getBalance()),
                DisplayUtil.displayAmount(getOVERDRAFT_LIMIT()),
                DisplayUtil.displayAmount(getMonthlyFee()), monthlyFeeMetadata,
                DisplayUtil.formatStatus(getStatus())
        );
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.CHECKING;
    }

    @Override
    public void withdraw(double amount) throws OverdraftExceededException{
        if(amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive");
        }

        double currentAccountBalance = super.getBalance();
        double newAccountBalance = currentAccountBalance - amount;

        if(newAccountBalance < -this.OVERDRAFT_LIMIT) {
            throw new OverdraftExceededException("Withdrawal not allowed: overdraft limit is exceeded");
        }

        super.setBalance(newAccountBalance);
    }

    @Override
    public String toCsv() {
        return String.join(",",
                this.getAccountType().toString(),
                super.getAccountNumber(),
                super.getCustomer().toCsv(),
                String.valueOf(super.getBalance()),
                super.getStatus(),
                String.valueOf(this.monthlyFee)
        );
    }

    public void applyMonthlyFee() throws OverdraftExceededException{
        double newAccountBalance = super.getBalance() - this.monthlyFee;
        if(newAccountBalance < -this.OVERDRAFT_LIMIT) {
            throw new OverdraftExceededException("Monthly fee cannot be applied: overdraft limit exceeded");
        }
        super.setBalance(newAccountBalance);
    }

    public double getOVERDRAFT_LIMIT() {
        return this.OVERDRAFT_LIMIT;
    }

    public double getMonthlyFee() {
        return this.monthlyFee;
    }

    public void setMonthlyFee(double fee) {
        this.monthlyFee = fee;
    }
}
