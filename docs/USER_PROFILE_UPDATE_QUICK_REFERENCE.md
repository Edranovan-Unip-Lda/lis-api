# User Profile Update - Quick Reference

## 🎯 Purpose
Allows authenticated users to update their own profile information only (User A can only update User A's data).

## 📍 Endpoint
```
PATCH /api/v1/users/{username}/profile
```

## 🔐 Authentication
- **Required:** Yes (JWT token in HttpOnly cookie)
- **Authorization:** User can only update their own profile

## 📤 Request Body
```json
{
  "firstName": "John",          // Required
  "lastName": "Doe",            // Required
  "email": "john@example.com",  // Required (valid email)
  "currentPassword": "old123",  // Optional (required if changing password)
  "newPassword": "new456"       // Optional (required if changing password)
}
```

## 📥 Success Response (200 OK)
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "role": {
    "id": 2,
    "name": "ROLE_CLIENT"
  },
  "status": "active"
}
```

## ❌ Error Responses

| Code | Reason | Message |
|------|--------|---------|
| 403 | Updating another user's profile | "Você só pode atualizar o seu próprio perfil" |
| 400 | Wrong current password | "A palavra-passe atual está incorreta" |
| 409 | Email already in use | "O email já está em uso por outro utilizador" |
| 404 | User not found | "Utilizador não encontrado" |
| 400 | Validation errors | Field-specific messages |

## 🧪 Testing Examples

### Update basic info (no password change)
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

### Update with password change
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

## 🔒 Security Features

✅ **Self-Update Only**: System verifies authenticated user matches target user  
✅ **Password Verification**: Current password must be correct to change password  
✅ **Email Uniqueness**: Checks email is not used by another user  
✅ **Password Encryption**: New passwords are BCrypt encrypted  
✅ **Transaction Safety**: Database changes are transactional  

## 📁 Implementation Files

**Created:**
- `UserProfileUpdateDto.java` - Request DTO
- `UserControllerProfileUpdateTest.java` - Unit tests
- `USER_PROFILE_UPDATE.md` - Full API documentation
- `USER_PROFILE_UPDATE_IMPLEMENTATION_SUMMARY.md` - Implementation details

**Modified:**
- `UserServices.java` - Added `updateOwnProfile()` method
- `UserController.java` - Added profile update endpoint

## 🚀 Running Tests
```bash
./mvnw test -Dtest=UserControllerProfileUpdateTest
```

## 📚 Documentation
- **API Guide:** `docs/USER_PROFILE_UPDATE.md`
- **Implementation:** `docs/USER_PROFILE_UPDATE_IMPLEMENTATION_SUMMARY.md`
- **This Guide:** `docs/USER_PROFILE_UPDATE_QUICK_REFERENCE.md`

