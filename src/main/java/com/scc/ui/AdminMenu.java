package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.sql.*;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner sc;

    public AdminMenu(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void display(User adminUser) {
        boolean administering = true;
        while (administering) {
            System.out.println("\n==================================");
            System.out.println("          ADMIN DASHBOARD         ");
            System.out.println("==================================");
            System.out.println("1. View All Users");
            System.out.println("2. View Pending User Approvals");
            System.out.println("3. Approve User Registration");
            System.out.println("4. Add New User");
            System.out.println("5. Update User Role / Status");
            System.out.println("6. Delete User Account");
            System.out.println("7. Logout");
            System.out.print("Choose an option (1-7): ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
                    viewPendingUsers();
                    break;
                case "3":
                    approveUser();
                    break;
                case "4":
                    addNewUser();
                    break;
                case "5":
                    updateUserRoleOrStatus();
                    break;
                case "6":
                    deleteUser();
                    break;
                case "7":
                    administering = false;
                    System.out.println("Logging out from Admin Dashboard...");
                    break;
                default:
                    System.out.println("Invalid option. Please choose between 1 and 7.");
            }
        }
    }

    private void viewAllUsers() {
        String sql = "SELECT id, username, roles, status FROM users";
        try (Connection conn = com.scc.dao.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- ALL USERS ---");
            while (rs.next()) {
                System.out.printf("[%d] Username: %s | Role: %s | Status: %s%n",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("roles"),
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            System.err.println("Error viewing users: " + e.getMessage());
        }
    }

    private void viewPendingUsers() {
        String sql = "SELECT id, username, roles, status FROM users WHERE status = 'PENDING'";
        try (Connection conn = com.scc.dao.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- PENDING USER APPROVALS ---");
            boolean hasPending = false;
            while (rs.next()) {
                hasPending = true;
                System.out.printf("[%d] Username: %s | Role: %s | Status: %s%n",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("roles"),
                        rs.getString("status"));
            }
            if (!hasPending) {
                System.out.println("No pending user approvals found.");
            }
        } catch (SQLException e) {
            System.err.println("Error viewing pending users: " + e.getMessage());
        }
    }

    private void approveUser() {
        viewPendingUsers();
        System.out.print("Enter User ID to approve: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            String sql = "UPDATE users SET status = 'APPROVED' WHERE id = ? AND status = 'PENDING'";

            try (Connection conn = com.scc.dao.DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✔ User successfully approved.");
                } else {
                    System.out.println("❌ Invalid ID or user is not in PENDING status.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid numeric ID.");
        } catch (SQLException e) {
            System.err.println("Error approving user: " + e.getMessage());
        }
    }

    private void addNewUser() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        System.out.print("Select Role [1] STAFF/CASHIER | [2] DELIVERY RIDER | [3] ADMIN: ");

        try {
            int roleChoice = Integer.parseInt(sc.nextLine());
            String role = switch (roleChoice) {
                case 1 -> "STAFF";
                case 2 -> "DELIVERY RIDER";
                case 3 -> "ADMIN";
                default -> null;
            };

            if (role != null) {
                String sql = "INSERT INTO users(username, password, roles, status) VALUES(?, ?, ?, 'APPROVED')";
                try (Connection conn = com.scc.dao.DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    pstmt.setString(3, role);
                    pstmt.executeUpdate();
                    System.out.println("✔ New user added and automatically approved.");
                }
            } else {
                System.out.println("❌ Invalid role selection!");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input number.");
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    private void updateUserRoleOrStatus() {
        viewAllUsers();
        System.out.print("Enter User ID to update: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            System.out.print("Enter new role (STAFF / DELIVERY RIDER / ADMIN): ");
            String newRole = sc.nextLine().toUpperCase();
            System.out.print("Enter new status (PENDING / APPROVED): ");
            String newStatus = sc.nextLine().toUpperCase();

            String sql = "UPDATE users SET roles = ?, status = ? WHERE id = ?";
            try (Connection conn = com.scc.dao.DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newRole);
                pstmt.setString(2, newStatus);
                pstmt.setInt(3, id);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("✔ User details updated successfully.");
                } else {
                    System.out.println("❌ User ID not found.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid numeric ID.");
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    private void deleteUser() {
        viewAllUsers();
        System.out.print("Enter User ID to delete: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            String sql = "DELETE FROM users WHERE id = ?";
            try (Connection conn = com.scc.dao.DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("✔ User account deleted successfully.");
                } else {
                    System.out.println("❌ User ID not found.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid numeric ID.");
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }
}