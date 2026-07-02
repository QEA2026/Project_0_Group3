from dataclasses import dataclass
from typing import Optional

@dataclass
class User:
    id: Optional[int]
    username: str 
    password: str
    role: str

    def __post_init__(self):
        if self.role != 'Employee':
            raise ValueError("Role must be 'Employee'")

#     user_id INTEGER PRIMARY KEY AUTOINCREMENT,
#     username TEXT UNIQUE NOT NULL,
#     password TEXT NOT NULL,
#     role TEXT NOT NULL`