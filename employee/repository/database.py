import sqlite3
import os
from pathlib import Path
from typing import Optional

PROJECT_ROOT = Path(__file__).resolve().parent.parent.parent

class DatabaseConnection:
    def __init__(self, db_path: Optional[str] = None):
        """get and save the DB Path"""
        raw_path = db_path or os.getenv('APP_DB_PATH', "./db/main.db")
        resolved = Path(raw_path)
        if not resolved.is_absolute():
            resolved = (PROJECT_ROOT / resolved).resolve()
        resolved.parent.mkdir(parents=True, exist_ok=True)
        self.db_path = str(resolved)

    def get_connection(self) -> sqlite3.Connection:
        """Connect to the path saved in the above (__init__) function"""
        conn = sqlite3.connect(self.db_path)
        conn.row_factory = sqlite3.Row
        # SQLite ignores FOREIGN KEY constraints unless each connection opts in
        conn.execute("PRAGMA foreign_keys = ON")
        return conn
    
    def initialize_database(self):
        with self.get_connection() as conn:

            # conn.execute('''
            #     DROP TABLE IF EXISTS approvals;
            # ''')
            # conn.execute('''
            #     DROP TABLE IF EXISTS expenses;
            # ''')
            # conn.execute('''
            #     DROP TABLE IF EXISTS users;
            # ''')

            conn.execute('''
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                )
            ''')

            conn.execute('''
                CREATE TABLE IF NOT EXISTS expenses(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    amount REAL,
                    description TEXT,
                    expense_date TEXT,
                    category TEXT NOT NULL DEFAULT 'Other' CHECK (category IN ('Travel', 'Lodging', 'Meals', 'Office Supplies', 'Software', 'Training', 'Other')),
                    user_id_fk INTEGER NOT NULL,
                    FOREIGN KEY (user_id_fk) REFERENCES users(id)
                )
            ''')

            expense_columns = conn.execute("PRAGMA table_info(expenses)").fetchall()
            expense_column_names = {column["name"] for column in expense_columns}
            if "category" not in expense_column_names:
                conn.execute('''
                    ALTER TABLE expenses
                    ADD COLUMN category TEXT NOT NULL DEFAULT 'Other'
                    CHECK (category IN ('Travel', 'Lodging', 'Meals', 'Office Supplies', 'Software', 'Training', 'Other'))
                ''')

            conn.execute('''
                CREATE TABLE IF NOT EXISTS approvals(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    expense_id_fk INTEGER,
                    status TEXT,
                    reviewer INTEGER,
                    comment TEXT,
                    review_date TEXT,
                    FOREIGN KEY (expense_id_fk) REFERENCES expenses(id)
                )
            ''')

            conn.commit()
