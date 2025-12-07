package services;

import models.Transaction;
import models.enums.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import utils.id.TransactionIdGenerator;

public class TransactionManagerTest {

    @Mock
    private TransactionIdGenerator transactionIdGenerator;

    @InjectMocks
    private TransactionManager transactionManager;

    @BeforeEach
    void setup()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should have 0 transactions on initialization")
    void testTransactionCountEmpty() {
        int expected = 0;
        int actual = transactionManager.getTransactionCount();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should increase transaction count on transactions")
    void testTransactionCountMultipleTransactions() {
        Transaction deposit  = new Transaction(
                "TXN001", TransactionType.DEPOSIT, "ACC001", 1000, 2000);
        Transaction withdrawal = new Transaction(
                "TXN002", TransactionType.WITHDRAWAL, "ACC001", 50, 1500);

        transactionManager.addTransaction(deposit);
        transactionManager.addTransaction(withdrawal);

        int expected = 2;
        int actual = transactionManager.getTransactionCount();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should have 0 total deposits on initialization")
    void testTotalDepositsEmpty() {
        double expected = 0;
        double actual = transactionManager.calculateTotalDeposits("ACC001");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should have 0 total withdrawal on initialization")
    void testTotalWithdrawalsEmpty() {
        double expected = 0;
        double actual = transactionManager.calculateTotalWithdrawals("ACC001");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should update total transactions after transacting")
    void testTransactionTotalAfterTransaction() {
        Transaction deposit  = new Transaction(
                "TXN001", TransactionType.DEPOSIT, "ACC001", 1000, 2000);
        Transaction withdrawal = new Transaction(
                "TXN002", TransactionType.WITHDRAWAL, "ACC001", 50, 1500);
        Transaction secondDeposit = new Transaction(
                "TXN003", TransactionType.WITHDRAWAL, "ACC002", 500, 500);

        transactionManager.addTransaction(deposit);
        transactionManager.addTransaction(withdrawal);
        transactionManager.addTransaction(secondDeposit);

        double expectedTotalWithdrawals = 50;
        double expectedTotalDeposits = 1000;
        double actualTotalWithdrawals = transactionManager.calculateTotalWithdrawals("ACC001");
        double actualTotalDeposits = transactionManager.calculateTotalDeposits("ACC001");

        Assertions.assertEquals(expectedTotalWithdrawals, actualTotalWithdrawals);
        Assertions.assertEquals(expectedTotalDeposits, actualTotalDeposits);
    }
}