package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.UsuarioDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.IUsuarioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Servicio para gestionar Usuarios.
 * Provee operaciones de consulta, guardado, eliminación, edición y conversión a DTO.
 */
@Service
@AllArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final IUsuarioRepository iUsuarioRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UsuarioDTO getUsuarioById(Integer id) {
        return iUsuarioRepository.findById(id).map(Mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario"));
    }

    /**
     * Regresa los usuarios que tengan un nombre de usuario que coincida con el filtrado
     * @param pageable información de paginación (número de página, tamaño, orden)
     * @param nombre nombre de usuario que se usa para buscar usuarios que coincidan
     * @return devuelve todos los usuarios con un nombre que coincida
     */
    @Override
    public Page<UsuarioDTO> getUsuarios(Pageable pageable, String nombre) {
        return iUsuarioRepository.filtrarUsuarios(pageable, nombre).map(Mapper::toDTO);
    }

    /**
     * Guarda un nuevo usuario en la db
     * @param usuario UsuarioDTO con la información que lleva el nuevo usuario
     * @return UsuarioDTO con la información que se guardó en la db
     */
    @Override
    public UsuarioDTO saveUsuario(UsuarioDTO usuario) {
        Usuario user = Usuario.builder()
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .estado(usuario.getEstado())
                .fecha_creacion(usuario.getFecha_creacion())
                .rol(usuario.getRol())
                .build();

        return Mapper.toDTO(iUsuarioRepository.save(user));
    }

    /**
     * Elimina un usuario de forma física de la DB
     * @param id id del usuario a eliminar
     */
    @Override
    public void deleteUsuarioById(Integer id) {
        if (iUsuarioRepository.existsById(id)) {
            iUsuarioRepository.deleteById(id);
        }else {
            throw new UsernameNotFoundException("No se encontró el usuario");
        }
    }

    /**
     *Edita la información de un usuario guardado en la db
     * @param id id del usuario a modificar
     * @param usuario UsuarioDTO con los nuevos datos
     * @return Devuelve un UsuarioDTO con los datos actualizados
     */
    @Override
    public UsuarioDTO editUsuario(Integer id ,UsuarioDTO usuario) {

        Usuario user = iUsuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario"));

        user.setNombre(usuario.getNombre());
        user.setEmail(usuario.getEmail());
        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        user.setEstado(usuario.getEstado());

        return Mapper.toDTO(iUsuarioRepository.save(user));
    }

    @Override
    public Usuario getUsuarioByEmail(String email) {
        return iUsuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario"));
    }

    /**
     * Devuelve el número de administradores y empleados que hay registrados en el sistema
     * @return Número de usuarios incluyendo inactivos
     */
    @Override
    public int totalUsuarios() {
        return iUsuarioRepository.totalUsuarios();
    }


    /**
     * Devuelve el número de administradores y empleados activos actualmente en el sistema
     * @return Número de usuarios activos actualmente
     */
    @Override
    public int totalUsuariosActivos() {
        return iUsuarioRepository.totalUsuariosActivos();
    }
}
