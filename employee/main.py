import os
from threading import Thread
from getpass import getpass
from datetime import datetime
from typing import Any, Dict, List, Optional

import requests
from flask import Flask
from werkzeug.serving import make_server



from repository import (DatabaseConnection , UserRepository , User, Expense, ExpenseRepository, Approval, ApprovalRepository )
from service import AuthenticationService, ExpenseService
from api import auth_bp, expense_bp



db_path = os.getenv("APP_DB_PATH", "../db/main")  # with default
jwt_secret_key = os.getenv("jwt_secret_key", "expense_manager_abdulrahman_scot_2026")
LOCAL_API_URL = "http://127.0.0.1:5000"
ALLOWED_CATEGORIES = ["Travel", "Lodging", "Meals", "Office Supplies", "Software", "Training", "Other"]


def create_app():

    app = Flask(__name__, static_folder='static', static_url_path='/static')

    app.config['SECRET_KEY'] = jwt_secret_key
    app.config['JSON_SORT_KEYS'] = False

    db_connection = DatabaseConnection()
    db_connection.initialize_database()

    user_repository = UserRepository(db_connection)
    expense_repository = ExpenseRepository(db_connection)
    approval_repository = ApprovalRepository(db_connection)

    auth_service = AuthenticationService(user_repository, jwt_secret_key)
    app.auth_service = auth_service

    expense_service = ExpenseService(expense_repository, approval_repository)
    app.expense_service = expense_service

    app.register_blueprint(auth_bp)
    app.register_blueprint(expense_bp)

    return app





# def create_sample_data():
#     db_connection = DatabaseConnection()
#     db_connection.initialize_database()

#     user_repository = UserRepository(db_connection)

#     sample_employee = User(
#         id=None,
#         username="employee2",
#         password="password12345678",
#         role="Employee",
#         )
#     returned_user = user_repository.create(sample_employee)
#     print(f"Created sample employee: \n{returned_user.id} \n{returned_user.username} \n{returned_user.password} \n{returned_user.role} ")


     
    
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

    

    





class EmployeeApiClient:
    def __init__(self):
        self.session = requests.Session()
        self.current_user: Optional[Dict[str, Any]] = None

    def _request(self, method: str, path: str, **kwargs) -> Dict[str, Any]:
        url = f"{LOCAL_API_URL}{path}"
        try:
            response = self.session.request(method, url, timeout=10, **kwargs)
        except requests.RequestException as exc:
            raise RuntimeError(
                "Could not reach the API on port 5000. Is the Flask server running?"
            ) from exc

        try:
            data = response.json()
        except ValueError:
            data = {"error": response.text or "Unexpected API response"}

        if response.status_code >= 400:
            message = data.get("error") or data.get("message") or "Request failed"
            details = data.get("details")
            if details:
                message = f"{message}: {details}"
            raise RuntimeError(message)

        return data

    def login(self, username: str, password: str) -> Dict[str, Any]:
        data = self._request(
            "POST",
            "/api/auth/login",
            json={"username": username, "password": password},
        )
        self.current_user = data.get("user")
        return data

    def register(self, username: str, password: str) -> Dict[str, Any]:
        data = self._request(
            "POST",
            "/api/auth/register",
            json={"username": username, "password": password},
        )
        self.current_user = data.get("user")
        return data

    def logout(self) -> None:
        self._request("POST", "/api/auth/logout")
        self.current_user = None

    def submit_expense(self, amount: float, description: str, date: Optional[str], category: str) -> Dict[str, Any]:
        payload = {"amount": amount, "description": description, "date": date or "", "category": category}
        return self._request("POST", "/api/expense/submit", json=payload)

    def get_expenses(self, status: Optional[str] = None) -> List[Dict[str, Any]]:
        params = {"status": status} if status else None
        data = self._request("GET", "/api/expense/get", params=params)
        return data.get("expenses", [])

    def get_expense(self, expense_id: int) -> Dict[str, Any]:
        data = self._request("GET", f"/api/expense/get/{expense_id}")
        return data.get("expense", {})

    def update_expense(
        self,
        expense_id: int,
        amount: float,
        description: str,
        date: str,
        category: str,
    ) -> Dict[str, Any]:
        return self._request(
            "PUT",
            f"/api/expense/update/{expense_id}",
            json={"amount": amount, "description": description, "date": date, "category": category},
        )

    def delete_expense(self, expense_id: int) -> Dict[str, Any]:
        return self._request("DELETE", f"/api/expense/delete/{expense_id}")


def prompt_required(prompt: str) -> str:
    while True:
        value = input(prompt).strip()
        if value:
            return value
        print("Please enter a value.")


def prompt_float(prompt: str, default: Optional[float] = None) -> float:
    while True:
        value = input(prompt).strip()
        if not value and default is not None:
            return default
        try:
            number = float(value)
            if number > 0:
                return number
            print("Amount must be greater than 0.")
        except ValueError:
            print("Please enter a valid number.")


def prompt_int(prompt: str) -> int:
    while True:
        value = input(prompt).strip()
        try:
            return int(value)
        except ValueError:
            print("Please enter a whole number.")


def prompt_category(default: Optional[str] = None) -> str:
    while True:
        print("Category:")
        for index, category in enumerate(ALLOWED_CATEGORIES, start=1):
            default_marker = " (current)" if category == default else ""
            print(f"{index}. {category}{default_marker}")

        prompt = "Choose category"
        if default:
            prompt += f" [{default}]"
        choice = input(f"{prompt}: ").strip()

        if not choice and default:
            return default

        if choice.isdigit():
            selected_index = int(choice)
            if 1 <= selected_index <= len(ALLOWED_CATEGORIES):
                return ALLOWED_CATEGORIES[selected_index - 1]

        for category in ALLOWED_CATEGORIES:
            if choice.lower() == category.lower():
                return category

        print("Please choose a valid category.")


def prompt_optional_date(prompt: str, default: Optional[str] = None) -> Optional[str]:
    while True:
        label = prompt
        if default:
            label += f" [{default}]"
        date_input = input(f"{label}: ").strip()

        if not date_input and default is not None:
            return default

        if date_input.lower() in ("", "none", "null"):
            return None

        try:
            datetime.strptime(date_input, "%Y-%m-%d")
            return date_input
        except ValueError:
            print("Please enter the date in YYYY-MM-DD format, or press Enter to auto-generate.")


def prompt_password(prompt: str) -> str:
    if os.name != "nt":
        return getpass(prompt)

    import msvcrt

    print(prompt, end="", flush=True)
    password = []

    while True:
        char = msvcrt.getwch()

        if char in ("\r", "\n"):
            print()
            return "".join(password)

        if char == "\003":
            raise KeyboardInterrupt

        if char == "\b":
            if password:
                password.pop()
                print("\b \b", end="", flush=True)
            continue

        if char in ("\x00", "\xe0"):
            msvcrt.getwch()
            continue

        password.append(char)
        print("*", end="", flush=True)


def print_expenses(expenses: List[Dict[str, Any]]) -> None:
    if not expenses:
        print("No expenses found.")
        return

    expenses = sorted(
        expenses,
        key=lambda expense: (
            str(expense.get("status") or "").lower(),
            str(expense.get("date") or ""),
            expense.get("id") or 0
        )
    )

    columns = {
        "id": 6,
        "amount": 13,
        "status": 12,
        "date": 14,
        "category": 20,
        "description": 34,
        "review_date": 16,
        "comment": 44,
    }
    gap = "  "
    header = (
        f"{'ID':<{columns['id']}}"
        f"{gap}{'Amount':>{columns['amount']}}"
        f"{gap}{'Status':<{columns['status']}}"
        f"{gap}{'Date':<{columns['date']}}"
        f"{gap}{'Category':<{columns['category']}}"
        f"{gap}{'Description':<{columns['description']}}"
        f"{gap}{'Review Date':<{columns['review_date']}}"
        f"{gap}{'Comment':<{columns['comment']}}"
    )

    print()
    print(header)
    print("-" * len(header))
    for expense in expenses:
        amount = float(expense.get("amount", 0))
        amount_text = f"${amount:.2f}"
        status = expense.get("status") or "N/A"
        date = expense.get("date") or "N/A"
        category = expense.get("category") or "N/A"
        description = expense.get("description") or "N/A"
        comment = expense.get("comment") or "N/A"
        review_date = expense.get("review_date") or "N/A"
        print(
            f"{str(expense.get('id', '')):<{columns['id']}}"
            f"{gap}{amount_text:>{columns['amount']}}"
            f"{gap}{str(status):<{columns['status']}}"
            f"{gap}{str(date):<{columns['date']}}"
            f"{gap}{str(category):<{columns['category']}}"
            f"{gap}{str(description):<{columns['description']}}"
            f"{gap}{str(review_date):<{columns['review_date']}}"
            f"{gap}{str(comment):<{columns['comment']}}"
        )
    print()


def login_flow(client: EmployeeApiClient) -> bool:
    for attempt in range(1, 4):
        username = prompt_required("Username: ")
        password = prompt_password("Password: ")
        try:
            result = client.login(username, password)
            user = result.get("user", {})
            print()
            print(f"Hi {user.get('username')}!")
            return True
        except RuntimeError as exc:
            print(f"Login failed: {exc}")
            if attempt < 3:
                print("Try again.")

    return False


def register_flow(client: EmployeeApiClient) -> bool:
    username = prompt_required("Choose a username: ")

    while True:
        password = prompt_password("Choose a password: ")
        confirm_password = prompt_password("Confirm password: ")

        if password and password == confirm_password:
            break
        print("Passwords must match and cannot be blank.")

    try:
        result = client.register(username, password)
        user = result.get("user", {})
        print()
        print(f"Hi {user.get('username')}!")
        return True
    except RuntimeError as exc:
        print(f"Register failed: {exc}")
        return False


def auth_menu(client: EmployeeApiClient) -> bool:
    while True:
        print()
        print("Employee Expense CLI")
        print("1. Login")
        print("2. Register")
        print("3. Exit")
        choice = input("Choose an option: ").strip()

        if choice == "1":
            if login_flow(client):
                return True
        elif choice == "2":
            if register_flow(client):
                return True
        elif choice == "3":
            return False
        else:
            print("Choose a menu option from 1 to 3.")


def submit_expense_flow(client: EmployeeApiClient) -> None:
    amount = prompt_float("Amount: ")
    description = prompt_required("Description: ")
    category = prompt_category()
    date = prompt_optional_date("Date YYYY-MM-DD (press Enter to auto-generate)")
    if date is None:
        print("No date entered. The API will auto-generate it.")

    result = client.submit_expense(amount, description, date, category)
    expense = result.get("expense", {})
    print()
    print("Expense submitted successfully.")
    print(f"ID: {expense.get('id')}")
    print(f"Amount: ${float(expense.get('amount', 0)):,.2f}")
    print(f"Description: {expense.get('description')}")
    print(f"Category: {expense.get('category')}")
    print(f"Date: {expense.get('date')}")
    print(f"Status: {expense.get('status')}")
    print()


def view_expenses_flow(client: EmployeeApiClient) -> None:
    status = input("Filter by status (pending/approved/denied, blank for all): ").strip().lower()
    if status not in {"pending", "approved", "denied"}:
        status = None
    print_expenses(client.get_expenses(status))


def edit_expense_flow(client: EmployeeApiClient) -> None:
    expense_id = prompt_int("Expense ID to edit: ")
    expense = client.get_expense(expense_id)

    if expense.get("status") != "pending":
        print("Only pending expenses can be edited.")
        return

    current_amount = float(expense.get("amount", 0))
    current_description = expense.get("description", "")
    current_date = expense.get("date", "")
    current_category = expense.get("category", "Other")

    print("Press Enter to keep the current value.")
    amount = prompt_float(f"Amount [{current_amount:.2f}]: ", default=current_amount)
    description = input(f"Description [{current_description}]: ").strip() or current_description
    category = prompt_category(current_category)
    date = prompt_optional_date("Date YYYY-MM-DD", current_date)

    result = client.update_expense(expense_id, amount, description, date, category)
    updated = result.get("expense", {})
    print()
    print("Expense updated successfully.")
    print(f"ID: {updated.get('id')}")
    print(f"Amount: ${float(updated.get('amount', 0)):,.2f}")
    print(f"Description: {updated.get('description')}")
    print(f"Category: {updated.get('category')}")
    print(f"Date: {updated.get('date')}")
    print("Status: pending")
    print()


def delete_expense_flow(client: EmployeeApiClient) -> None:
    expense_id = prompt_int("Expense ID to delete: ")
    expense = client.get_expense(expense_id)

    if expense.get("status") != "pending":
        print("Only pending expenses can be deleted.")
        return

    while True:
        confirm = input(f"Delete expense #{expense_id}? (y/n): ").strip().lower()
        if confirm in ("y", "n"):
            break
        print("Please enter y or n.")

    if confirm == "n":
        print("Delete canceled.")
        return

    client.delete_expense(expense_id)
    print(f"Deleted expense #{expense_id}.")


def view_history_flow(client: EmployeeApiClient) -> None:
    expenses = client.get_expenses()
    reviewed_expenses = [
        expense for expense in expenses
        if str(expense.get("status", "")).lower() in ("approved", "denied")
    ]
    pending_count = sum(
        1 for expense in expenses
        if str(expense.get("status", "")).lower() == "pending"
    )

    approved_total = sum(
        float(expense.get("amount", 0))
        for expense in reviewed_expenses
        if str(expense.get("status", "")).lower() == "approved"
    )
    denied_total = sum(
        float(expense.get("amount", 0))
        for expense in reviewed_expenses
        if str(expense.get("status", "")).lower() == "denied"
    )

    if not reviewed_expenses:
        print("No approved or denied expenses found.")
        if pending_count:
            print(f"{pending_count} pending expense(s) are waiting for manager review and are not included in history.")
        print()
        print("History totals:")
        print(f"Approved total: ${approved_total:,.2f}")
        print(f"Denied total:   ${denied_total:,.2f}")
        print()
        return

    print_expenses(reviewed_expenses)

    print("History totals:")
    print(f"Approved total: ${approved_total:,.2f}")
    print(f"Denied total:   ${denied_total:,.2f}")
    print()


def menu_loop(client: EmployeeApiClient) -> None:
    actions = {
        "1": submit_expense_flow,
        "2": view_expenses_flow,
        "3": edit_expense_flow,
        "4": delete_expense_flow,
        "5": view_history_flow,
    }

    while True:
        print()
        print("1. Submit Expense")
        print("2. View My Expenses")
        print("3. Edit Pending Expense")
        print("4. Delete Pending Expense")
        print("5. View Approved/Denied History")
        print("6. Logout")
        print()
        choice = input("Choose an option: ").strip()

        if choice == "6":
            try:
                client.logout()
            except RuntimeError as exc:
                print(f"Logout warning: {exc}")
            print("Logged out.")
            return

        action = actions.get(choice)
        if not action:
            print("Choose a menu option from 1 to 6.")
            continue

        try:
            action(client)
        except RuntimeError as exc:
            print(f"Error: {exc}")


def run_employee_cli() -> None:
    client = EmployeeApiClient()
    if auth_menu(client):
        menu_loop(client)


def cli_main() -> None:
    run_employee_cli()


class ApiServerThread(Thread):
    def __init__(self, app: Flask):
        super().__init__(daemon=True)
        self.server = make_server("127.0.0.1", 5000, app, threaded=True)

    def run(self) -> None:
        self.server.serve_forever()

    def shutdown(self) -> None:
        self.server.shutdown()


def api_is_running() -> bool:
    try:
        requests.get(f"{LOCAL_API_URL}/api/auth/status", timeout=1)
        return True
    except requests.RequestException:
        return False


def start_api_server() -> Optional[ApiServerThread]:
    if api_is_running():
        print("Using Employee Expense Management API already running on port 5000.")
        return None

    app = create_app()
    server = ApiServerThread(app)
    server.start()
    print()
    print("Employee Expense Management API started on port 5000.")
    print()
    return server






if __name__ == '__main__':
    api_server = start_api_server()
    try:
        run_employee_cli()
    finally:
        if api_server:
            api_server.shutdown()
    
    

















