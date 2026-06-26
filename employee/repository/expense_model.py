from dataclasses import dataclass
from typing import Optional

# CREATE TABLE IF NOT EXISTS expenses(
#                     expense_id INTEGER PRIMARY KEY AUTOINCREMENT,
#                     amount REAL,
#                     description TEXT,
#                     expense_date TEXT,
#                     user_id_fk INTEGER NOT NULL,
#                     FOREIGN KEY (user_id_fk) REFERENCES users(user_id)
#                 )

@dataclass
class Expense:
    id: Optional[int]
    amount: float
    description: str
    expense_date: str
    user_id_fk: int

