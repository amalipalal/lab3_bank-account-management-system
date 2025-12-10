import config.AppConfig;
import handlers.AccountFlowHandler;
import handlers.TestHandler;
import handlers.TransactionFlowHandler;
import interfaces.DataStorageService;
import models.Account;
import services.AccountManager;
import services.FileStorageService;
import services.TransactionManager;
import services.BankingService;
import utils.DataSeeder;
import utils.DisplayUtil;
import utils.InputReader;
import utils.id.AccountIdGenerator;
import utils.id.TransactionIdGenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TerminalApplication {
    private final DataStorageService dataStorageService;
    private final BankingService bankingService;
    private final InputReader input;
    private final AccountFlowHandler accountFlowHandler;
    private final TransactionFlowHandler transactionFlowHandler;
    private final TestHandler testHandler;

    public TerminalApplication() {
        this.dataStorageService = new FileStorageService(
                AppConfig.ACC_STORE_FILE_NAME,
                AppConfig.TRANS_STORE_FILE_NAME
        );
        Map<String, Account> savedAccounts = loadSavedAccounts(dataStorageService);

        this.bankingService = new BankingService(
                new AccountManager(new AccountIdGenerator(), savedAccounts),
                new TransactionManager(new TransactionIdGenerator())
        );
        this.input = new InputReader(new Scanner(System.in));
        this.accountFlowHandler = new AccountFlowHandler(bankingService, input);
        this.transactionFlowHandler = new TransactionFlowHandler(bankingService, input);
        this.testHandler = new TestHandler();
    }

    private Map<String, Account> loadSavedAccounts(DataStorageService store) {
        try {
            return store.loadAccounts();
        } catch (IOException e) {
            DisplayUtil.displayNotice(e.getMessage());
            return new HashMap<>();
        }
    }

    public void start() {
        // Populate the program with already existing customer accounts
        // defined within seed method
        seedData();

        boolean userIsActive = true;

        while(userIsActive) {
            try {
                DisplayUtil.displayMainMenu();

                int userSelection = input.readInt("Select an option (1-6)", 1, 6);
                System.out.println();

                userIsActive = handleMenuSelection(userSelection);

            } catch (Exception e) {
                DisplayUtil.displayNotice(e.getMessage());
            }
        }
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
                transactionFlowHandler.handleTransactionListingFlow();
                break;
            case 5:
                testHandler.run();
                break;
            case 6:
                return false;
            default:
                DisplayUtil.displayNotice("Wrong number selection");
        }

        return true;
    }
}
