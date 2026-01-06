package tl.gov.mci.lis.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import tl.gov.mci.lis.dtos.mappers.UserMapper;
import tl.gov.mci.lis.dtos.user.UserDetailDto;
import tl.gov.mci.lis.dtos.user.UserProfileUpdateDto;
import tl.gov.mci.lis.exceptions.ForbiddenException;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.services.user.UserServices;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserController profile update functionality
 */
class UserControllerProfileUpdateTest {

    @Mock
    private UserServices userServices;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testUpdateOwnProfile_Success() {
        // Arrange
        String username = "johndoe";
        UserProfileUpdateDto profileUpdate = new UserProfileUpdateDto();
        profileUpdate.setFirstName("John");
        profileUpdate.setLastName("Doe");
        profileUpdate.setEmail("john.doe@example.com");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername(username);
        updatedUser.setFirstName("John");
        updatedUser.setLastName("Doe");
        updatedUser.setEmail("john.doe@example.com");

        UserDetailDto userDetailDto = mock(UserDetailDto.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userServices.updateOwnProfile(
                eq(username),
                eq(username),
                eq("John"),
                eq("Doe"),
                eq("john.doe@example.com"),
                isNull(),
                isNull()
        )).thenReturn(updatedUser);
        when(userMapper.toDto1(updatedUser)).thenReturn(userDetailDto);

        // Act
        ResponseEntity<UserDetailDto> response = userController.updateOwnProfile(username, profileUpdate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userServices).updateOwnProfile(
                eq(username),
                eq(username),
                eq("John"),
                eq("Doe"),
                eq("john.doe@example.com"),
                isNull(),
                isNull()
        );
    }

    @Test
    void testUpdateOwnProfile_WithPasswordChange_Success() {
        // Arrange
        String username = "johndoe";
        UserProfileUpdateDto profileUpdate = new UserProfileUpdateDto();
        profileUpdate.setFirstName("John");
        profileUpdate.setLastName("Doe");
        profileUpdate.setEmail("john.doe@example.com");
        profileUpdate.setCurrentPassword("oldPassword123");
        profileUpdate.setNewPassword("newPassword456");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername(username);

        UserDetailDto userDetailDto = mock(UserDetailDto.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userServices.updateOwnProfile(
                eq(username),
                eq(username),
                eq("John"),
                eq("Doe"),
                eq("john.doe@example.com"),
                eq("oldPassword123"),
                eq("newPassword456")
        )).thenReturn(updatedUser);
        when(userMapper.toDto1(updatedUser)).thenReturn(userDetailDto);

        // Act
        ResponseEntity<UserDetailDto> response = userController.updateOwnProfile(username, profileUpdate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userServices).updateOwnProfile(
                eq(username),
                eq(username),
                eq("John"),
                eq("Doe"),
                eq("john.doe@example.com"),
                eq("oldPassword123"),
                eq("newPassword456")
        );
    }

    @Test
    void testUpdateOwnProfile_AttemptToUpdateAnotherUser_ThrowsForbiddenException() {
        // Arrange
        String authenticatedUsername = "johndoe";
        String targetUsername = "janedoe";
        UserProfileUpdateDto profileUpdate = new UserProfileUpdateDto();
        profileUpdate.setFirstName("Jane");
        profileUpdate.setLastName("Doe");
        profileUpdate.setEmail("jane.doe@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(authenticatedUsername);
        when(userServices.updateOwnProfile(
                eq(authenticatedUsername),
                eq(targetUsername),
                anyString(),
                anyString(),
                anyString(),
                isNull(),
                isNull()
        )).thenThrow(new ForbiddenException("Você só pode atualizar o seu próprio perfil"));

        // Act & Assert
        assertThrows(ForbiddenException.class, () ->
            userController.updateOwnProfile(targetUsername, profileUpdate)
        );

        verify(userServices).updateOwnProfile(
                eq(authenticatedUsername),
                eq(targetUsername),
                eq("Jane"),
                eq("Doe"),
                eq("jane.doe@example.com"),
                isNull(),
                isNull()
        );
    }
}

