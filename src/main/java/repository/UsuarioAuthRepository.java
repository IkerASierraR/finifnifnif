package repository;

import model.UsuarioAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioAuthRepository extends JpaRepository<UsuarioAuth, Integer> {
    Optional<UsuarioAuth> findByCorreoU(String correoU);
}