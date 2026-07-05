import os
from pathlib import Path
from dotenv import load_dotenv
from flask import Flask


from repository import (DatabaseConnection , UserRepository , User, Expense, ExpenseRepository, Approval, ApprovalRepository )
from service import AuthenticationService, ExpenseService
from api import auth_bp, expense_bp

load_dotenv(Path(__file__).parent.parent / ".env")

db_path = os.getenv("APP_DB_PATH", "../db/main")  # with default
jwt_secret_key = os.getenv("jwt_secret_key")


def create_app():

    app = Flask(__name__, static_folder='static', static_url_path='/static')

    app.config['SECRET_KEY'] = jwt_secret_key
    app.config['JSON_SORT_KEYS'] = False

    db_connection = DatabaseConnection()
    db_connection.initialize_database()

    user_repository = UserRepository(db_connection)
    expense_repository = ExpenseRepository(db_connection)
    approval_repository = ApprovalRepository(db_connection)

    auth_service = AuthenticationService(user_repository)
    app.auth_service = auth_service

    expense_service = ExpenseService(expense_repository, approval_repository)
    app.expense_service = expense_service

    app.register_blueprint(auth_bp)
    app.register_blueprint(expense_bp)

    return app



def create_sample_data():
    db_connection = DatabaseConnection()
    db_connection.initialize_database()

    user_repository = UserRepository(db_connection)

    sample_employee = User(
        id=None,
        username="employee2",
        password="password12345678",
        role="Employee",
        )
    returned_user = user_repository.create(sample_employee)
    print(f"Created sample employee: \n{returned_user.id} \n{returned_user.username} \n{returned_user.password} \n{returned_user.role} ")

    
    # sample_expense_repo = ExpenseRepository(db_connection)

    # temp_expense = Expense(
    #         id=None,
    #         amount = 150.50,
    #         description="Plane tickets",
    #         expense_date="06/25/2026",
    #         user_id_fk=1
    #     )
    
    # sample_expense_repo.create(temp_expense)
    # print(sample_expense_repo.find_by_id(1))
    

    # temp_expense_update = Expense(
    #     id=1,
    #     amount = 175.50,
    #     description="Plane tickets and uber to work",
    #     expense_date="06/25/2026",
    #     user_id_fk=10,
    # )
    
    # print(sample_expense_repo.update(temp_expense_update))
    # print(sample_expense_repo.find_by_user_id(1))
    # print(sample_expense_repo.delete(1))

    # approval_repository = ApprovalRepository(db_connection)

    # print(approval_repository.find_expense_by_user_id_with_status(1))
    # print(approval_repository.update_status(1, "approved"))
    # print(approval_repository.find_expense_by_user_id_with_status(1))


if __name__ == '__main__':
    # app = create_app()
    # sample = create_sample_data()
    # db_connection = DatabaseConnection()
    # db_connection.initialize_database()
    # user_repo = UserRepository(db_connection)

    # target_user = user_repo.find_by_id(2)
    # hashed_password = target_user.password

    # input_password = "super secret password".encode()

    # if bcrypt.checkpw(input_password, hashed_password):
    #     print("It Matches!")
    # else:
    #     print("It Does not Match :(")
    # print(bcrypt.checkpw(input_password, hashed_password))

    # auth_service = AuthenticationService(user_repo)
    # temp = auth_service.get_user_by_id(2)
    # temp_token = auth_service.generate_jwt_token(temp)
    # print(f"Before: \n{temp} \n" )
    
    # temp_validate = auth_service.validate_jwt_token(temp_token)
    # print(f"\n After: \n{temp_validate} \n" )

    # temp_get_user = auth_service.get_user_by_token(temp_token)
    # print(temp_get_user)

    # print(jwt_secret_key)
    
    app = create_app()

    print("Starting Employee Expense Management API...")
    print("Available endpoints:")
    print("  POST /api/auth/login - Employee login")
    print("  POST /api/auth/login - Register Employee")
    print("  POST /api/auth/logout - Employee logout")
    print("  GET  /api/auth/status - Check auth status")
    print("  POST /api/expenses - Submit new expense")
    print("  GET  /api/expenses - Get all user expenses")
    print("  GET  /api/expenses/<id> - Get specific expense")
    print("  PUT  /api/expenses/<id> - Update expense (if pending)")
    print("  DELETE /api/expenses/<id> - Delete expense (if pending)")
    print("  GET  /health - Health check")
    print("  GET  /api - API info")
    print()
    print("Sample credentials:")
    print("  Employee: employee1/password123")

    app.run(host='0.0.0.0', port=5000)
    
    


















