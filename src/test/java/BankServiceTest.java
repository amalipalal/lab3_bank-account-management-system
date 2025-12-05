import models.Account;
import models.CheckingAccount;
import models.Customer;
import models.Transaction;
import models.enums.TransactionType;
import models.exceptions.InsufficientFundsException;
import models.exceptions.OverdraftExceededException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import services.AccountManager;
import services.BankingService;
import services.TransactionManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BankServiceTest {

    @Mock
    private AccountManager accountManager;

    @Mock
    private TransactionManager transactionManager;

    @InjectMocks
    private BankingService bankingService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should deposit in account and add transaction")
    void testConfirmTransactionDeposit() throws OverdraftExceededException, InsufficientFundsException {
        Account account = mock(Account.class);
        Transaction deposit = new Transaction(
                TransactionType.DEPOSIT, "ACC001", 200, 700);

        bankingService.confirmTransaction(account, deposit);

        verify(account).deposit(200);
        verify(transactionManager).addTransaction(deposit);
    }

    @Test
    @DisplayName("Should withdraw from account and transaction")
    void testConfirmTransactionWithdrawal() throws Exception {
        Account account = mock(Account.class);
        Transaction withdrawal = new Transaction(
                TransactionType.WITHDRAWAL, "ACC001", 200, 700);

        bankingService.confirmTransaction(account, withdrawal);

        verify(account).withdraw(200);
        verify(transactionManager).addTransaction(withdrawal);
    }

    @Test
    @DisplayName("Should create a savings account for a customer")
    void testCreateSavingsAccount() {
        Customer customer = mock(Customer.class);

        bankingService.createSavingsAccount(customer);

        verify(accountManager).addAccount(any());
    }

    @Test
    @DisplayName("Should create a checking account for a customer")
    void testCreateCheckingAccount() {
        Customer customer = mock(Customer.class);

        bankingService.createCheckingAccount(customer);

        verify(accountManager).addAccount(any());
    }

    @Test
    @DisplayName("Should create correct withdrawal transactions")
    void testProcessWithdrawal() {
        Customer customer = mock(Customer.class);
        Account account = new CheckingAccount(customer, 1000, "active");

        Transaction transaction = bankingService.processWithdrawal(account, 20);

        Assertions.assertEquals(TransactionType.WITHDRAWAL, transaction.getTransactionType());
        Assertions.assertEquals(account.getAccountNumber(), transaction.getAccountNumber());
        Assertions.assertEquals(980, transaction.getBalanceAfter());
        Assertions.assertEquals(20, transaction.getAmount());

        Assertions.assertEquals(1000, account.getBalance());
    }

    @Test
    @DisplayName("Should create correct deposit transactions")
    void testProcessDeposit() {
        Customer customer = mock(Customer.class);
        Account account = new CheckingAccount(customer, 1000, "active");

        Transaction transaction = bankingService.processDeposit(account, 1000);

        Assertions.assertEquals(TransactionType.DEPOSIT, transaction.getTransactionType());
        Assertions.assertEquals(account.getAccountNumber(), transaction.getAccountNumber());
        Assertions.assertEquals(2000, transaction.getBalanceAfter());
        Assertions.assertEquals(1000, transaction.getAmount());

        Assertions.assertEquals(1000, account.getBalance());
    }
}
