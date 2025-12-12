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

/**
 * Handles concurrent execution of confirmed banking transactions using a fixed thread pool.
 * This service submits transaction confirmation tasks, waits for their completion,
 * aggregates thread-safe error messages, and provides controlled shutdown of the executor.
 */
public class TransactionExecutionService {
    private final ExecutorService executorService;
    private final BankingService bankingService;
    private final ThreadErrorCollector errorCollector;

    public TransactionExecutionService(
            int poolSize,
            BankingService bankingService,
            ThreadErrorCollector errorCollector
    ) {
        this.executorService = Executors.newFixedThreadPool(poolSize);;
        this.bankingService = bankingService;
        this.errorCollector = errorCollector;
    }

    /**
     * Submits all given transactions for concurrent execution
     * prints any errors collected during processing.
     *
     * @param transactions the list of transactions to execute
     */
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

    /**
     * Returns the number of errors recorded from the most
     * recent batch of executed transactions
     *
     * @return number of errors previously collected
     */
    public int getErrorCount() {
        return this.errorCollector.getPREVIOUS_ERROR_COUNT();
    }

    /**
     * Resets the internal error counter to zero.
     * Does not clear the actual stored messages.
     */
    public void resetErrorCount() {
        this.errorCollector.resetCount();
    }

    /**
     * Shuts down the executor service gracefully. No new tasks will be accepted.
     * The method waits briefly for all running tasks to finish
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if(!executorService.awaitTermination(5, TimeUnit.SECONDS))
                executorService.shutdown();
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

}
