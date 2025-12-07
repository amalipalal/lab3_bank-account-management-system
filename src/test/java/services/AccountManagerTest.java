package services;

import models.Account;
import models.RegularCustomer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import services.exceptions.AccountNotFoundException;
import utils.id.AccountIdGenerator;

import static org.mockito.Mockito.when;

class AccountManagerTest {

    @Mock
    private AccountIdGenerator accountIdGenerator;

    @InjectMocks
    private AccountManager accountManager;

    @BeforeEach
    void setup() { MockitoAnnotations.openMocks(this); }

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

        when(accountIdGenerator.generateId()).thenReturn("ACC001").thenReturn("ACC002");

        Account savings = accountManager.createSavingsAccount(regularCustomer, 100.1);
        Account checking = accountManager.createCheckingAccount(regularCustomer, 100.1);

        when(accountIdGenerator.getCounter()).thenReturn(1).thenReturn(2);

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
        when(accountIdGenerator.generateId()).thenReturn("ACC001");
        Account savingsAccount = accountManager.createSavingsAccount(regularCustomer, 500.1);

        when(accountIdGenerator.getCounter()).thenReturn(1);
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
        when(accountIdGenerator.generateId()).thenReturn("ACC001");
        Account savingsAccount = accountManager.createSavingsAccount(regularCustomer, 500.1);

        when(accountIdGenerator.getCounter()).thenReturn(1);
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