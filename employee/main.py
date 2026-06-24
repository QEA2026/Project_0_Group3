import os
from pathlib import Path
from dotenv import load_dotenv
from flask import Flask

from repository import (DatabaseConnection , UserRepository , User )

load_dotenv(Path(__file__).parent.parent / ".env")

db_path = os.getenv("APP_DB_PATH", "./db/main.db")  # with default

def create_app():
    db_connection = DatabaseConnection(r"C:\Users\Abdulrahman\Documents\GitHub\Project_0_Group3\db\main")
    db_connection.initialize_database()

    



def create_sample_data():
    db_connection = DatabaseConnection(r"C:\Users\Abdulrahman\Documents\GitHub\Project_0_Group3\db\main")
    db_connection.initialize_database()

    user_repository = UserRepository(db_connection)

    sample_employee = User(
        id=None,
        username="employee1",
        password="password123",
        role="Employee",
    )

    returned_user = user_repository.create(sample_employee)
    print(f"Created sample employee: {returned_user.id} \n {returned_user.username} \n {returned_user.password} \n {returned_user.role} ")




if __name__ == '__main__':
    app = create_app()
    sample = create_sample_data()
    # db_connection = DatabaseConnection()
    # db_connection.initialize_database()
