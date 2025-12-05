import models.Account;
import models.CheckingAccount;
import models.RegularCustomer;
import models.SavingsAccount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.AccountManager;
import services.exceptions.AccountNotFoundException;

class AccountManagerTest {

    private AccountManager accountManager;

    @BeforeEach
    void setup() {
        accountManager = new AccountManager();
    }

    @Test
    @DisplayName("Should return 0 when no account exists")
    void testGetTotalBalanceEmpty() {
        double expectedBalance = 0;
        double actualBalance = accountManager.getTotalBalance();

        Assertions.assertEquals(expectedBalance, actualBalance);
    }

    @Test
    @DisplayName("Should return correct sum for multiple accounts")
    void testGetTotalBalanceMultipleAccounts() {
        var regularCustomer = new RegularCustomer("Palal", 21, "+233599968996", "somewhere");

        Account savings = new SavingsAccount(regularCustomer, 100.1, "active");
        Account checking = new CheckingAccount(regularCustomer, 100.1, "active");

        accountManager.addAccount(savings);
        accountManager.addAccount(checking);

        double expected = 200.2;
        double actual = accountManager.getTotalBalance();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should update total balance after deposit or withdrawal")
    void testGetTotalBalanceAfterTransaction() throws Exception{
        var regularCustomer = new RegularCustomer("Palal", 21, "+233599968996", "somewhere");
        Account savingsAccount = new SavingsAccount(regularCustomer, 500.1, "active");

        accountManager.addAccount(savingsAccount);

        savingsAccount.deposit(500);
        savingsAccount.withdraw(40);

        double expected = 960.1;
        double actual = accountManager.getTotalBalance();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should return correct account when account number is valid")
    void testFindAccountFound() throws AccountNotFoundException {
        var regularCustomer = new RegularCustomer("Palal", 21, "+233599968996", "somewhere");
        Account savingsAccount = new SavingsAccount(regularCustomer, 500.1, "active");
        accountManager.addAccount(savingsAccount);

        Account found = accountManager.findAccount(savingsAccount.getAccountNumber());

        Assertions.assertEquals(savingsAccount, found);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException for non-existent account")
    void testFindAccountInvalid() {
        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            accountManager.findAccount("ACC999");
        });
    }
}