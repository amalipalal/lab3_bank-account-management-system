package services;

import models.Transaction;
import models.enums.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import utils.id.TransactionIdGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionManagerTest {

    @Mock
    private TransactionIdGenerator transactionIdGenerator;

    private Map<String, List<Transaction>> transactions;

    private TransactionManager transactionManager;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        transactions = new HashMap<>();
        transactionManager = new TransactionManager(transactionIdGenerator, transactions);
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
        String time = "2025-12-10T08:45:12.345Z";
        Transaction deposit  = new Transaction(
                "TXN001",
                TransactionType.DEPOSIT,
                "ACC001",
                1000,
                2000,
                time);
        Transaction withdrawal = new Transaction(
                "TXN002",
                TransactionType.WITHDRAWAL,
                "ACC001",
                50,
                1500,
                time);

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
        String time = "2025-12-10T08:45:12.345Z";
        Transaction deposit  = new Transaction(
                "TXN001", TransactionType.DEPOSIT, "ACC001", 1000, 2000, time);
        Transaction withdrawal = new Transaction(
                "TXN002", TransactionType.WITHDRAWAL, "ACC001", 50, 1500, time);
        Transaction secondDeposit = new Transaction(
                "TXN003", TransactionType.WITHDRAWAL, "ACC002", 500, 500, time);

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