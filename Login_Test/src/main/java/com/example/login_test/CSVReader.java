package com.example.login_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public static List<User> readUsersFromCSV(InputStream inputStream) {
        List<User> users = new ArrayList<>();
        String line;

        // Check if the InputStream is null
        if (inputStream == null) {
            System.err.println("InputStream is null, resource not found.");
            return users; // Return empty list if resource not found
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // Validate the format before creating User
                if (values.length == 3) {
                    // Assume User constructor takes username, password, and role
                    users.add(new User(values[0].trim(), values[1].trim(), values[2].trim()));
                } else {
                    System.err.println("Invalid line format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }
}