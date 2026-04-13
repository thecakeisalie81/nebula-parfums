package com.nebulaparfums.nebula_parfums.repository;

import com.nebulaparfums.nebula_parfums.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    @Query("""
        SELECT u
        FROM Usuario u
        WHERE (:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
        AND u.rol.nombre_rol IN ('ROLE_ADMIN', 'ROLE_EMPLEADO')
    """)
    Page<Usuario> filtrarUsuarios(
            Pageable pageable,
            @Param("nombre") String nombre
    );

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.nombre_rol IN ('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    int totalUsuarios();

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.estado = true AND u.rol.nombre_rol IN ('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    int totalUsuariosActivos();
}
