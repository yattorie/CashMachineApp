package atm.app;

import atm.service.CardService;
import atm.service.Service;
import atm.account.Account;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ATMApp {
    private final Scanner scanner = new Scanner(System.in);
    private final CardService cardService = new CardService();

    public void run() {
        System.out.println("Welcome to the ATM!");
        System.out.println("Enter card number:");
        String cardNumber = scanner.next();
        Pattern cardNumberPattern = Pattern.compile("\\d{4}-\\d{4}-\\d{4}-\\d{4}");

        if (!cardNumberPattern.matcher(cardNumber).matches()) {
            System.out.println("Invalid card number format");
            return;
        }

        boolean cardExists = false;
        try {
            List<String[]> dataList = cardService.readDataFromFile();

            for (String[] data : dataList) {
                if (data[0].equals(cardNumber)) {
                    cardExists = true;
                    break;
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Error reading card data: " + e.getMessage());
            return;
        }

        if (!cardExists) {
            System.out.println("Card not found");
            return;
        }

        String pinCode = "";
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Enter pin code:");
            pinCode = scanner.next();

            if (pinCode.length() != 4) {
                System.out.println("Pin code must be 4 digits");
                continue;
            }

            isValid = cardService.validate(cardNumber, pinCode);
            if (!isValid) {
                System.out.println("Invalid pin code. Please try again.");
            }
        }

        Account account = new Account(cardNumber, pinCode);
        Service atmService = new Service(account);

        while (true) {
            System.out.println("1 - Check card balance");
            System.out.println("2 - Withdraw funds");
            System.out.println("3 - Deposit funds");
            System.out.println("4 - View mini statement");
            System.out.println("5 - Exit");
            System.out.print("Choose an operation: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    atmService.viewBalance();
                    break;
                case 2:
                    handleWithdrawal(atmService);
                    break;
                case 3:
                    handleDeposit(atmService);
                    break;
                case 4:
                    atmService.viewMiniStatement();
                    break;
                case 5:
                    System.out.println("Please pick up your card");
                    System.out.println("Exiting system");
                    return;
                default:
                    System.out.println("Invalid operation choice");
            }
        }
    }

    private void handleWithdrawal(Service atmService) {
        System.out.print("Enter amount to withdraw: ");
        if (scanner.hasNextDouble()) {
            double withdrawAmount = scanner.nextDouble();
            atmService.withdrawAmount(withdrawAmount);
        } else {
            System.out.println("Invalid input. Please enter a positive number.");
            scanner.next();
        }
    }

    private void handleDeposit(Service atmService) {
        System.out.print("Enter amount to deposit: ");
        if (scanner.hasNextDouble()) {
            double depositAmount = scanner.nextDouble();
            atmService.depositAmount(depositAmount);
        } else {
            System.out.println("Invalid input. Please enter a positive number.");
            scanner.next();
        }
    }
}
