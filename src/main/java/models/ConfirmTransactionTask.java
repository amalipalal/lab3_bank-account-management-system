package models;

import models.exceptions.InsufficientFundsException;
import models.exceptions.OverdraftExceededException;
import services.BankingService;
import services.exceptions.AccountNotFoundException;
import utils.DisplayUtil;
import utils.ThreadErrorCollector;

public class ConfirmTransactionTask implements Runnable{
    private final BankingService bankingService;
    private final Transaction transaction;
    private final ThreadErrorCollector errorCollector;


    public ConfirmTransactionTask(BankingService bankingService, Transaction transaction, ThreadErrorCollector errorCollector) {
        this.bankingService = bankingService;
        this.transaction = transaction;
        this.errorCollector = errorCollector;
    }

    @Override
    public void run() {
        try {
            Account account = bankingService.getAccountByNumber(transaction.getAccountNumber());
            bankingService.confirmTransaction(account, transaction);
            System.out.println(
                    "Thread:" + Thread.currentThread().getName() + " " + transaction.getTransactionType() + " " + DisplayUtil.displayAmount(transaction.getAmount()) + " to " + transaction.getAccountNumber());
        } catch (InsufficientFundsException | OverdraftExceededException | AccountNotFoundException e) {
            errorCollector.addError(e.getMessage());
        }
    }
}
