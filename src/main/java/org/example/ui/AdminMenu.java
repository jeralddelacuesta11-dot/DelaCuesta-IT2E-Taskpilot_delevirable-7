package org.example.ui;

import org.example.dao.UserDao;
import org.example.model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class AdminMenu {
    private static final Set<String> VALID_ROLES = Set.of("ADMIN", "MANAGER", "EMPLOYEE");
    private static final Set<String> VALID_STATUSES = Set.of("APPROVED", "PENDING");
    private final Scanner scanner;
    private final UserDao userDao;
    private final User currentAdmin;
    private final org.example.dao.TaskDao taskDao;

    public AdminMenu(Scanner scanner, UserDao userDao, User currentAdmin, org.example.dao.TaskDao taskDao) {
        this.scanner = scanner; this.userDao = userDao; this.currentAdmin = currentAdmin; this.taskDao = taskDao;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("1. View All System Users\n2. View Pending Registrations\n3. Approve Pending User\n4. Update User Role and Status\n5. Task Progress and Deadline Report\n6. View Tasks\n7. Cancel Task\n0. Logout");
            int choice = readIntInput("Choose: ");
            switch (choice) {
                case 1 -> viewUsers(false);
                case 2 -> viewUsers(true);
                case 3 -> approveUser();
                case 4 -> updateUser();
                case 5 -> showReport();
                case 6 -> viewTasks();
                case 7 -> cancelTask();
                case 0 -> { running = false; System.out.println("Logged out."); }
                default -> System.out.println("Invalid menu option.");
            }
        }
    }

    private void viewUsers(boolean pendingOnly) {
        try {
            List<User> users = pendingOnly ? userDao.getPendingUsers() : userDao.getAllUsers();
            printTable(users);
            if (users.isEmpty()) System.out.println(pendingOnly ? "No pending registrations." : "No users found.");
        } catch (SQLException e) { System.out.println("Could not retrieve users: " + e.getMessage()); }
    }

    private void printTable(List<User> users) {
        System.out.printf("%5s | %-20s | %-10s | %-10s%n", "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println("------+----------------------+------------+------------");
        for (User user : users) System.out.printf("%5d | %-20s | %-10s | %-10s%n", user.id(), user.username(), user.roles(), user.status());
    }

    private void approveUser() {
        viewUsers(true);
        int id = readIntInput("Enter User ID to approve (0 to cancel): ");
        if (id == 0) { System.out.println("Update cancelled."); return; }
        try {
            if (userDao.approveUser(id)) System.out.println("User approved successfully.");
            else System.out.println("User ID not found or already updated.");
        } catch (SQLException e) { System.out.println("Could not approve user: " + e.getMessage()); }
    }

    private void updateUser() {
        try {
            List<User> users = userDao.getAllUsers();
            printTable(users);
            if (users.isEmpty()) { System.out.println("No users available to update."); return; }
            int targetUserId = readIntInput("Enter target User ID (0 to cancel): ");
            if (targetUserId == 0) { System.out.println("Update cancelled."); return; }
            if (targetUserId == currentAdmin.id()) {
                System.out.println("WARNING: Modifying the currently active admin account is restricted.");
                return;
            }
            User target = userDao.getUserById(targetUserId);
            if (target == null) { System.out.println("User ID not found."); return; }
            System.out.println("Selected: " + target.username() + " | Current role: " + target.roles() + " | Current status: " + target.status());
            String newRole = readValidRole();
            if (newRole == null) { System.out.println("Update cancelled."); return; }
            String newStatus = readValidStatus();
            if (newStatus == null) { System.out.println("Update cancelled."); return; }
            if (userDao.updateUserRoleAndStatus(targetUserId, newRole, newStatus)) {
                System.out.println("SUCCESS: User ID " + targetUserId + " updated successfully.");
                System.out.println("New role: " + newRole + " | New status: " + newStatus);
            } else System.out.println("No user was updated. Please check the ID.");
        } catch (SQLException e) { System.out.println("Could not update user: " + e.getMessage()); }
    }

    private void viewTasks() { try { var tasks=taskDao.getAllTasks(); System.out.printf("%4s | %-22s | %-18s | %-8s | %-12s | %-12s | %-12s%n","ID","TASK","EMPLOYEE","PRIORITY","DUE DATE","STATUS","NOTES"); for(var t:tasks) System.out.printf("%4d | %-22.22s | %-18.18s | %-8s | %-12s | %-12s | %-12.12s%n",t.id(),t.name(),t.employeeName(),t.priority(),t.dueDate(),t.status(),t.progressNotes()); if(tasks.isEmpty())System.out.println("No tasks recorded."); } catch(SQLException e){System.out.println("Could not retrieve tasks: "+e.getMessage());} }
    private void showReport(){try{int[] a=taskDao.getSummary();System.out.println("\n=== TASK PROGRESS AND DEADLINE REPORT ===");System.out.println("Total tasks: "+a[0]+" | Completed: "+a[1]+" | Pending: "+a[2]+" | Ongoing: "+a[3]+" | Overdue: "+a[4]+" | High priority: "+a[5]);}catch(SQLException e){System.out.println("Could not generate report: "+e.getMessage());}}
    private void cancelTask(){viewTasks();int id=readIntInput("Task ID to cancel (0 to cancel): ");if(id==0){System.out.println("Cancelled.");return;}try{System.out.println(taskDao.cancelTask(id)?"Task cancelled and retained in history.":"Task not found or already cancelled.");}catch(SQLException e){System.out.println("Could not cancel task: "+e.getMessage());}}

    // Returns -1 on non-numeric input; caller safely handles it as an invalid choice/ID.
    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try { return Integer.parseInt(input); }
            catch (NumberFormatException e) { System.out.println("Invalid input. Please enter a whole number (or 0 to cancel)."); }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter new role (ADMIN / MANAGER / EMPLOYEE, or 0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) return null;
            if (VALID_ROLES.contains(input)) return input;
            System.out.println("Invalid role. Valid roles are ADMIN, MANAGER, and EMPLOYEE.");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status (APPROVED / PENDING, or 0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) return null;
            if (VALID_STATUSES.contains(input)) return input;
            System.out.println("Invalid status. Valid statuses are APPROVED and PENDING.");
        }
    }
}
