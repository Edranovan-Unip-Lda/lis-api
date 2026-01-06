# User Profile Update Endpoint

## Overview
This endpoint allows authenticated users to update their own profile information. Users can only update their own profile (User A can only update User A's information, not User B's).

## Endpoint Details

### Update Own Profile
**Endpoint:** `PATCH /api/v1/users/{username}/profile`

**Authentication:** Required (JWT token in HttpOnly cookie)

**Authorization:** Any authenticated user can update only their own profile

### Request

**Path Parameter:**
- `username` (String) - The username of the profile to update (must match authenticated user's username)

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "currentPassword": "oldPassword123",  // Optional: only if changing password
  "newPassword": "newPassword456"        // Optional: only if changing password
}
```

**Required Fields:**
- `firstName` - User's first name (not blank)
- `lastName` - User's last name (not blank)
- `email` - User's email address (valid email format)

**Optional Fields:**
- `currentPassword` - Required if changing password
- `newPassword` - Required if changing password

### Response

**Success Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "username": "johndoe",
  "email": "john.doe@example.com",
  "role": {
    "id": 2,
    "name": "ROLE_CLIENT"
  },
  "status": "active",
  "createdAt": "2025-01-01T10:00:00Z",
  "updatedAt": "2025-01-06T15:30:00Z"
}
```

**Error Responses:**

1. **403 Forbidden** - User attempting to update another user's profile
```json
{
  "message": "Você só pode atualizar o seu próprio perfil",
  "timestamp": "2025-01-06T15:30:00Z"
}
```

2. **400 Bad Request** - Current password is incorrect when changing password
```json
{
  "message": "A palavra-passe atual está incorreta",
  "timestamp": "2025-01-06T15:30:00Z"
}
```

3. **409 Conflict** - Email already in use by another user
```json
{
  "message": "O email john@example.com já está em uso por outro utilizador",
  "timestamp": "2025-01-06T15:30:00Z"
}
```

4. **404 Not Found** - User not found
```json
{
  "message": "Utilizador com o nome johndoe não encontrado",
  "timestamp": "2025-01-06T15:30:00Z"
}
```

## Security Features

1. **Authentication Required:** Only authenticated users with valid JWT tokens can access this endpoint
2. **Self-Update Only:** The system verifies that the authenticated username matches the target username in the path
3. **Password Verification:** When changing password, the current password must be provided and verified
4. **Email Uniqueness:** The system checks that the new email is not already used by another user
5. **Password Encryption:** New passwords are encrypted using BCrypt before storage

## Usage Examples

### Example 1: Update Basic Profile Information
```bash
curl -X PATCH 'http://localhost:8080/api/v1/users/johndoe/profile' \
  -H 'Content-Type: application/json' \
  -H 'Cookie: jwt=eyJhbGc...' \
  -d '{
    "firstName": "John",
    "lastName": "Smith",
    "email": "john.smith@example.com"
  }'
```

### Example 2: Update Profile and Change Password
```bash
curl -X PATCH 'http://localhost:8080/api/v1/users/johndoe/profile' \
  -H 'Content-Type: application/json' \
  -H 'Cookie: jwt=eyJhbGc...' \
  -d '{
    "firstName": "John",
    "lastName": "Smith",
    "email": "john.smith@example.com",
    "currentPassword": "oldPassword123",
    "newPassword": "newSecurePassword456"
  }'
```

### Example 3: Attempting to Update Another User's Profile (Will Fail)
```bash
curl -X PATCH 'http://localhost:8080/api/v1/users/anothername/profile' \
  -H 'Content-Type: application/json' \
  -H 'Cookie: jwt=eyJhbGc...' \
  -d '{
    "firstName": "Hacker",
    "lastName": "Attempt",
    "email": "hacker@example.com"
  }'

# Response: 403 Forbidden
# {"message": "Você só pode atualizar o seu próprio perfil"}
```

## Implementation Details

### Components Created/Modified

1. **UserProfileUpdateDto** - New DTO for profile update requests
   - Location: `tl.gov.mci.lis.dtos.user.UserProfileUpdateDto`
   - Contains validated fields for profile updates

2. **UserServices.updateOwnProfile()** - New service method
   - Location: `tl.gov.mci.lis.services.user.UserServices`
   - Handles business logic and security validation
   - Verifies user can only update own profile
   - Validates current password before allowing password change
   - Checks email uniqueness

3. **UserController.updateOwnProfile()** - New endpoint
   - Location: `tl.gov.mci.lis.controllers.UserController`
   - Extracts authenticated user from SecurityContext
   - Validates request and delegates to service layer

## Notes

- The endpoint uses `PATCH` method as it performs a partial update
- Password change is optional - users can update profile without changing password
- If changing password, both `currentPassword` and `newPassword` must be provided
- The authenticated username is extracted from the JWT token automatically
- All validations are performed server-side for security

