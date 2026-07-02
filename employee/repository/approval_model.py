from dataclasses import dataclass
from typing import Optional

# approvals(
#                     id INTEGER PRIMARY KEY AUTOINCREMENT,
#                     expense_id_fk INTEGER, FOREIGN KEY (expense_id_fk) REFERENCES expenses(expense_id)
#                     status TEXT,
#                     reviewer INTEGER,
#                     comment TEXT,
#                     review_date TEXT,       
#                 )

@dataclass
class Approval:
    id : Optional[int]
    expense_id_fk: int
    status: str
    # TODO: will probably change this reviewer to fk
    reviewer: Optional[int]
    comment: Optional[str]
    review_date: Optional[str]

    def __post_init__(self):
        if self.status not in ["pending", "approved", "denied"]:
            raise ValueError("status must be 'pending', 'approved', 'denied'")
