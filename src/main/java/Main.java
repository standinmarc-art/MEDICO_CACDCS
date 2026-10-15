package com.scc;

import com.scc.dao.UserDao;
import com.scc.model.User;
import com.scc.ui.AdminMenu;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final UserDao userDao = new UserDao();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n==============================================");
            System.out.println(" CONDOMINIUM ACCESS CONTROL & DUES COLLECTION ");
            System.out.println("==============================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    handleLogin();
                    break;

                case "2":
                    handleRegistration();
                    break;

                case "3":
                    running = false;
                    System.out.println("Exiting application...");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        sc.close();
    }

    private static void handleRegistration() {
        System.out.println("\n========== USER REGISTRATION ==========");

        System.out.print("Enter new username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        System.out.println("\nSelect User Role:");
        System.out.println("1. CONDO ADMINISTRATOR");
        System.out.println("2. SECURITY GUARD");
        System.out.println("3. SERVICE / DELIVERY PERSONNEL");
        System.out.print("Choose role: ");

        String roleChoice = sc.nextLine();
        String role;

        switch (roleChoice) {
            case "1":
                role = "ADMIN";
                break;

            case "2":
                role = "SECURITY";
                break;

            case "3":
                role = "SERVICE";
                break;

            default:
                System.out.println("Invalid role selection!");
                return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        userDao.registerUser(username, password, role);
    }

    private static void handleLogin() {
        System.out.println("\n========== USER LOGIN ==========");

        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            System.out.println("Login failed. Please check your credentials.");
            return;
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + user.username());
        System.out.println("Role: " + user.roles());

        switch (user.roles()) {
            case "ADMIN":
                new AdminMenu(userDao, sc).display(user);
                break;

            case "SECURITY":
                displaySecurityMenu();
                break;

            case "SERVICE":
                displayServiceMenu();
                break;

            default:
                System.out.println("Account role not recognized.");
        }
    }

    private static void displaySecurityMenu() {
        System.out.println("\n========== SECURITY DASHBOARD ==========");
        System.out.println("1. Verify Resident");
        System.out.println("2. Verify Visitor Host");
        System.out.println("3. Issue Temporary Visitor Pass");
        System.out.println("4. Record Entry Log");
        System.out.println("5. View Security Access Records");
        System.out.println("Security dashboard is under development.");
    }

    private static void displayServiceMenu() {
        System.out.println("\n========== SERVICE / DELIVERY DASHBOARD ==========");
        System.out.println("1. Request Unit Access");
        System.out.println("2. Submit Delivery Information");
        System.out.println("3. Request Temporary Access Permit");
        System.out.println("4. View Access Status");
        System.out.println("Service dashboard is under development.");
    }
}