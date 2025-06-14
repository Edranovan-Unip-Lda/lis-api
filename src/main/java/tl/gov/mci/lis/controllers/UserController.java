package tl.gov.mci.lis.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.configs.jwt.JwtSessionService;
import tl.gov.mci.lis.configs.jwt.JwtUtil;
import tl.gov.mci.lis.dtos.mappers.UserMapper;
import tl.gov.mci.lis.dtos.user.UserDto;
import tl.gov.mci.lis.dtos.user.UserLoginDto;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.services.user.UserServices;

import java.time.Duration;
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

    @GetMapping("/{username}")
    public ResponseEntity<User> getByUsername(@PathVariable String username) {
        return new ResponseEntity<>(userServices.getByUsername(username), HttpStatus.OK);
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
        UserLoginDto loginDto = userMapper.toLoginDto(userServices.getJWTByOTP(user.getUsername(), otp, request));
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", loginDto.getJwtSession())
                .httpOnly(true)
                .secure(true) // Set to true in production (HTTPS)
                .path("/")
                .sameSite("None") // If frontend is on different origin
                .maxAge(Duration.ofHours(5))
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(loginDto);
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
    public ResponseEntity<String> logOut(@CookieValue(name = "jwt", required = false) String token, HttpServletResponse response) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("Session expired. Please log in again.");
        }

        String username = jwtUtil.extractUsername(token);

        // Invalidate session
        jwtSessionService.invalidateSession(username);

        // Clear cookie
        ResponseCookie clearedCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(0)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, clearedCookie.toString());

        return ResponseEntity.ok("Logged out successfully");
    }
}
