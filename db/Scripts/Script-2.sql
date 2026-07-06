PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS approvals;
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS users;


CREATE TABLE IF NOT EXISTS users (
	user_id INTEGER PRIMARY KEY AUTOINCREMENT,
	username TEXT UNIQUE NOT NULL,
	password TEXT NOT NULL,
	role TEXT NOT NULL);

CREATE TABLE IF NOT EXISTS expenses(
	expense_id INTEGER PRIMARY KEY AUTOINCREMENT,
	amount REAL,
	description TEXT,
	expense_date TEXT,
	user_id_fk INTEGER NOT NULL,
	FOREIGN KEY (user_id_fk) REFERENCES users(user_id));

CREATE TABLE IF NOT EXISTS approvals(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	expense_id_fk INTEGER,
	status TEXT,
	reviewer INTEGER,
	comment TEXT,
	review_date TEXT,
	FOREIGN KEY (reviewer) REFERENCES expenses(user_id_fk),
	FOREIGN KEY (expense_id_fk) REFERENCES expenses(expense_id)
);

SELECT * FROM users;
SELECT * FROM expenses;
SELECT * FROM approvals;

SELECT * FROM approvals a
LEFT JOIN expenses e ON a.expense_id_fk = e.id
WHERE e.id = 10;

SELECT a.status, a.id as approval_id, e.id as expense_id,* FROM approvals a
LEFT JOIN expenses e ON a.expense_id_fk = e.id;

                    SELECT e.id as expense_id, a.id as approval_id, * FROM approvals a
                    INNER JOIN expenses e ON a.expense_id_fk = e.id
                    WHERE a.status = "pending";
                    
                    SELECT a.status, a.id as approval_id, e.id as expense_id,* FROM approvals a
                    LEFT JOIN expenses e ON a.expense_id_fk = e.id
                    WHERE e.user_id_fk = 2;
                    
DELETE FROM expenses 
WHERE amount IS NULL;
                    