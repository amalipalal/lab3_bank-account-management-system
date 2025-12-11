package handlers;

import interfaces.DataStorageService;
import models.Account;
import models.Transaction;
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
        handleSavingAccounts();
        handleSavingTransactions();
        System.out.println("File save completed successfully");
    }

    private void handleSavingAccounts() {
        System.out.println("Saving accounts data...");
        List<Account> accounts = bankingService.viewAllAccounts();
        try {
            dataStorageService.saveAccounts(accounts);
            System.out.println("Accounts saved successfully");
        } catch (IOException e) {
            DisplayUtil.displayNotice("Could not save accounts. Try again later");
        }
    }

    private void handleSavingTransactions() {
        System.out.println("Saving transactions data...");
        List<Transaction> transactions = bankingService.viewAllTransactions();
        try {
            dataStorageService.saveTransactions(transactions);
            System.out.println("Transactions saved successfully");
        } catch (IOException e) {
            DisplayUtil.displayNotice("Could not save transactions. Try again later.");
        }
    }
}
