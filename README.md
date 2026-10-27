# TaskPilot — Company Task Assignment and Progress Monitoring System

PF201: OOP - IT2G | DELA CUESTA, JERALD | October 06, 2026

## Requirements
- Java 17 or later
- IntelliJ IDEA with Maven support

## Run
Open this folder as a Maven project, reload Maven dependencies, then run `src/main/java/org/example/Main.java`.

The SQLite database `taskpilot.db` is created in the working directory. Default first-run administrator: username `admin`, password `admin123`. Change the password after setup if using real data.

## Roles
- ADMIN: manage users, approve pending accounts, update roles/statuses, view tasks, cancel tasks, and view reports.
- MANAGER: create and assign tasks, view tasks, cancel tasks, and view task reports.
- EMPLOYEE: view assigned tasks and update task status/progress notes.

New Manager and Employee registrations start as `PENDING`; an Admin must approve them. Roles accepted by Deliverable 6: `ADMIN`, `MANAGER`, `EMPLOYEE`. Statuses: `APPROVED`, `PENDING`. The admin cannot update their own currently active account. Enter `0` to cancel at the role/status update prompts.

## Database tables
- `users`: account username, password, role and approval status.
- `tasks`: task details, employee assignment, priority, dates, status and notes.
- `activity_logs`: table reserved for audit events in the approved system design.

## Deliverable 6 proof
1. Log in as admin and choose Update User Role and Status. Enter letters at the ID prompt to show validation; then update a different user.
2. Open `taskpilot.db` in SQLiteStudio/DB Browser and show the matching row in `users`.
