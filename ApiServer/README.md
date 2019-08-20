# API Server for Indulger

## API Doc

### Request

In most cases, proto should be HTTPS to ensure safety.

```
proto://SERVER_DOMAIN:SERVER_PORT/API_NAME
```

### Common Items

Server response convention:

```javascript
{
    "status": 0, // status code. 0: no error; -1: invalid request; 1: invalid username or password
    "message": "err_msg", // OPTIONAL, useful when status != 0
    "payload": {} // OPTIONAL
}
```

### API names

+ add_user

  Description: Register a new user.

  ```javascript
  {
      "payload": {
          "username": "user",
          "password": "pwd",
          "email": "test@test.com"
      }
  }
  ```

  No payload in response.

+ del_user

  Description: Delete an existing user. Error if user does not exist. **When user is deleted, all JSON strings of this user will be dropped simultaneously.**

  ```javascript
  {
      "payload": {
          "username": "user",
          "password": "pwd"
      }
  }
  ```

  No payload in response.

+ get_user

  Description: Get the info of an existing user. Error if user does not exist.

  ```javascript
  {
      "payload": {
          "username": "user",
          "password": "pwd"
      }
  }
  ```

  Response payload:

  ```javascript
  {
      "email": "test@test.com",
      "register_date": "user_register_date"
  }
  ```

+ check_user

  Description: Validate whether user exists and password is correct. Can be used to check login.

  ```javascript
  {
      "payload": {
          "username": "user",
          "password": "pwd"
      }
  }
  ```

  No payload in response.

+ update_user

  Description: Update user information, password, email, etc. Error if user does not exist.

  ```javascript
  {
      "payload": {
          "username": "user",
          "password": "pwd",
          "new_password": "pwd", // leave this the same as the original val to keep it unchanged
          "email": "new_email@test.com"
      }
  }
  ```

  No payload in response.

+ put_json

  Description: Put a JSON into server storage, indicated by a variable name. Error if user is not authorized. JSON string with the same variable name will be overwritten.

  ```javascript
  {
      "payload": {
          "username": "user",
          "password": "pwd",
          "varname": "variable_name",
          "json": "json_string"
      }
  }
  ```

  No payload in response.

+ get_json

  Description: Given variable name, obtain a JSON string from the server. Error if user is not authorized. If this variable does not exist, the server returns an empty JSON.

  ```javascript
  {
      "payload": {
          "username": "user",
          "password": "pwd",
          "varname": "variable_name"
      }
  }
  ```

  Response payload:

  ```javascript
  {
      "json": "json_string"
  }
  ```

  

