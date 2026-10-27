package org.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {
    private DatabaseInitializer() {}
    public static void initialize() {
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL, roles TEXT NOT NULL CHECK (roles IN ('ADMIN','MANAGER','EMPLOYEE')), status TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('APPROVED','PENDING'))) ");
            st.execute("CREATE TABLE IF NOT EXISTS tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, task_name TEXT NOT NULL, description TEXT, assigned_employee_id INTEGER, priority TEXT NOT NULL CHECK(priority IN ('LOW','MEDIUM','HIGH')), start_date TEXT NOT NULL, due_date TEXT NOT NULL, status TEXT NOT NULL DEFAULT 'PENDING' CHECK(status IN ('PENDING','IN PROGRESS','COMPLETED','CANCELLED')), progress_notes TEXT DEFAULT '', created_by INTEGER, FOREIGN KEY(assigned_employee_id) REFERENCES users(id), FOREIGN KEY(created_by) REFERENCES users(id))");
            st.execute("CREATE TABLE IF NOT EXISTS activity_logs (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, task_id INTEGER, action TEXT NOT NULL, date_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, description TEXT, FOREIGN KEY(user_id) REFERENCES users(id), FOREIGN KEY(task_id) REFERENCES tasks(id))");
            try (PreparedStatement check = c.prepareStatement("SELECT id FROM users WHERE username = ?")) {
                check.setString(1, "admin");
                try (var rs = check.executeQuery()) { if (!rs.next()) try (PreparedStatement ins = c.prepareStatement("INSERT INTO users(username,password,roles,status) VALUES(?,?,?,?)")) { ins.setString(1,"admin"); ins.setString(2,"admin123"); ins.setString(3,"ADMIN"); ins.setString(4,"APPROVED"); ins.executeUpdate(); } }
            }
            System.out.println("TaskPilot database initialized successfully.");
        } catch (SQLException e) { System.out.println("Could not initialize the database: " + e.getMessage()); throw new IllegalStateException("Database initialization failed", e); }
    }
}
