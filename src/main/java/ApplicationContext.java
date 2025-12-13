import config.AppConfig;
import handlers.AccountFlowHandler;
import handlers.FileFlowHandler;
import handlers.TransactionFlowHandler;
import interfaces.DataStorageService;
import models.Account;
import models.Transaction;
import services.*;
import utils.DisplayUtil;
import utils.InputReader;
import utils.ThreadErrorCollector;
import utils.id.AccountIdGenerator;
import utils.id.TransactionIdGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ApplicationContext {
    public final DataStorageService storage;
    public final BankingService bankingService;
    public final TransactionExecutionService executionService;
    public final InputReader input;
    public final AccountFlowHandler accountFlowHandler;
    public final TransactionFlowHandler transactionFlowHandler;
    public final FileFlowHandler fileFlowHandler;

    public ApplicationContext() {
        this.storage = new FileStorageService(
                AppConfig.ACC_STORE_FILE_NAME,
                AppConfig.TRANS_STORE_FILE_NAME
        );
        Map<String, Account> savedAccounts = safeLoad(storage::loadAccounts, new HashMap<>());
        Map<String, List<Transaction>> savedTransactions = safeLoad(storage::loadTransactions, new HashMap<>());

        this.bankingService = new BankingService(
                new AccountManager(new AccountIdGenerator(), savedAccounts),
                new TransactionManager(new TransactionIdGenerator(), savedTransactions)
        );
        this.executionService = new TransactionExecutionService(3, bankingService, new ThreadErrorCollector());
        this.input = new InputReader(new Scanner(System.in));
        this.accountFlowHandler = new AccountFlowHandler(bankingService, input);
        this.transactionFlowHandler = new TransactionFlowHandler(bankingService, executionService, input);
        this.fileFlowHandler = new FileFlowHandler(bankingService, storage, input);
    }

    private <T> T safeLoad(CheckedSupplier<T> supplier, T fallback) {
        try {
            return supplier.get();
        } catch (Exception e) {
            DisplayUtil.displayNotice(e.getMessage());
            return fallback;
        }
    }

    @FunctionalInterface
    interface CheckedSupplier<T> {
        T get() throws Exception;
    }
}
