from typing import Optional
from .user_model import User
from .database import DatabaseConnection
import bcrypt

class UserRepository:
        
    def __init__(self, db_connection: DatabaseConnection):
        
        self.db_connection = db_connection

    def find_by_username(self, username: str) -> Optional[User]:
        with self.db_connection.get_connection() as conn:
            cursor = conn.execute(
                "SELECT id, username, password, role FROM users WHERE username = ?",
                (username,)
            )
            row = cursor.fetchone()
            if row:
                return User(id=row['id'], username=row['username'], password=row['password'], role=row['role'])
        return None
    
    def find_by_id(self, id : int) -> Optional[User]:
        with self.db_connection.get_connection() as conn:
            cursor = conn.execute(
                "SELECT id, username, password, role FROM users WHERE id = ?",
                (id,)
            )
            row = cursor.fetchone()
            if row:
                return User(id=row['id'], username=row['username'], password=row['password'], role=row['role'])
        return None
        
    def create(self, user: User) -> User:
        
        temp_password = user.password.encode()
        hashed = bcrypt.hashpw(temp_password, bcrypt.gensalt())

        with self.db_connection.get_connection() as conn:
            cursor = conn.execute(
                "INSERT INTO users (username, password, role) VALUES (?,?,?)",
                (user.username, hashed, user.role)
            )
            user.id = cursor.lastrowid
            
            conn.commit()

        return user