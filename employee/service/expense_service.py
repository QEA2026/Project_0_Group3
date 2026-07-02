from typing import List, Optional, Tuple
from datetime import datetime
from repository.expense_model import Expense
from repository.approval_model import Approval
from repository.expense_repository import ExpenseRepository
from repository.approval_repository import ApprovalRepository

class ExpenseService:

    def __init__(self, expense_repository : ExpenseRepository, approval_repository : ApprovalRepository):
        self.expense_repository = expense_repository
        self.approval_repository = approval_repository
    
    def submit_expense(self, user_id: int, amount: float, description : str, expense_date : str = None) -> Expense:

        if amount <= 0:
            raise ValueError("Amount must be greater than 0")
        
        if not description.strip():
            raise ValueError("Description is required")
        
        if not date:
            date = datetime.now().strftime('%Y-%m-%d')

        expense = Expense(
            id=None,
            user_id=user_id,
            amount=amount,
            description=description.strip(),
            date=date
        )
        
        return self.expense_repository.create(expense)