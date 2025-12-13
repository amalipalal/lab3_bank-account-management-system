import config.AppConfig;
import services.*;
import utils.DataSeeder;
import utils.DisplayUtil;

import java.util.HashMap;
import java.util.Map;

public class TerminalApplication {
    private final ApplicationContext context;
    private final Map<Integer, Runnable> commandMap = new HashMap<>();
    private boolean running = true;

    public TerminalApplication(ApplicationContext context) {
        this.context = context;
        registerCommands();
    }

    private void registerCommands() {
        this.commandMap.put(1, context.accountFlowHandler::handleAccountCreationFlow);
        this.commandMap.put(2, context.accountFlowHandler::handleAccountListingFlow);
        this.commandMap.put(3, context.transactionFlowHandler::handleTransactionFlow);
        this.commandMap.put(4, context.transactionFlowHandler::handleConcurrentTransactionFlow);
        this.commandMap.put(5, context.transactionFlowHandler::handleTransactionListingFlow);
        this.commandMap.put(6, context.fileFlowHandler::handleSavingApplicationFlow);

        this.commandMap.put(7, () -> {
            context.executionService.shutdown();
            this.running = false;
        });
    }

    public void start() {
        displayLoadMessages();

        // Populate the program with already existing customer accounts
        // defined within seed method
        seedData();

        while(this.running) {
            DisplayUtil.displayMainMenu();

            int userSelection = context.input.readInt("Select an option (1-7)", 1, 7);
            System.out.println();

            Runnable command = commandMap.get(userSelection);
            if(command == null) {
                DisplayUtil.displayNotice("Wrong number selection");
                continue;
            }

            try {
                command.run();
            } catch (Exception e) {
                DisplayUtil.displayNotice(e.getMessage());
            }
        }
    }

    private void displayLoadMessages() {
        int accountsCount = context.bankingService.viewAllAccounts().size();
        int transactionsCount = context.bankingService.viewAllTransactions().size();

        DisplayUtil.displayNotice(
                accountsCount + " accounts loaded from " + AppConfig.ACC_STORE_FILE_NAME);
        DisplayUtil.displayNotice(
                transactionsCount + " transactions loaded from " + AppConfig.TRANS_STORE_FILE_NAME);
    }

    public void seedData() {
        DataSeeder seeder = new DataSeeder(context.bankingService);
        try {
            seeder.seed();
        } catch (Exception e) {
            DisplayUtil.displayNotice("Could Not start Application");
        }
    }
}
