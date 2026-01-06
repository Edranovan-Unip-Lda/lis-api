# User Profile Update Feature - Implementation Summary

## Overview
A new endpoint has been implemented to allow users to update their own profile information. This feature ensures that User A can only update User A's information and cannot update User B's profile.

## Changes Made

### 1. New DTO Created
**File:** `src/main/java/tl/gov/mci/lis/dtos/user/UserProfileUpdateDto.java`

A new Data Transfer Object for handling profile update requests with validation:
- `firstName` (required, not blank)
- `lastName` (required, not blank)
- `email` (required, valid email format)
- `currentPassword` (optional - only required when changing password)
- `newPassword` (optional - only required when changing password)

### 2. Service Layer Enhancement
**File:** `src/main/java/tl/gov/mci/lis/services/user/UserServices.java`

Added new method: `updateOwnProfile()`

**Key Features:**
- Validates that authenticated user matches the target user
- Checks email uniqueness before updating
- Verifies current password before allowing password change
- Encrypts new password using BCrypt
- Comprehensive error handling and logging
- Transactional to ensure data consistency

**Security Validations:**
1. ✅ Authenticated username must match target username
2. ✅ Email uniqueness check (excludes current user)
3. ✅ Current password verification before password change
4. ✅ New password encryption

### 3. Controller Layer Enhancement
**File:** `src/main/java/tl/gov/mci/lis/controllers/UserController.java`

Added new endpoint: `PATCH /api/v1/users/{username}/profile`

**Key Features:**
- Extracts authenticated user from SecurityContext
- Validates request using Jakarta Bean Validation
- Returns updated user details
- Proper HTTP status codes (200 OK for success, 403 for forbidden, etc.)

**Imports Added:**
- `UserProfileUpdateDto` - The new DTO
- `Authentication` and `SecurityContextHolder` - For authentication checks

### 4. Unit Tests
**File:** `src/test/java/tl/gov/mci/lis/controllers/UserControllerProfileUpdateTest.java`

Comprehensive unit tests covering:
1. ✅ Successful profile update without password change
2. ✅ Successful profile update with password change
3. ✅ Forbidden exception when attempting to update another user's profile

### 5. Documentation
**File:** `docs/USER_PROFILE_UPDATE.md`

Complete API documentation including:
- Endpoint details and authentication requirements
- Request/response examples
- Error responses
- Security features
- Usage examples with curl commands
- Implementation details

## API Endpoint Details

### Endpoint
```
PATCH /api/v1/users/{username}/profile
```

### Authentication
- Required: Yes (JWT token in HttpOnly cookie)
- Authorization: User can only update their own profile

### Request Example
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "currentPassword": "oldPassword123",
  "newPassword": "newPassword456"
}
```

### Success Response (200 OK)
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
  "status": "active"
}
```

## Security Implementation

### How It Works

1. **Request Arrives**: User sends PATCH request to `/api/v1/users/{username}/profile`

2. **JWT Validation**: `JwtAuthFilter` validates the JWT token from HttpOnly cookie

3. **Authentication Context**: User details are loaded into `SecurityContext`

4. **Controller**: 
   - Extracts authenticated username from `SecurityContext`
   - Passes both authenticated username and target username to service

5. **Service Layer Validation**:
   - Compares authenticated username with target username
   - Throws `ForbiddenException` if they don't match
   - Validates email uniqueness
   - Verifies current password if password change is requested

6. **Response**: Returns updated user details or appropriate error response

### Security Measures

1. **Self-Update Only**: 
   ```java
   if (!authenticatedUsername.equals(targetUsername)) {
       throw new ForbiddenException("Você só pode atualizar o seu próprio perfil");
   }
   ```

2. **Email Uniqueness**:
   ```java
   if (!user.getEmail().equals(email)) {
       userRepository.findByEmail(email).ifPresent(existingUser -> {
           if (!existingUser.getId().equals(user.getId())) {
               throw new AlreadyExistException("O email já está em uso");
           }
       });
   }
   ```

3. **Password Verification**:
   ```java
   if (!bcryptEncoder.matches(currentPassword, user.getPassword())) {
       throw new BadRequestException("A palavra-passe atual está incorreta");
   }
   ```

## Error Handling

The implementation handles various error scenarios:

| Status Code | Error Condition | Message |
|------------|-----------------|---------|
| 403 Forbidden | User tries to update another user's profile | "Você só pode atualizar o seu próprio perfil" |
| 400 Bad Request | Incorrect current password | "A palavra-passe atual está incorreta" |
| 409 Conflict | Email already in use by another user | "O email já está em uso por outro utilizador" |
| 404 Not Found | User not found | "Utilizador com o nome {username} não encontrado" |
| 400 Bad Request | Validation errors | Field-specific validation messages |

## Testing

### Manual Testing with cURL

1. **Update profile without password change:**
```bash
curl -X PATCH 'http://localhost:8080/api/v1/users/johndoe/profile' \
  -H 'Content-Type: application/json' \
  -H 'Cookie: jwt=YOUR_JWT_TOKEN' \
  -d '{
    "firstName": "John",
    "lastName": "Smith",
    "email": "john.smith@example.com"
  }'
```

2. **Update profile with password change:**
```bash
curl -X PATCH 'http://localhost:8080/api/v1/users/johndoe/profile' \
  -H 'Content-Type: application/json' \
  -H 'Cookie: jwt=YOUR_JWT_TOKEN' \
  -d '{
    "firstName": "John",
    "lastName": "Smith",
    "email": "john.smith@example.com",
    "currentPassword": "oldPassword",
    "newPassword": "newPassword"
  }'
```

3. **Try to update another user (should fail):**
```bash
curl -X PATCH 'http://localhost:8080/api/v1/users/anothername/profile' \
  -H 'Content-Type: application/json' \
  -H 'Cookie: jwt=YOUR_JWT_TOKEN' \
  -d '{
    "firstName": "Hacker",
    "lastName": "Attempt",
    "email": "hacker@example.com"
  }'
# Expected: 403 Forbidden
```

### Running Unit Tests
```bash
./mvnw test -Dtest=UserControllerProfileUpdateTest
```

## Files Modified/Created

### Created Files:
1. `src/main/java/tl/gov/mci/lis/dtos/user/UserProfileUpdateDto.java`
2. `src/test/java/tl/gov/mci/lis/controllers/UserControllerProfileUpdateTest.java`
3. `docs/USER_PROFILE_UPDATE.md`
4. `docs/USER_PROFILE_UPDATE_IMPLEMENTATION_SUMMARY.md` (this file)

### Modified Files:
1. `src/main/java/tl/gov/mci/lis/services/user/UserServices.java`
   - Added `updateOwnProfile()` method
2. `src/main/java/tl/gov/mci/lis/controllers/UserController.java`
   - Added `updateOwnProfile()` endpoint
   - Added necessary imports

## Code Quality

- ✅ Follows existing code patterns and conventions
- ✅ Uses Lombok annotations consistently
- ✅ Proper exception handling
- ✅ Comprehensive logging
- ✅ Jakarta Bean Validation annotations
- ✅ Transaction management with `@Transactional`
- ✅ Portuguese error messages (consistent with existing code)
- ✅ RESTful principles (PATCH for partial updates)

## Future Enhancements (Optional)

1. Add rate limiting to prevent brute force password attempts
2. Send email notification when profile is updated (especially password)
3. Add password strength validation
4. Implement profile change audit log
5. Add two-factor authentication for sensitive changes

## Conclusion

The implementation successfully adds a secure user profile update endpoint that ensures users can only modify their own information. The solution includes proper validation, error handling, security checks, comprehensive documentation, and unit tests.

