package atm.service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CardService {
    private static final String DATA_FILE = "src/atm/data/card_data.txt";
    private static final int MAX_ATTEMPTS = 3;
    private static final int BLOCK_DURATION_HOURS = 24;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public boolean validate(String cardNumber, String pinCode) {
        boolean isValid = false;
        boolean cardExists = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            StringBuilder fileContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(" ");
                if (data[0].equals(cardNumber)) {
                    cardExists = true;
                    int failedAttempts = Integer.parseInt(data[3]);
                    LocalDateTime blockTimestamp = parseDateTime(data[4]);
                    LocalDateTime currentTime = LocalDateTime.now();

                    if (failedAttempts >= MAX_ATTEMPTS) {
                        if (blockTimestamp != null && currentTime.isAfter(blockTimestamp)) {
                            data[3] = "0";
                            data[4] = "0";
                            failedAttempts = 0;
                        } else {
                            System.out.println("Card is blocked. Try again later.");
                            return false;
                        }
                    }

                    if (data[1].equals(pinCode)) {
                        System.out.println("Card validation successful");
                        data[3] = "0";
                        data[4] = "0";
                        isValid = true;
                    } else {
                        failedAttempts++;
                        data[3] = String.valueOf(failedAttempts);
                        if (failedAttempts >= MAX_ATTEMPTS) {
                            LocalDateTime unblockTime = currentTime.plusHours(BLOCK_DURATION_HOURS);
                            data[4] = unblockTime.format(DATE_TIME_FORMATTER);
                            System.out.println("Maximum attempts reached. Card is blocked for 24 hours.");
                        } else {
                            System.out.println("Invalid PIN. Attempts left: " + (MAX_ATTEMPTS - failedAttempts));
                        }
                    }

                    fileContent.append(String.join(" ", data)).append("\n");
                } else {
                    fileContent.append(line).append("\n");
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
                writer.write(fileContent.toString());
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading card data", e);
        }

        if (!cardExists) {
            System.out.println("Card not found");
            return false;
        }

        return isValid;
    }

    public List<String[]> readDataFromFile() {
        List<String[]> dataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dataList.add(line.split(" "));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading card data", e);
        }
        return dataList;
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        try {
            if (dateTimeString.equals("0")) {
                return null;
            }
            return LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(dateTimeString + "T00:00:00");
        }
    }
}
