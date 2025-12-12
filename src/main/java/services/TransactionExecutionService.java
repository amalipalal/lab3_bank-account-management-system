package services;

import models.ConfirmTransactionTask;
import utils.ThreadErrorCollector;
import models.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TransactionExecutionService {
    private final ExecutorService executorService;
    private final BankingService bankingService;
    private final ThreadErrorCollector errorCollector;

    public TransactionExecutionService(int poolSize, BankingService bankingService, ThreadErrorCollector errorCollector) {
        this.executorService = Executors.newFixedThreadPool(poolSize);;
        this.bankingService = bankingService;
        this.errorCollector = errorCollector;
    }

    public void submitTransactions(List<Transaction> transactions) {
        List<Future<?>> futures = new ArrayList<>();

        transactions.forEach(transaction -> {
            ConfirmTransactionTask task = new ConfirmTransactionTask(bankingService,transaction, errorCollector);
            Future<?> future = executorService.submit(task);
            futures.add(future);
        });

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                // The task already added the error to errorCollector
            }
        }

        if (errorCollector.hasErrors()) {
            errorCollector.showErrors();
            errorCollector.clearErrors();
        }
    }

    public int getErrorCount() {
        return this.errorCollector.getPREVIOUS_ERROR_COUNT();
    }

    public void resetErrorCount() {
        this.errorCollector.resetCount();
    }

}
