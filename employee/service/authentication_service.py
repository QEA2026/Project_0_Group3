# TODO: implement this in authenticate_user():
# db_connection = DatabaseConnection()
# db_connection.initialize_database()
# user_repo = UserRepository(db_connection)
# target_user = user_repo.find_by_id(2)
#     hashed_password = target_user.password

#     input_password = "hashed".encode()

#     if bcrypt.checkpw(input_password, hashed_password):
#         print("It Matches!")
#     else:
#         print("It Does not Match :(")
import os
import jwt
import bcrypt
from datetime import datetime, timedelta
from typing import Optional, Dict, Any
from repository.user_model import User
from repository.user_repository import UserRepository

# print(jwt)
# print(jwt.__file__)

class AuthenticationService:
    def __init__(self, user_repository : UserRepository, jwt_secret_key : str = None):
        self.user_repository = user_repository
        self.jwt_secret_key = jwt_secret_key or os.getenv("jwt_secret_key")
        self.jwt_algorithm = 'HS256'
        self.token_expiry_hours = 24

    def authenticate_user(self, username : str, password : str) -> Optional[User]:
        user = self.user_repository.find_by_username(username)
        input_password = password.encode()
        if bcrypt.checkpw(input_password, user.password):
            return user
        return None
    
    def get_user_by_id(self, user_id : int) -> Optional[User]:
        
        return self.user_repository.find_by_id(user_id)
    
    def generate_jwt_token(self, user: User) -> str:
        payload = {
            "user_id" : user.id,
            "username" : user.username,
            "role" : user.role,
            "exp" : datetime.utcnow() + timedelta(hours=self.token_expiry_hours),
            "iat" : datetime.utcnow()
        }
        # print(payload)
        return jwt.encode(payload, self.jwt_secret_key, algorithm = self.jwt_algorithm)
    

    def validate_jwt_token(self, token : str) -> Optional[Dict[str, Any]]:
        try:
            payload = jwt.decode(token, self.jwt_secret_key, algorithms = [self.jwt_algorithm])
            return payload
        except jwt.ExpiredSignatureError:
            return None
        except jwt.InvalidTokenError:
            return None

    def get_user_by_token(self, token: str) -> Optional[User]:
        payload = self.validate_jwt_token(token)
        if payload:
            return self.get_user_by_id(payload["user_id"])    
        return None



