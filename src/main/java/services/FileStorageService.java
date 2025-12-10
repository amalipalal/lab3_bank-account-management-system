package services;

import interfaces.DataStorageService;
import models.*;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileStorageService implements DataStorageService {

    @Override
    public Map<String, Account> loadAccounts() throws IOException {
        return new HashMap<>();
    }

    @Override
    public void saveAccounts() throws IOException {

    }

    @Override
    public Map<String, List<Transaction>> loadTransactions() throws IOException {
        return new HashMap<>();
    }

    @Override
    public void saveTransactions() throws IOException {

    }
}