import sqlite3
import os
from typing import Optional

class DatabaseConnection:
    def __init__(self, db_path: Optional[str] = None):
        """get and save the DB Path"""
        self.db_path = db_path or os.getenv('APP_DB_PATH', "main.db")
        print(os.getenv('APP_DB_PATH'))

    def get_connection(self) -> sqlite3.Connection:
        """Connect to the path saved in the above (__init__) function"""
        conn = sqlite3.connect(self.db_path)
        conn.row_factory = sqlite3.Row
        return conn
    
    def initialize_database(self):
        with self.get_connection() as conn:

            conn.execute('''
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                )
            ''')

            conn.execute('''
                CREATE TABLE IF NOT EXISTS expenses(
                    expense_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    amount REAL,
                    description TEXT,
                    expense_date TEXT,
                    user_id_fk INTEGER NOT NULL,
                    FOREIGN KEY (user_id_fk) REFERENCES users(user_id)
                )
            ''')

            conn.execute('''
                CREATE TABLE IF NOT EXISTS approvals(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    expense_id_fk INTEGER,
                    status TEXT,
                    reviewer INTEGER,
                    comment TEXT,
                    review_date TEXT,
                    FOREIGN KEY (expense_id_fk) REFERENCES expenses(expense_id)
                )
            ''')

            conn.commit()
