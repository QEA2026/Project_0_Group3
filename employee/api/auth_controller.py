from flask import Blueprint, request, jsonify, make_response , current_app
from service.authentication_service import AuthenticationService
from repository.user_model import User
from repository.user_repository import UserRepository
from repository.database import DatabaseConnection

auth_bp = Blueprint('auth', __name__ ,  url_prefix = "/api/auth")

def get_auth_service() -> AuthenticationService:
    return current_app.auth_service

@auth_bp.route("/register", methods = ["POST"])
def register():

    try :
        data = request.get_json()
        if not data:
            return jsonify({'error': 'JSON data required'}), 400

        temp_user_name = str(data.get('username')).strip()
        temp_password = data.get('password')

        if not temp_user_name or not temp_password:
            jsonify({'error': 'username and password are required'}), 400

        payload = User(
            id= None,
            username = temp_user_name,
            password = temp_password,
            role = "Employee",
            )
        
        database_connection = DatabaseConnection()

        user_repo = UserRepository(database_connection)

        user = user_repo.create(payload)
        
        auth_service = get_auth_service()
        token = auth_service.generate_jwt_token(payload)


        response_data = {
            'message': 'Registered successfully',
            'user': {
                'id': user.id,
                'username': user.username,
                'role': user.role
            }
        }
        response = make_response(jsonify(response_data), 201)

        response.set_cookie(
            'jwt_token',
            token,
            httponly=True,
            secure=False,  # Set to True in production with HTTPS
            samesite='Lax',
            max_age=24*60*60  # 24 hours in seconds
        )

        return response
    
    except Exception as e:
        return jsonify({'error': 'register failed', 'details': str(e)}), 500



@auth_bp.route("/login", methods = ["POST"])
def login():

    try :

        data = request.get_json()

        if not data:
            return jsonify({'error': 'JSON data required'}), 400
        
        username = data.get("username")
        password = data.get("password")

        if not username or not password:
            return jsonify({'error': 'Username and password required'}), 400
        
        auth_service = get_auth_service()
        user = auth_service.authenticate_user(username, password)

        if not user:
            return jsonify({'error': 'Invalid credentials'}), 401

        token = auth_service.generate_jwt_token(user)

        response_data = {
            'message': 'Login successful',
            'user': {
                'id': user.id,
                'username': user.username,
                'role': user.role
            }
        }
        response = make_response(jsonify(response_data))

        response.set_cookie(
            'jwt_token',
            token,
            httponly=True,
            secure=False,  # Set to True in production with HTTPS
            samesite='Lax',
            max_age=24*60*60  # 24 hours in seconds
        )

        return response
    
    except Exception as e:
        return jsonify({'error': 'Login failed', 'details': str(e)}), 500


@auth_bp.route("/logout", methods = ["POST"])
def logout():

    cookies= request.cookies
    if not cookies:
        return jsonify({'error': 'already logged out'}), 400

    response = make_response(jsonify({'message': 'Logout successful'}))
    
    # Clear the JWT token cookie
    response.set_cookie(
        'jwt_token',
        '',
        httponly=True,
        secure=False,  # Set to True in production with HTTPS
        samesite='Lax',
        expires=0  # Expire immediately
    )
    
    return response


@auth_bp.route("/status", methods = ["GET"])
def status():
    token = request.cookies.get("jwt_token")

    if not token:
        return jsonify({'authenticated': False}), 200
    
    try: 
        auth_service = get_auth_service()
        user = auth_service.get_user_by_token(token)

        if user:
            return jsonify({
                'authenticated': True,
                'user': {
                    'id': user.id,
                    'username': user.username,
                    'role': user.role
                }
            })
    
    except Exception:
        pass
    
    return jsonify({'authenticated': False}), 200

    


        

