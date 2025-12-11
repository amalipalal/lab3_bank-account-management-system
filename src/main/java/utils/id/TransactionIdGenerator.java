package utils.id;

import interfaces.AutoIdGenerator;

import java.text.DecimalFormat;

public class TransactionIdGenerator implements AutoIdGenerator {
    private final DecimalFormat FORMATTER = new DecimalFormat("000");
    private int transactionCounter;

    public TransactionIdGenerator() { this.transactionCounter = 0; }

    @Override
    public String generateId() {
        this.transactionCounter += 1;
        String idString = FORMATTER.format(transactionCounter);
        return "TXN" + idString;
    }

    @Override
    public int getCounter() {
        return this.transactionCounter;
    }

    @Override
    public int extractIndex(String transactionNumber) {
        int startIndex = transactionNumber.length() - 3;
        String indexString = transactionNumber.substring(startIndex);

        try {
            return Integer.parseInt(indexString);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Transaction number format is invalid: " + transactionNumber);
        }
    }

    @Override
    public void setIdCounter(int count) {
        this.transactionCounter = count;
    }
}
