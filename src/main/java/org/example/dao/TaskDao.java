package org.example.dao;

import org.example.model.Task;
import java.sql.*;
import java.util.*;

public class TaskDao {
    public boolean createTask(String name, String description, int employeeId, String priority, String startDate, String dueDate, int managerId) throws SQLException {
        String sql = "INSERT INTO tasks(task_name,description,assigned_employee_id,priority,start_date,due_date,status,created_by) VALUES(?,?,?,?,?,?, 'PENDING',?)";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,name); ps.setString(2,description); if(employeeId==0) ps.setNull(3,Types.INTEGER); else ps.setInt(3,employeeId); ps.setString(4,priority.toUpperCase()); ps.setString(5,startDate); ps.setString(6,dueDate); ps.setInt(7,managerId); return ps.executeUpdate()==1;
        }
    }
    public List<Task> getAllTasks() throws SQLException { return fetch("SELECT t.*, COALESCE(u.username,'Unassigned') employee_name FROM tasks t LEFT JOIN users u ON u.id=t.assigned_employee_id ORDER BY t.id",0); }
    public List<Task> getTasksForEmployee(int employeeId) throws SQLException { return fetch("SELECT t.*, COALESCE(u.username,'Unassigned') employee_name FROM tasks t LEFT JOIN users u ON u.id=t.assigned_employee_id WHERE t.assigned_employee_id=? ORDER BY t.due_date",employeeId); }
    private List<Task> fetch(String sql,int employeeId) throws SQLException { List<Task> out=new ArrayList<>(); try(Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)){ if(sql.contains("?")) ps.setInt(1,employeeId); try(ResultSet rs=ps.executeQuery()){while(rs.next()) out.add(new Task(rs.getInt("id"),rs.getString("task_name"),rs.getString("description"),rs.getInt("assigned_employee_id"),rs.getString("employee_name"),rs.getString("priority"),rs.getString("start_date"),rs.getString("due_date"),rs.getString("status"),rs.getString("progress_notes")));}} return out; }
    public boolean updateTaskStatus(int taskId,int employeeId,String status,String notes) throws SQLException { String sql="UPDATE tasks SET status=?, progress_notes=? WHERE id=? AND assigned_employee_id=? AND status <> 'CANCELLED'"; try(Connection c=DatabaseConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setString(1,status.toUpperCase());ps.setString(2,notes);ps.setInt(3,taskId);ps.setInt(4,employeeId);return ps.executeUpdate()==1;} }
    public boolean cancelTask(int taskId) throws SQLException {try(Connection c=DatabaseConnection.getConnection();PreparedStatement ps=c.prepareStatement("UPDATE tasks SET status='CANCELLED' WHERE id=? AND status <> 'CANCELLED'")){ps.setInt(1,taskId);return ps.executeUpdate()==1;}}
    public int[] getSummary() throws SQLException { int[] a=new int[6]; String sql="SELECT COUNT(*), SUM(CASE WHEN status='COMPLETED' THEN 1 ELSE 0 END), SUM(CASE WHEN status='PENDING' THEN 1 ELSE 0 END), SUM(CASE WHEN status='IN PROGRESS' THEN 1 ELSE 0 END), SUM(CASE WHEN status NOT IN ('COMPLETED','CANCELLED') AND due_date < date('now') THEN 1 ELSE 0 END), SUM(CASE WHEN priority='HIGH' AND status <> 'CANCELLED' THEN 1 ELSE 0 END) FROM tasks"; try(Connection c=DatabaseConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql);ResultSet rs=ps.executeQuery()){if(rs.next())for(int i=0;i<6;i++)a[i]=rs.getInt(i+1);}return a;}
}
