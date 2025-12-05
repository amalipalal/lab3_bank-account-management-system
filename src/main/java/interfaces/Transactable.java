package interfaces;

import models.enums.TransactionType;
import models.exceptions.InsufficientFundsException;
import models.exceptions.OverdraftExceededException;

public interface Transactable {
    public boolean processTransaction(double amount, TransactionType type) throws OverdraftExceededException,
            InsufficientFundsException;
}
