package controller;

import dto.LoginRequest;
import dto.LoginResponse;
import service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login-academico")
    public LoginResponse loginAcademico(@RequestBody LoginRequest request) {
        return authService.loginAcademico(request);
    }

    @PostMapping("/login-administrativo")
    public LoginResponse loginAdministrativo(@RequestBody LoginRequest request) {
        return authService.loginAdministrativo(request);
    }

    @GetMapping("/health")
    public String health() {
        return "Auth service is running";
    }
}