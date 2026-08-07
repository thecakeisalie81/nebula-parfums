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

    /**
     *Busca entre los empleados y administradores basada en el nombre de usuario
     * @param pageable información de paginación (número de página, tamaño, orden)
     * @param nombre cadena usada para buscar usuarios que encajen con la búsqueda
     * @return Page con los usuarios filtrados que coincidan con el nombre
     */
    @Query("""
        SELECT u
        FROM Usuario u
        WHERE (:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
        AND u.rol IN ('ADMINISTRADOR', 'EMPLEADO')
    """)
    Page<Usuario> filtrarUsuarios(
            Pageable pageable,
            @Param("nombre") String nombre
    );

    /**
     * Devuelve el número de usuarios que tiene el sistema de administración incluyendo los inactivos
     * @return Número total de usuarios
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol IN ('ADMINISTRADOR', 'EMPLEADO')")
    int totalUsuarios();

    /**
     * Devuelve el número de usuarios que tiene el sistema de administración pero excluyendo los inactivos
     * @return Número de usuarios activos en el sistema
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.estado = true AND u.rol IN ('ADMINISTRADOR', 'EMPLEADO')")
    int totalUsuariosActivos();
}
