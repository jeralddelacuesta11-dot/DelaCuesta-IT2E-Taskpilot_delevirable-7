package org.example.model;
public record Task(int id, String name, String description, int employeeId, String employeeName, String priority, String startDate, String dueDate, String status, String progressNotes) {}
