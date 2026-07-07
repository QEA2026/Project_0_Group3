-- ============================================================
-- Revature Expense Manager: full rebuild + test data
-- Run as a script (Alt+X in DBeaver), not statement-by-statement
-- ============================================================
PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS approvals;
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL
);

CREATE TABLE expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    amount REAL,
    description TEXT,
    expense_date TEXT,                        -- ISO format: YYYY-MM-DD
    user_id_fk INTEGER NOT NULL,
    category TEXT NOT NULL DEFAULT 'Other',
    FOREIGN KEY (user_id_fk) REFERENCES users(id)
);

CREATE TABLE approvals (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    expense_id_fk INTEGER,
    status TEXT,
    reviewer INTEGER,                         -- manager's user id, NULL until reviewed
    comment TEXT,
    review_date TEXT,
    FOREIGN KEY (expense_id_fk) REFERENCES expenses(id),
    FOREIGN KEY (reviewer) REFERENCES users(id)
);

-- one approval row per expense; a duplicate insert fails instead of
-- making the expense repeat in the manager app
CREATE UNIQUE INDEX idx_approvals_one_per_expense ON approvals(expense_id_fk);

-- ---- users (hashes copied from your current DB so logins keep working) ----
INSERT INTO users (id, username, password, role) VALUES
(2, 'test_manager', '$2a$12$sZZ3GAWLOSpU9zIdSX.pZ.QUmuHvizurrTvpjSkvTALywz6oZf4YK', 'Manager');

-- ---- expenses: every category, three employees, May-July ----
INSERT INTO expenses (id, amount, description, expense_date, user_id_fk, category) VALUES
(12, 245.50, 'Flight to client site',       '2026-06-02', 1, 'Travel'),
(13, 480.00, 'Hotel, 3 nights',             '2026-06-03', 1, 'Lodging'),
(14,  62.75, 'Team lunch with client',      '2026-06-04', 1, 'Meals'),
(15, 129.99, 'Mechanical keyboard',         '2026-06-10', 1, 'Office Supplies'),
(16, 199.00, 'IntelliJ license renewal',    '2026-06-15', 1, 'Software'),
(17, 350.00, 'SQL certification course',    '2026-06-20', 1, 'Training'),
(18,  45.30, 'Rideshare to airport',        '2026-06-28', 1, 'Other'),
(19,  88.20, 'Client dinner',               '2026-07-01', 1, 'Other'),
(20, 312.40, 'Flight to quarterly review',  '2026-05-05', 1, 'Travel'),
(21, 275.00, 'Hotel, 2 nights',             '2026-05-06', 1, 'Lodging'),
(22,  38.60, 'Airport parking',             '2026-05-07', 1, 'Travel'),
(23,  54.20, 'Working lunch, new hires',    '2026-05-12', 1, 'Meals'),
(24,  89.99, 'Standing desk mat',           '2026-05-18', 1, 'Office Supplies'),
(25, 149.00, 'GitHub Copilot annual',       '2026-05-20', 1, 'Software'),
(26, 499.00, 'AWS certification exam',      '2026-06-08', 1, 'Training'),
(27,  23.80, 'Client coffee meeting',       '2026-06-11', 1, 'Meals'),
(28, 180.75, 'Train tickets, client site',  '2026-06-17', 1, 'Travel'),
(29,  65.00, 'Whiteboard and markers',      '2026-07-02', 1, 'Office Supplies'),
(30, 120.00, 'Team dinner, project launch', '2026-07-03', 1, 'Meals'),
(31, 240.00, 'Online course subscription',  '2026-07-05', 1, 'Training'),
(32,  74.99, 'Ergonomic mouse',             '2026-07-06', 1, 'Office Supplies'),
(33, 415.00, 'Flight to team offsite',      '2026-07-08', 1, 'Travel'),
(34,  29.50, 'Lunch with vendor',           '2026-07-10', 1, 'Meals'),
(35, 189.00, 'Docker Desktop licenses',     '2026-07-12', 1, 'Software');

-- ---- approvals: exactly one per expense; 12-16 + 20-27 reviewed, rest pending ----
INSERT INTO approvals (expense_id_fk, status, reviewer, comment, review_date) VALUES
(12, 'approved', 2, 'Standard client travel',         '2026-06-05'),
(13, 'approved', 2, 'Within lodging policy',          '2026-06-05'),
(14, 'approved', 2, 'Client meal, receipt attached',  '2026-06-06'),
(15, 'denied',   2, 'Personal equipment not covered', '2026-06-12'),
(16, 'approved', 2, 'Annual license, budgeted',       '2026-06-16'),
(17, 'pending',  NULL, NULL, NULL),
(18, 'pending',  NULL, NULL, NULL),
(19, 'pending',  NULL, NULL, NULL),
(20, 'approved', 2, 'Quarterly review travel',        '2026-05-08'),
(21, 'approved', 2, 'Within lodging policy',          '2026-05-08'),
(22, 'denied',   2, 'Use rideshare next time',        '2026-05-09'),
(23, 'approved', 2, 'Onboarding meal',                '2026-05-13'),
(24, 'denied',   2, 'Home office gear not covered',   '2026-05-19'),
(25, 'approved', 2, 'Approved team tooling',          '2026-05-21'),
(26, 'approved', 2, 'Cert supports current project',  '2026-06-09'),
(27, 'approved', 2, 'Client relations',               '2026-06-12'),
(28, 'pending',  NULL, NULL, NULL),
(29, 'pending',  NULL, NULL, NULL),
(30, 'pending',  NULL, NULL, NULL),
(31, 'pending',  NULL, NULL, NULL),
(32, 'pending',  NULL, NULL, NULL),
(33, 'pending',  NULL, NULL, NULL),
(34, 'pending',  NULL, NULL, NULL),
(35, 'pending',  NULL, NULL, NULL);

-- ---- sanity checks: 24/24/0 expected ----
SELECT (SELECT COUNT(*) FROM expenses)  AS expense_count, 
       (SELECT COUNT(*) FROM approvals) AS approval_count,
       (SELECT COUNT(*) FROM expenses e LEFT JOIN approvals a
            ON a.expense_id_fk = e.id WHERE a.id IS NULL) AS invisible_expenses;

-- ---- handy queries (run individually with Ctrl+Enter) ----

SELECT * FROM users;
SELECT * FROM expenses;
SELECT * FROM approvals;
