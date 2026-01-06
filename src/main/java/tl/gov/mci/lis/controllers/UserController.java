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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.configs.jwt.JwtSessionService;
import tl.gov.mci.lis.configs.jwt.JwtUtil;
import tl.gov.mci.lis.dtos.aplicante.AplicanteAssignmentDto;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.mappers.AplicanteMapper;
import tl.gov.mci.lis.dtos.mappers.EmpresaMapper;
import tl.gov.mci.lis.dtos.mappers.UserMapper;
import tl.gov.mci.lis.dtos.user.PasswordResetRequest;
import tl.gov.mci.lis.dtos.user.UserDetailDto;
import tl.gov.mci.lis.dtos.user.UserLoginDto;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.aplicante.HistoricoEstadoAplicante;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.services.aplicante.AplicanteService;
import tl.gov.mci.lis.services.aplicante.AssignmentService;
import tl.gov.mci.lis.services.cadastro.CertificadoService;
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
    private final AplicanteService aplicanteService;
    private final AplicanteMapper aplicanteMapper;
    private final AssignmentService assignmentService;
    private final CertificadoService certificadoService;
    private final EmpresaMapper empresaMapper;

    @PostMapping("")
    public ResponseEntity<UserLoginDto> register(@Valid @RequestBody User user) {
        return new ResponseEntity<>(userMapper.toLoginDto(userServices.register(user)), HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<Page<User>> getPage(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "50") int size) {
        return new ResponseEntity<>(userServices.getPage(page, size), HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDetailDto> getByUsername(@PathVariable String username) {
        return new ResponseEntity<>(userMapper.toDto1(userServices.getByUsername(username)), HttpStatus.OK);
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDetailDto> update(@PathVariable String username, @RequestBody User user) {
        return new ResponseEntity<>(userMapper.toDto1(userServices.updateByUsername(username, user)), HttpStatus.OK);
    }

    @DeleteMapping("/{username}/signature")
    public ResponseEntity<Void> deleteSignature(@PathVariable String username) {
        userServices.removeSignature(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> sendForgotPasswordEmail(@RequestParam String email) {
        return ResponseEntity.ok(userServices.sendForgotPasswordEmail(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody PasswordResetRequest req) {
        return ResponseEntity.ok(userServices.resetPassword(req));
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

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CHIEF')")
    @GetMapping("/{username}/empresas/{utilizadorUsername}")
    public ResponseEntity<EmpresaDto> getEmpresaByUtilizadorUsername(
            @PathVariable String utilizadorUsername
    ) {
        return new ResponseEntity<>(empresaMapper.toDto(userServices.getEmpresaByUtilizadorUsername(utilizadorUsername)), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CHIEF', 'ROLE_CHIEF')")
    @GetMapping("/{username}/aplicantes")
    public ResponseEntity<Page<AplicanteDto>> getPageAplicanteByIdAndDirecaoId(
            @PathVariable String username,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        return new ResponseEntity<>(userServices.getPageAssignedAplicante(username, page, size), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CHIEF', 'ROLE_STAFF')")
    @GetMapping("/{username}/aplicantes/{aplicanteId}")
    public ResponseEntity<AplicanteDto> getAssignedAplicanteByIdAndDirecaoId(
            @PathVariable String username,
            @PathVariable Long aplicanteId
    ) {
        return ResponseEntity.ok(
                aplicanteMapper.toDto(userServices.getAssignedAplicanteByUsernameAndId(username, aplicanteId))
        );
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CHIEF')")
    @PatchMapping("/{username}/aplicantes/{aplicanteId}/revisto")
    public ResponseEntity<AplicanteDto> revistoAplicante(
            @PathVariable String username,
            @PathVariable Long aplicanteId,
            @RequestBody HistoricoEstadoAplicante historicoEstadoAplicante
    ) {
        return switch (historicoEstadoAplicante.getStatus()) {
            case REVISTO -> ResponseEntity.ok(
                    aplicanteMapper.toDto(userServices.reviewedAplicante(username, aplicanteId)));
            case REJEITADO -> ResponseEntity.ok(
                    aplicanteMapper.toDto(
                            userServices.rejectAplicante(username, aplicanteId, historicoEstadoAplicante)));
            default -> ResponseEntity.badRequest().build();
        };
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER')")
    @PatchMapping("/{username}/aplicantes/{aplicanteId}")
    public ResponseEntity<AplicanteDto> decisionAplicante(
            @PathVariable String username,
            @PathVariable Long aplicanteId,
            @RequestBody HistoricoEstadoAplicante historicoEstadoAplicante
    ) {
        return switch (historicoEstadoAplicante.getStatus()) {
            case APROVADO -> new ResponseEntity<>(
                    aplicanteMapper.toDto(
                            userServices.approveAplicante(username, aplicanteId, historicoEstadoAplicante)
                    ),
                    HttpStatus.OK);
            case REJEITADO -> new ResponseEntity<>(
                    aplicanteMapper.toDto(
                            userServices.rejectAplicante(username, aplicanteId, historicoEstadoAplicante)
                    ),
                    HttpStatus.OK);
            default -> ResponseEntity.badRequest().build();
        };

    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_CHIEF', 'ROLE_STAFF')")
    @GetMapping("/{username}/aplicantes/atribuidos")
    public ResponseEntity<Page<AplicanteDto>> getListAplicanteAtribuidos(
            @PathVariable String username,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(aplicanteService.getAplicantesAtribuidos(username, page, size)
                .map(aplicanteMapper::toDto));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_CHIEF')")
    @PatchMapping("/{username}/aplicantes/atribuidos/{aplicanteId}")
    public ResponseEntity<AplicanteAssignmentDto> atribuirAplicante(
            @PathVariable String username,
            @PathVariable Long aplicanteId,
            @RequestParam String staffUsername,
            @RequestParam String notes
    ) {
        return ResponseEntity.ok(aplicanteMapper.toDto(assignmentService.assign(aplicanteId, username, staffUsername, notes)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CHIEF', 'ROLE_STAFF')")
    @GetMapping("/{username}/certificados")
    ResponseEntity<Page<?>> getPageCertificates(
            @RequestParam(value = "categoria") Categoria categoria,
            @RequestParam(value = "type") AplicanteType aplicanteType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(certificadoService.findPageApprovedCertificados(aplicanteType, categoria, page, size));
    }
}
