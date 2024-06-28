package atm.service;

import atm.account.Account;

import java.util.ArrayList;
import java.util.List;

public class Service {
    private final Account account;
    private final List<String> miniStatement = new ArrayList<>();

    public Service(Account account) {
        this.account = account;
    }

    public void viewBalance() {
        System.out.println("Available balance: " + account.getBalance());
    }

    public void withdrawAmount(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be a positive number");
            return;
        }
        try {
            account.withdraw(amount);
            miniStatement.add("Withdrawn: " + amount);
            System.out.println("Please take your cash: " + amount);
            viewBalance();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void depositAmount(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be a positive number");
            return;
        }
        try {
            account.deposit(amount);
            miniStatement.add("Deposited: " + amount);
            System.out.println(amount + " successfully deposited");
            viewBalance();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void viewMiniStatement() {
        for (String statement : miniStatement) {
            System.out.println(statement);
        }
    }
}
