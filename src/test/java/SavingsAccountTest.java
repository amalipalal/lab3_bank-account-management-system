import models.Customer;
import models.PremiumCustomer;
import models.RegularCustomer;
import models.SavingsAccount;
import models.exceptions.InsufficientFundsException;
import models.exceptions.InvalidAmountException;
import org.junit.jupiter.api.*;

public class SavingsAccountTest {

    private static Customer regularCustomer;
    private static Customer premiumCustomer;

    private SavingsAccount regularSavings;
    private SavingsAccount premiumSavings;

    @BeforeAll
    static void setupCustomers() {
        regularCustomer = new RegularCustomer("Palal", 21, "+233599968996", "somewhere");
        premiumCustomer = new PremiumCustomer("Asare", 12, "+233123456789", "anywhere");
    }

    @BeforeEach
    void setupAccounts() {
        double initialBalance = 1000;
        String accountState = "active";

        regularSavings = new SavingsAccount(regularCustomer, initialBalance, accountState);
        premiumSavings = new SavingsAccount(premiumCustomer, initialBalance, accountState);
    }

    @Test
    @DisplayName("Should throw InvalidAmountException for negative withdrawal")
    public void validateWithdrawalAmount() {
        double withdrawalAmount = -50.49;

        Assertions.assertThrows(InvalidAmountException.class, () -> {
            regularSavings.withdraw(withdrawalAmount);
            premiumSavings.withdraw(withdrawalAmount);
        });
    }

    @Test
    @DisplayName("Should throw InsufficientFundsException for balance < $500")
    public void enforceMinimumBalance() {
        double withdrawalAmount = 500.99;

        Assertions.assertThrows(InsufficientFundsException.class, () -> {
            regularSavings.withdraw(withdrawalAmount);
        });
        Assertions.assertThrows(InsufficientFundsException.class, () -> {
            premiumSavings.withdraw(withdrawalAmount);
        });
    }

    @Test
    @DisplayName("Should update balance correctly after valid withdrawal")
    public void processAllowedWithdrawal() {
        double withdrawalAmount = 500;

        Assertions.assertDoesNotThrow(() -> {
            regularSavings.withdraw(withdrawalAmount);
            premiumSavings.withdraw(withdrawalAmount);
        });

        double expectedBalance = 500;
        double actualRegularBalance = regularSavings.getBalance();
        double actualPremiumBalance = premiumSavings.getBalance();

        Assertions.assertEquals(expectedBalance, actualRegularBalance);
        Assertions.assertEquals(expectedBalance, actualPremiumBalance);
    }

    @Test
    @DisplayName("Should throw InvalidAmountException for negative deposit")
    public void validateDepositAmount() {
        double depositAmount = -409.56;

        Assertions.assertThrows(InvalidAmountException.class, () -> {
            regularSavings.deposit(depositAmount);
            premiumSavings.deposit(depositAmount);
        });
    }

    @Test
    @DisplayName("Should update balance correctly after valid deposit")
    public void processAllowedDeposit() {
        double depositAmount = 500;

        Assertions.assertDoesNotThrow(() -> {
            regularSavings.deposit(depositAmount);
            premiumSavings.deposit(depositAmount);
        });

        double expectedBalance = 1500;
        double actualRegularBalance = regularSavings.getBalance();
        double actualPremiumBalance = premiumSavings.getBalance();

        Assertions.assertEquals(expectedBalance, actualRegularBalance);
        Assertions.assertEquals(expectedBalance, actualPremiumBalance);
    }
}
