package interfaces;

import models.enums.TransactionType;
import models.exceptions.InsufficientFundsException;
import models.exceptions.OverdraftExceededException;

/**
 * Represents an entity capable of processing financial transactions.
 */
public interface Transactable {

    /**
     * Processes a transaction of the specified type and amount.
     *
     * @param amount The amount to process
     * @param type They type of transaction (DEPOSIT or WITHDRAWAL)
     * @return True if the transaction was processed successfully
     * @throws OverdraftExceededException if the transaction exceeds allowed overdraft
     * @throws InsufficientFundsException if there are insufficient funds for withdrawal
     */
    boolean processTransaction(double amount, TransactionType type) throws OverdraftExceededException,
            InsufficientFundsException;
}
