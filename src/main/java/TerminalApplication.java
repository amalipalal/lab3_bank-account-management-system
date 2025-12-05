import handlers.AccountFlowHandler;
import handlers.TransactionFlowHandler;
import services.AccountManager;
import services.TransactionManager;
import services.BankingService;
import utils.DataSeeder;
import utils.DisplayUtil;
import utils.InputReader;

import java.util.Scanner;

public class TerminalApplication {
    private static final BankingService BANKING_SERVICE = new BankingService(new AccountManager(), new TransactionManager());
    private static final InputReader INPUT = new InputReader(new Scanner(System.in));
    private static final AccountFlowHandler ACCOUNT_FLOW = new AccountFlowHandler(BANKING_SERVICE, INPUT);
    private static final TransactionFlowHandler TRANSACTION_FLOW = new TransactionFlowHandler(BANKING_SERVICE, INPUT);

    public static void start() {
        // Populate the program with already existing customer accounts
        // defined within seed method
        seedData();

        boolean userIsActive = true;

        while(userIsActive) {
            try {
                DisplayUtil.displayMainMenu();

                int userSelection = INPUT.readInt("Select an option (1-5)", 1, 5);
                System.out.println();

                userIsActive = handleMenuSelection(userSelection);

            } catch (Exception e) {
                DisplayUtil.displayNotice(e.getMessage());
            }
        }
    }

    public static void seedData() {
        DataSeeder seeder = new DataSeeder(BANKING_SERVICE);
        try {
            seeder.seed();
        } catch (Exception e) {
            DisplayUtil.displayNotice("Could Not start Application");
        }
    }

    public static boolean handleMenuSelection(int userSelection) {
        switch(userSelection) {
            case 1:
                ACCOUNT_FLOW.handleAccountCreationFlow();
                break;
            case 2:
                ACCOUNT_FLOW.handleAccountListingFlow();
                break;
            case 3:
                TRANSACTION_FLOW.handleTransactionFlow();
                break;
            case 4:
                TRANSACTION_FLOW.handleTransactionListingFlow();
                break;
            case 5:
                return false;
            default:
                DisplayUtil.displayNotice("Wrong number selection");
        }

        return true;
    }
}
