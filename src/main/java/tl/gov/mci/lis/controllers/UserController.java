package tl.gov.mci.lis.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.configs.jwt.JwtSessionService;
import tl.gov.mci.lis.configs.jwt.JwtUtil;
import tl.gov.mci.lis.dtos.mappers.UserMapper;
import tl.gov.mci.lis.dtos.user.UserDto;
import tl.gov.mci.lis.dtos.user.UserLoginDto;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.services.user.UserServices;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServices userServices;
    private final UserMapper userMapper;
    private final JwtSessionService jwtSessionService;
    private final JwtUtil jwtUtil;

    @PostMapping("")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        return new ResponseEntity<>(userServices.register(user), HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<Page<User>> getPage(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        return new ResponseEntity<>(userServices.getPage(page, size), HttpStatus.OK);
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDto> update(@PathVariable String username, @RequestBody User user) {
        return new ResponseEntity<>(userMapper.toDto(userServices.updateByUsername(username, user)), HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserLoginDto> authenticate(@RequestBody User user, HttpServletRequest request) {
        return new ResponseEntity<>(userMapper.toLoginDto(userServices.authenticate(user.getUsername(), user.getPassword(), request)), HttpStatus.OK);
    }

    @PostMapping("/otp/{otp}")
    public ResponseEntity<UserLoginDto> validateOTP(@PathVariable String otp, @RequestBody User user, HttpServletRequest request) {
        return new ResponseEntity<>(userMapper.toLoginDto(userServices.getJWTByOTP(user.getUsername(), otp, request)), HttpStatus.OK);
    }

    @PutMapping("/otp/{username}")
    public ResponseEntity<UserLoginDto> resendOtp(@PathVariable String username) {
        return new ResponseEntity<>(userMapper.toLoginDto(userServices.resendOTPToEmail(username)), HttpStatus.OK);
    }

    @PostMapping("/activate")
    public ResponseEntity<Map<String, String>> activate(@RequestParam String token, @RequestBody User user) {
        user.setJwtSession(token);
        return new ResponseEntity<>(userServices.activateFromEmail(user), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logOut(@RequestBody String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        String username = jwtUtil.extractUsername(token);

        // Invalidate session
        jwtSessionService.invalidateSession(username);

        return ResponseEntity.ok("Logged out successfully");
    }
}
