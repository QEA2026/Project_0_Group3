from functools import wraps
from flask import request, jsonify, current_app
from service.authentication_service import AuthenticationService

def get_auth_service() -> AuthenticationService:
    return current_app.auth_service

def require_employee_auth(f):

    @wraps(f)
    def decorated_function(*args, **kwargs):
        token = request.cookies.get("jwt_token")

        if not token:
            return jsonify({'error': 'Authentication required'}), 401

        auth_service = get_auth_service()
        user = auth_service.get_user_by_token(token)

        if not user:
            return jsonify({'error': 'user not found'}), 404
        
        if user.role != 'Employee':
            return jsonify({'error': 'Access denied'}), 403

        request.current_user = user
        return f(*args, **kwargs)
    
    return decorated_function

def get_current_user():
    
    return getattr(request, 'current_user', None)