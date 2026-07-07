from typing import List, Optional, Tuple
from .approval_model import Approval
from .expense_model import Expense
from .database import DatabaseConnection


# approvals(
#                     id INTEGER PRIMARY KEY AUTOINCREMENT,
#                     expense_id_fk INTEGER, FOREIGN KEY (expense_id_fk) REFERENCES expenses(id)
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
                return Approval(id=row["id"], expense_id_fk=row["expense_id_fk"], status=row["status"].lower(), reviewer=row["reviewer"], comment=row["comment"], review_date=row["review_date"] )
        return None
    
    def find_expense_by_user_id_with_status(self, user_id : int) -> List[tuple]:
        results = []

        with self.db_connection.get_connection() as conn:

            cursor = conn.execute('''
                SELECT
                    e.id,
                    e.amount,
                    e.description,
                    e.expense_date,
                    e.category,
                    a.id AS approval_id,
                    a.expense_id_fk,
                    a.status,
                    a.reviewer,
                    a.comment,
                    a.review_date
                FROM expenses e
                JOIN approvals a ON e.id = a.expense_id_fk
                WHERE e.user_id_fk = ?
                ORDER BY e.expense_date DESC
            ''',(user_id,))

            for row in cursor.fetchall():
                expense = Expense(id=row["id"], amount=row["amount"], description=row["description"], expense_date=row["expense_date"], category=row["category"], user_id_fk=user_id)
                approval = Approval(
                    id=row["approval_id"],
                    expense_id_fk=row["expense_id_fk"],
                    status=row["status"].lower(),
                    reviewer=row["reviewer"],
                    comment=row["comment"],
                    review_date=row["review_date"]
                )
                results.append((expense, approval))
            return results
    
    def update_status(self, expense_id: int, status: str, reviewer: Optional[int] = None, 
                     comment: Optional[str] = None, review_date: Optional[str] = None) -> bool:
        
        with self.db_connection.get_connection() as conn:
            cursor = conn.execute(
                "UPDATE approvals SET status = ?, reviewer = ?, comment = ?, review_date = ? WHERE expense_id_fk = ?"            
                , (status, reviewer, comment, review_date, expense_id)
            )
            conn.commit()
            return cursor.rowcount > 0
