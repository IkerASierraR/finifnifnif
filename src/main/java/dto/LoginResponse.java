package dto;

public class LoginResponse {
    public Integer idUsuario;
    public String nombreCompleto;
    public Integer idRol;
    public String tipoUsuario;
    public String mensaje;
    public boolean success;
    
    // Constructor vacío
    public LoginResponse() {}
    
    // Constructor para errores
    public LoginResponse(String mensaje, boolean success) {
        this.mensaje = mensaje;
        this.success = success;
    }
}