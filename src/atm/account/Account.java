package atm.account;

import java.io.*;
import java.util.*;

public class Account {
    private double balance;
    private final String cardNumber;
    private final String pinCode;
    private static final String DATA_FILE = "src/atm/data/card_data.txt";

    public Account(String cardNumber, String pinCode) {
        this.cardNumber = cardNumber;
        this.pinCode = pinCode;
        this.balance = readInitialBalance();
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 1000000) {
            balance += amount;
            updateBalance();
        } else {
            throw new IllegalArgumentException("Deposit limit exceeded (1,000,000 rubles)");
        }
    }

    public void withdraw(double amount) {
        if (amount % 50 != 0) {
            throw new IllegalArgumentException("Amount must be a multiple of 50");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        balance -= amount;
        updateBalance();
    }

    private double readInitialBalance() {
        List<String[]> dataList = readDataFromFile();
        for (String[] data : dataList) {
            if (data[0].equals(cardNumber) && data[1].equals(pinCode)) {
                return Double.parseDouble(data[2]);
            }
        }
        return 0.0;
    }

    private void updateBalance() {
        List<String[]> dataList = readDataFromFile();
        for (String[] data : dataList) {
            if (data[0].equals(cardNumber) && data[1].equals(pinCode)) {
                data[2] = String.valueOf(balance);
                break;
            }
        }
        writeDataToFile(dataList);
    }

    private List<String[]> readDataFromFile() {
        List<String[]> dataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dataList.add(line.split(" "));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading balance data", e);
        }
        return dataList;
    }

    private void writeDataToFile(List<String[]> dataList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (String[] data : dataList) {
                writer.write(String.join(" ", data) + '\n');
            }
        } catch (IOException e) {
            throw new RuntimeException("Error updating balance data", e);
        }
    }
}
