package utils;

import models.Customer;
import models.PremiumCustomer;
import models.RegularCustomer;
import services.BankingService;

public class DataSeeder {
    private final BankingService bankingService;

    public DataSeeder(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    public void seed() throws Exception{
        createSavingsAccount(
                new RegularCustomer("Alice Johnson", 28, "0101010101", "Accra"),
                1200
        );

        createCheckingAccount(
                new PremiumCustomer("Michael Mensah", 45, "0202020202", "Kumasi"),
                20000
        );

        createCheckingAccount(
                new RegularCustomer("Sarah Boateng", 34, "0303030303", "Tema"),
                850
        );

        createSavingsAccount(
                new PremiumCustomer("Kwame Frimpong", 50, "0404040404", "Cape Coast"),
                15000
        );

        createSavingsAccount(
                new RegularCustomer("John Doe", 22, "0505050505", "Takoradi"),
                750
        );
    }

    private void createSavingsAccount(Customer customer, double initialDeposit) throws Exception {
        var account = bankingService.createSavingsAccount(customer);
        var transaction = bankingService.processDeposit(account, initialDeposit);
        bankingService.confirmTransaction(account, transaction);
    }

    private void createCheckingAccount(Customer customer, double initialDeposit) throws Exception {
        var account = bankingService.createCheckingAccount(customer);
        var transaction = bankingService.processDeposit(account, initialDeposit);
        bankingService.confirmTransaction(account, transaction);
    }
}
