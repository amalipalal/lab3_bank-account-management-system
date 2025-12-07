package models;

import models.exceptions.InvalidAmountException;
import models.exceptions.OverdraftExceededException;
import org.junit.jupiter.api.*;

public class CheckedAccountTest {

    private static Customer regularCustomer;
    private static Customer premiumCustomer;

    private CheckingAccount regularChecked;
    private CheckingAccount premiumChecked;

    @BeforeAll
    static void setupCustomers() {
        regularCustomer = new RegularCustomer("Palal", 21, "+233599968996", "somewhere");
        premiumCustomer = new PremiumCustomer("Asare", 12, "+233123456789", "anywhere");
    }

    @BeforeEach
    void setupAccounts() {
        double initialBalance = 1000;
        String accountState = "active";

        regularChecked = new CheckingAccount("ACC001", regularCustomer, initialBalance, accountState);
        premiumChecked = new CheckingAccount("ACC002", premiumCustomer, initialBalance, accountState);
    }

    @Test
    @DisplayName("Should throw InvalidAmountException for negative withdrawal")
    public void validateWithdrawalAmount() {
        double withdrawalAmount = -50.49;

        Assertions.assertThrows(InvalidAmountException.class, () -> {
            regularChecked.withdraw(withdrawalAmount);
            premiumChecked.withdraw(withdrawalAmount);
        });
    }

    @Test
    @DisplayName("Should throw InsufficientFundsException for balance < -$1000")
    public void enforceOverdraftLimit() {
        // Overdraft limit here is $1000 and account has a current balance of $1000
        double withdrawalAmount = 2000;

        // Processing the transaction before overdraft limit has/can be reached
        Assertions.assertDoesNotThrow(() -> {
            regularChecked.withdraw(withdrawalAmount);
            premiumChecked.withdraw(withdrawalAmount);
        });

        // Processing the transaction after overdraft limit has been reached
        Assertions.assertThrows(OverdraftExceededException.class, () -> {
            regularChecked.withdraw(withdrawalAmount);
            premiumChecked.withdraw(withdrawalAmount);
        });
    }

    @Test
    @DisplayName("Should update balance correctly after valid withdrawal")
    public void processAllowedWithdrawal() {
        double withdrawalAmount = 500;

        Assertions.assertDoesNotThrow(() -> {
            regularChecked.withdraw(withdrawalAmount);
            premiumChecked.withdraw(withdrawalAmount);
        });

        double expectedBalance = 500;
        double actualRegularBalance = regularChecked.getBalance();
        double actualPremiumBalance = premiumChecked.getBalance();

        Assertions.assertEquals(expectedBalance, actualRegularBalance);
        Assertions.assertEquals(expectedBalance, actualPremiumBalance);
    }

    @Test
    @DisplayName("Should throw InvalidAmountException for negative deposit")
    public void validateDepositAmount() {
        double depositAmount = -409.56;

        Assertions.assertThrows(InvalidAmountException.class, () -> {
            regularChecked.deposit(depositAmount);
            premiumChecked.deposit(depositAmount);
        });
    }

    @Test
    @DisplayName("Should update balance correctly after valid deposit")
    public void processAllowedDeposit() {
        double depositAmount = 500;

        Assertions.assertDoesNotThrow(() -> {
            regularChecked.deposit(depositAmount);
            premiumChecked.deposit(depositAmount);
        });

        double expectedBalance = 1500;
        double actualRegularBalance = regularChecked.getBalance();
        double actualPremiumBalance = premiumChecked.getBalance();

        Assertions.assertEquals(expectedBalance, actualRegularBalance);
        Assertions.assertEquals(expectedBalance, actualPremiumBalance);
    }
}