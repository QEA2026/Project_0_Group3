# Revature Expense Manager - Development Plan

## Project Overview

Revature Expense Manager is a backend-focused expense tracking system consisting of:

* Python Employee Application
* Java Manager Application
* Shared SQLite Database

The goal is for employees to submit expenses and managers to review, approve, or deny them using separate applications that communicate through the same database.

---

# Phase 1: Project Setup

## Create Project Structure

```text
revature-expense-manager/
│
├── db/
│   ├── expense_manager.db
│   ├── schema.sql
│   └── seed.sql
│
├── employee-python/
│   ├── main.py
│   ├── database.py
│   ├── auth.py
│   ├── expense_service.py
│   └── requirements.txt
│
├── manager-java/
│   ├── pom.xml
│   └── src/main/java/com/revature/
│       ├── Main.java
│       ├── DatabaseConnection.java
│       ├── AuthService.java
│       ├── ExpenseService.java
│       ├── User.java
│       └── Expense.java
│
└── README.md
```

---

# Phase 2: Database Design

## Create SQLite Schema

Create:

```text
db/schema.sql
```

### Users Table

```text
id
username
password
role
```

Role values:

```text
Employee
Manager
```

---

### Expenses Table

```text
id
user_id
amount
description
category
date
```

Category is included because managers must generate category reports.

---

### Approvals Table

```text
id
expense_id
status
reviewer
comment
review_date
```

Status values:

```text
pending
approved
denied
```

---

# Phase 3: Seed Data

Create:

```text
db/seed.sql
```

Add sample users:

```text
employee1 / password123 / Employee
manager1 / password123 / Manager
```

Add sample expenses for testing.

---

# Phase 4: Database Testing

Before writing any application code:

Verify:

```sql
SELECT * FROM users;
SELECT * FROM expenses;
SELECT * FROM approvals;
```

Make sure:

* Tables exist
* Data is inserted correctly
* Relationships work

---

# Phase 5: Python Employee Application

## Build Database Connection

File:

```text
employee-python/database.py
```

Responsibilities:

* Connect to SQLite
* Return database connection object

---

## Build Authentication

File:

```text
employee-python/auth.py
```

Functions:

```python
login(username, password)
logout()
```

Requirements:

* Verify credentials
* Verify role is Employee

---

## Build Expense Service

File:

```text
employee-python/expense_service.py
```

Functions:

```python
submit_expense()
view_my_expenses()
edit_pending_expense()
delete_pending_expense()
view_history()
```

Rules:

* Employees only see their own expenses
* Employees can only edit pending expenses
* Employees can only delete pending expenses

---

## Build Employee CLI

File:

```text
employee-python/main.py
```

Menu:

```text
1. Submit Expense
2. View My Expenses
3. Edit Pending Expense
4. Delete Pending Expense
5. View Expense History
6. Logout
```

---

# Phase 6: Java Manager Application

## Create Maven Project

Inside:

```text
manager-java/
```

Requirements:

* Java 17+
* SQLite JDBC Driver

---

## Build Database Connection

File:

```java
DatabaseConnection.java
```

Responsibilities:

* Connect to shared SQLite database

---

## Build Authentication

File:

```java
AuthService.java
```

Functions:

```java
login(username, password)
logout()
```

Requirements:

* Verify credentials
* Verify role is Manager

---

## Build Expense Service

File:

```java
ExpenseService.java
```

Functions:

```java
viewPendingExpenses()
approveExpense()
denyExpense()
reportByEmployee()
reportByCategory()
reportByDate()
```

Rules:

* Managers can only review pending expenses
* Managers can add comments
* Managers update approval status

---

## Build Manager CLI

File:

```java
Main.java
```

Menu:

```text
1. View Pending Expenses
2. Approve Expense
3. Deny Expense
4. Report By Employee
5. Report By Category
6. Report By Date
7. Logout
```

---

# Phase 7: Reporting Features

## Report By Employee

Show:

```text
Employee Name
Total Expenses
Approved Expenses
Denied Expenses
Pending Expenses
```

---

## Report By Category

Show:

```text
Travel
Meals
Office Supplies
Training
Other
```

And total spending for each.

---

## Report By Date

Allow managers to:

* View expenses between two dates
* View totals within date range

---

# Phase 8: Business Rules

## Employee Rules

Employees can:

* Login
* Submit expenses
* View their expenses
* Edit pending expenses
* Delete pending expenses

Employees cannot:

* Approve expenses
* View other employees' expenses

---

## Manager Rules

Managers can:

* Login
* View pending expenses
* Approve expenses
* Deny expenses
* Add review comments
* Generate reports

Managers cannot:

* Submit expenses as employees

---

# Phase 9: End-to-End Testing

Test the following workflow:

### Employee

```text
Login
Submit Expense
Logout
```

### Manager

```text
Login
View Pending Expense
Approve Expense
Add Comment
Logout
```

### Employee

```text
Login
View Expense History
See Approved Status
See Manager Comment
Logout
```

---

# Final MVP Checklist

## Database

* [ ] SQLite database created
* [ ] Schema implemented
* [ ] Seed data created

## Python Employee App

* [ ] Login
* [ ] Submit expense
* [ ] View expenses
* [ ] Edit pending expense
* [ ] Delete pending expense
* [ ] View history

## Java Manager App

* [ ] Login
* [ ] View pending expenses
* [ ] Approve expenses
* [ ] Deny expenses
* [ ] Add comments
* [ ] Generate reports

## Project

* [ ] README completed
* [ ] GitHub repository created
* [ ] Code committed and pushed

---

# Recommended Build Order

```text
1. Create folder structure
2. Create schema.sql
3. Create seed.sql
4. Test SQLite database
5. Build Python database connection
6. Build Python authentication
7. Build Python expense submission
8. Build Python expense viewing
9. Build Python edit/delete functionality
10. Create Java Maven project
11. Build Java database connection
12. Build Java authentication
13. Build Java pending expense review
14. Build Java approve/deny functionality
15. Build Java reporting
16. Perform end-to-end testing
17. Write README
18. Push to GitHub
```

---

# Definition of Done

A complete expense lifecycle works:

```text
Employee Login
    ↓
Submit Expense
    ↓
Expense Stored In SQLite
    ↓
Manager Login
    ↓
Review Expense
    ↓
Approve/Deny Expense
    ↓
Comment Added
    ↓
Employee Views Final Status
```
