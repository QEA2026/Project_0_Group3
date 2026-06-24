PRAGMA foreign_keys = ON;

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
	approval_id INTEGER PRIMARY KEY AUTOINCREMENT,
	expense_id_fk INTEGER NOT NULL,
	FOREIGN KEY (expense_id_fk) REFERENCES expenses(expense_id),
	status TEXT,
	reviewer_id INTEGER,
	FOREIGN KEY (reviewer_id) REFERENCES expenses(user_id_fk),
	comment TEXT,
	review_date TEXT
);