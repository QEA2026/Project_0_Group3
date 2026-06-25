from typing import List, Optional
from .approval_model import Approval
from .expense_model import Expense
from .database import DatabaseConnection


# approvals(
#                     id INTEGER PRIMARY KEY AUTOINCREMENT,
#                     expense_id_fk INTEGER, FOREIGN KEY (expense_id_fk) REFERENCES expenses(expense_id)
#                     status TEXT,
#                     reviewer INTEGER,
#                     comment TEXT,
#                     review_date TEXT,       
#                 )

class ApprovalRepository:

    def __init__(self, db_connection: DatabaseConnection):
        self.db_connection = db_connection
    
    def find_expense_by_id(self, expense_id: int) -> Optional[Approval]:
        with self.db_connection.get_connection() as conn:
            cursor = conn.execute(
                "SELECT id, expense_id_fk, status, reviewer, comment, review_date FROM approvals WHERE expense_id_fk = ?",
                (expense_id,)
            )
            row = cursor.fetchone()
            if row:
                return Approval(id=row["id"], expense_id_fk=row["expense_id_fk"], status=row["status"], reviewer=row["reviewer"], comment=row["comment"], review_date=row["review_date"] )
        return None