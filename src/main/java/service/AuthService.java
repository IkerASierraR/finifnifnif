package service;

import dto.LoginRequest;
import dto.LoginResponse;
import model.Usuario;
import model.UsuarioAuth;
import repository.UsuarioAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioAuthRepository authRepository;

    @Transactional(readOnly = true)
    public LoginResponse loginAcademico(LoginRequest request) {
        Optional<UsuarioAuth> authOpt = authRepository.findByCorreoU(request.correo);
        
        if (authOpt.isEmpty()) {
            return createErrorResponse("Correo no registrado en el sistema");
        }
        
        UsuarioAuth auth = authOpt.get();
        String storedPassword = new String(Base64.getDecoder().decode(auth.password));
        
        if (!storedPassword.equals(request.password)) {
            return createErrorResponse("Contraseña incorrecta");
        }
        
        Usuario usuario = auth.usuario;
        
        // ✅ VALIDACIÓN ESTRICTA: Solo permitir roles 1 y 2 (Docente y Estudiante)
        if (usuario.rol.idRol != 1 && usuario.rol.idRol != 2) {
            return createErrorResponse("Acceso denegado. El login académico es solo para estudiantes y docentes. Use el login administrativo.");
        }
        
        // Validar que el usuario esté activo
        if (usuario.estado != 1) {
            return createErrorResponse("Usuario inactivo. Contacte al administrador.");
        }

        // Actualizar último login
        auth.ultimoLogin = LocalDateTime.now();
        authRepository.save(auth);
        
        return createSuccessResponse(usuario, 
            (usuario.rol.idRol == 1) ? "Docente" : "Estudiante", 
            "Login académico exitoso");
    }

    @Transactional(readOnly = true)
    public LoginResponse loginAdministrativo(LoginRequest request) {
        Optional<UsuarioAuth> authOpt = authRepository.findByCorreoU(request.correo);
        
        if (authOpt.isEmpty()) {
            return createErrorResponse("Correo no registrado en el sistema");
        }
        
        UsuarioAuth auth = authOpt.get();
        String storedPassword = new String(Base64.getDecoder().decode(auth.password));
        
        if (!storedPassword.equals(request.password)) {
            return createErrorResponse("Contraseña incorrecta");
        }
        
        Usuario usuario = auth.usuario;
        
        // ✅ VALIDACIÓN ESTRICTA: Solo permitir roles 3 y 4 (Administrador y Supervisor)
        if (usuario.rol.idRol != 3 && usuario.rol.idRol != 4) {
            return createErrorResponse("Acceso restringido. El login administrativo es solo para personal autorizado. Use el login académico.");
        }
        
        // Validar que el usuario esté activo
        if (usuario.estado != 1) {
            return createErrorResponse("Usuario administrativo inactivo. Contacte al supervisor.");
        }

        // Actualizar último login
        auth.ultimoLogin = LocalDateTime.now();
        authRepository.save(auth);
        
        return createSuccessResponse(usuario, 
            (usuario.rol.idRol == 3) ? "Administrador" : "Supervisor", 
            "Login administrativo exitoso");
    }

    private LoginResponse createSuccessResponse(Usuario usuario, String tipoUsuario, String mensaje) {
        LoginResponse response = new LoginResponse();
        response.idUsuario = usuario.idUsuario;
        response.nombreCompleto = usuario.nombre + " " + usuario.apellido;
        response.idRol = usuario.rol.idRol;
        response.tipoUsuario = tipoUsuario;
        response.mensaje = mensaje;
        response.success = true;
        return response;
    }

    private LoginResponse createErrorResponse(String mensaje) {
        LoginResponse response = new LoginResponse();
        response.mensaje = mensaje;
        response.success = false;
        return response;
    }
}