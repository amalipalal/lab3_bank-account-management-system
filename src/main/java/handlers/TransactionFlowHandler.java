package handlers;

import models.Account;
import models.Transaction;
import models.exceptions.InsufficientFundsException;
import models.exceptions.OverdraftExceededException;
import services.BankingService;
import services.exceptions.AccountNotFoundException;
import utils.DisplayUtil;
import utils.InputReader;
import utils.ValidationUtil;

public class TransactionFlowHandler {

    private final BankingService bankingService;
    private final InputReader input;

    public TransactionFlowHandler(BankingService bankingService, InputReader input) {
        this.bankingService = bankingService;
        this.input = input;
    }

    public void handleTransactionFlow() {
        DisplayUtil.displayHeading("Process Transaction");

        System.out.println();

        Account customerAccount = findAccountOrNotify();
        if (customerAccount == null) return;

        System.out.println("Account Details:");
        DisplayUtil.displayAccountDetails(customerAccount);

        System.out.println();

        Transaction newTransaction = handleTransactionTypeFlow(customerAccount);

        handleTransactionConfirmation(customerAccount, newTransaction);
    }

    private Account findAccountOrNotify() {
        String accountNumber = this.input.readNonEmptyString("Enter Account Number", ValidationUtil::validateAccountNumber);
        try {
            return this.bankingService.getAccountByNumber(accountNumber);
        } catch (AccountNotFoundException e) {
            DisplayUtil.displayNotice(e.getMessage());
            return null;
        }
    }

    private Transaction handleTransactionTypeFlow(Account customerAccount) {
        System.out.println("Transaction type:");
        System.out.println("1. Deposit \n2. Withdrawal");
        System.out.println();

        int transactionType = this.input.readInt("Select type (1-2)", 1, 2);

        double transactionAmount = this.input.readDouble("Enter amount", 0);

        System.out.println();

        return transactionType == 1
                ? this.bankingService.processDeposit(customerAccount, transactionAmount)
                : this.bankingService.processWithdrawal(customerAccount, transactionAmount);
    }

    private void handleTransactionConfirmation(Account customerAccount, Transaction newTransaction) {
        DisplayUtil.displayHeading("Transaction Confirmation");

        DisplayUtil.displayTransaction(newTransaction);

        System.out.println();

        boolean isConfirmed = this.input.readYesOrNo("Confirm transaction? (Y/N)");

        if(isConfirmed) {
            try {
                this.bankingService.confirmTransaction(customerAccount, newTransaction);
                System.out.println("Transaction completed successful!");
            } catch (InsufficientFundsException | OverdraftExceededException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Transaction not confirmed. Aborting.");
        }
    }

    public void handleTransactionListingFlow() {
        DisplayUtil.displayHeading("View Transaction history");

        Account customerAccount = findAccountOrNotify();
        if(customerAccount == null) return;

        DisplayUtil.displayAccountDetails(customerAccount);

        Transaction[] customerTransactions = this.bankingService.getTransactionsByAccount(
                customerAccount.getAccountNumber());

        if (customerTransactions.length == 0) {
            DisplayUtil.displayNotice("No transactions recorded for this account.");
        } else {
            DisplayUtil.displayMultipleTransactions(customerTransactions);
            displayTransactionTotals(customerTransactions);
        }
    }

    private void displayTransactionTotals(Transaction[] transactions) {
        String accountNumber = transactions[0].getAccountNumber();

        double totalDeposits = this.bankingService.getTotalDeposit(accountNumber);
        double totalWithdrawals = this.bankingService.getTotalWithdrawals(accountNumber);

        double netChange = totalDeposits - totalWithdrawals;

        System.out.println("Total Transactions: " + transactions.length);
        System.out.println("Total Deposits: " + DisplayUtil.displayAmount(totalDeposits));
        System.out.println("Total Withdrawals: " + DisplayUtil.displayAmount(totalWithdrawals));
        System.out.println("Net Change: " + DisplayUtil.displayAmount(netChange));
    }
}
