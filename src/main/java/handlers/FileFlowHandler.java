package handlers;

import interfaces.DataStorageService;
import models.Account;
import services.BankingService;
import utils.DisplayUtil;
import utils.InputReader;

import java.io.IOException;
import java.util.List;

public class FileFlowHandler {

    private final BankingService bankingService;
    private final DataStorageService dataStorageService;
    private final InputReader input;


    public FileFlowHandler(BankingService bankingService, DataStorageService dataStorageService, InputReader input) {
        this.bankingService = bankingService;
        this.dataStorageService = dataStorageService;
        this.input = input;
    }

    public void handleSavingApplicationFlow() {
        DisplayUtil.displayNotice("Saving Application Data");

        System.out.println("Saving accounts data...");
        List<Account> accounts = bankingService.viewAllAccounts();
        try {
            dataStorageService.saveAccounts(accounts);
        } catch (IOException e) {
            DisplayUtil.displayNotice("Could not save application. Try again later");
        }

        System.out.println("Accounts saved successfully");
        System.out.println("File save completed successfully");
    }
}
