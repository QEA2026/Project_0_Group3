from typing import Optional, List
from .expense_model import Expense
from .database import DatabaseConnection

# CREATE TABLE IF NOT EXISTS expenses(
#                     expense_id INTEGER PRIMARY KEY AUTOINCREMENT,
#                     amount REAL,
#                     description TEXT,
#                     expense_date TEXT,
#                     user_id_fk INTEGER NOT NULL, FOREIGN KEY (user_id_fk) REFERENCES users(user_id)
#                 )

class ExpenseRepository:

    def __init__(self, db_connection: DatabaseConnection):
        self.db_connection = db_connection

    def create(self, expense: Expense) -> Expense:

        with self.db_connection.get_connection() as conn:

            cursor = conn.execute(
                "INSERT INTO expenses VALUES (?,?,?,?)",
                (expense.amount, expense.description, expense.expense_date, expense.user_id_fk)
            )
            expense.id = cursor.lastrowid

            # TODO: Insert it into approvals this newly created expense along with the expense id

            conn.commit()
        return expense
    
    def find_by_id(self, expense_id: int) -> Optional[Expense]:

        with self.db_connection.get_connection() as conn:

            cursor = conn.execute(
                "SELECT expense_id, amount, description, expense_date, user_id_fk FROM expenses WHERE expense_id = ?",
                (expense_id,)
            )

            row = cursor.fetchone()

            if row:
                return Expense(expense_id=row["expense_id"], amount=row["amount"], description=row["description"], expense_date=row["expense_date"], user_id_fk=row["user_id_fk"])
            
            return None
        
    def find_by_user_id(self, user_id: int) -> List[Expense]:

        expenses = []

        with self.db_connection.get_connection() as conn:

            cursor = conn.execute(
                "SELECT expense_id, amount, description, expense_date, user_id_fk FROM expenses WHERE user_id_fk = ?",
                (user_id,)
            )

            for row in cursor.fetchall():
                expenses.append(Expense(expense_id=row["expense_id"], amount=row["amount"], description=row["description"], expense_date=row["expense_date"], user_id_fk=row["user_id_fk"]))
        
        return expenses
    
    def update(self, expense: Expense) -> Expense:

        with self.db_connection.get_connection() as conn:
            conn.execute(
                "UPDATE expenses SET amount = ?, description = ? WHERE expense_id = ?",
                (expense.amount , expense.description, expense.expense_id)
            )

            conn.commit()
        return expense
    
    def delete(self, expense_id: int) -> bool:

        with self.db_connection.get_connection() as conn:
            conn.execute("DELETE from approvals WHERE expense_id_fk = ?", (expense_id,))

            cursor = conn.execute("DELETE from expenses WHERE expense_id = ?", (expense_id,))
            conn.commit()

            return cursor.rowcount > 0
        
        