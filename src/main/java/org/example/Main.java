package org.example;

import org.example.dao.DatabaseInitializer;
import org.example.dao.UserDao;
import org.example.ui.Authentication;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();
        UserDao userDao = new UserDao();
        try (Scanner scanner = new Scanner(System.in)) {
            new Authentication(scanner, userDao).start();
        }
    }
}
