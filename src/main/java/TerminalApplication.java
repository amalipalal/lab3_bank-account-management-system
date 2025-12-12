import config.AppConfig;
import handlers.AccountFlowHandler;
import handlers.FileFlowHandler;
import handlers.TestHandler;
import handlers.TransactionFlowHandler;
import interfaces.DataStorageService;
import models.Account;
import utils.ThreadErrorCollector;
import models.Transaction;
import services.*;
import utils.DataSeeder;
import utils.DisplayUtil;
import utils.InputReader;
import utils.id.AccountIdGenerator;
import utils.id.TransactionIdGenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TerminalApplication {
    private final DataStorageService dataStorageService;
    private final BankingService bankingService;
    private final TransactionExecutionService executionService;
    private final InputReader input;
    private final AccountFlowHandler accountFlowHandler;
    private final TransactionFlowHandler transactionFlowHandler;
    private final FileFlowHandler fileFlowHandler;

    public TerminalApplication() {
        this.dataStorageService = new FileStorageService(
                AppConfig.ACC_STORE_FILE_NAME,
                AppConfig.TRANS_STORE_FILE_NAME
        );
        Map<String, Account> savedAccounts = loadSavedAccounts(dataStorageService);
        Map<String, List<Transaction>> savedTransactions = loadSavedTransactions(dataStorageService);

        this.bankingService = new BankingService(
                new AccountManager(new AccountIdGenerator(), savedAccounts),
                new TransactionManager(new TransactionIdGenerator(), savedTransactions)
        );
        this.executionService = new TransactionExecutionService(3, bankingService, new ThreadErrorCollector());
        this.input = new InputReader(new Scanner(System.in));
        this.accountFlowHandler = new AccountFlowHandler(bankingService, input);
        this.transactionFlowHandler = new TransactionFlowHandler(bankingService, executionService, input);
        this.fileFlowHandler = new FileFlowHandler(bankingService, dataStorageService, input);
    }

    private Map<String, Account> loadSavedAccounts(DataStorageService store) {
        try {
            return store.loadAccounts();
        } catch (IOException e) {
            DisplayUtil.displayNotice(e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<String, List<Transaction>> loadSavedTransactions(DataStorageService store) {
        try {
            return store.loadTransactions();
        } catch (IOException e) {
            DisplayUtil.displayNotice(e.getMessage());
            return new HashMap<>();
        }
    }

    public void start() {
        displayLoadMessages();

        // Populate the program with already existing customer accounts
        // defined within seed method
        seedData();

        boolean userIsActive = true;

        while(userIsActive) {
            try {
                DisplayUtil.displayMainMenu();

                int userSelection = input.readInt("Select an option (1-7)", 1, 7);
                System.out.println();

                userIsActive = handleMenuSelection(userSelection);

            } catch (Exception e) {
                DisplayUtil.displayNotice(e.getMessage());
            }
        }
    }

    private void displayLoadMessages() {
        int accountsCount = bankingService.viewAllAccounts().size();
        int transactionsCount = bankingService.viewAllTransactions().size();

        DisplayUtil.displayNotice(
                accountsCount + " accounts loaded from " + AppConfig.ACC_STORE_FILE_NAME);
        DisplayUtil.displayNotice(
                transactionsCount + " transactions loaded from " + AppConfig.TRANS_STORE_FILE_NAME);
    }

    public void seedData() {
        DataSeeder seeder = new DataSeeder(bankingService);
        try {
            seeder.seed();
        } catch (Exception e) {
            DisplayUtil.displayNotice("Could Not start Application");
        }
    }

    public boolean handleMenuSelection(int userSelection) {
        switch(userSelection) {
            case 1:
                accountFlowHandler.handleAccountCreationFlow();
                break;
            case 2:
                accountFlowHandler.handleAccountListingFlow();
                break;
            case 3:
                transactionFlowHandler.handleTransactionFlow();
                break;
            case 4:
                transactionFlowHandler.handleConcurrentTransactionFlow();
                break;
            case 5:
                transactionFlowHandler.handleTransactionListingFlow();
                break;
            case 6:
                fileFlowHandler.handleSavingApplicationFlow();
                break;
            case 7:
                executionService.shutdown();
                return false;
            default:
                DisplayUtil.displayNotice("Wrong number selection");
        }

        return true;
    }
}
