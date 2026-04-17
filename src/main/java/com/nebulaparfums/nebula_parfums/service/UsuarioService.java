package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.UsuarioDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.Proveedor;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.IUsuarioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;

@Service
public class UsuarioService implements IUsuarioService {
    @Autowired
    private IUsuarioRepository iUsuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public Usuario getUsuarioById(Integer id) {
        return iUsuarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro el usuario"));
    }

    @Override
    public Page<UsuarioDTO> getUsuarios(Pageable pageable, String nombre) {
        return iUsuarioRepository.filtrarUsuarios(pageable, nombre).map(usuario ->
                new UsuarioDTO(
                        usuario.getId_usuario(),
                        usuario.getNombre(),
                        usuario.getEmail(),
                        usuario.getEstado(),
                        usuario.getFecha_creacion(),
                        usuario.getRol() != null ? usuario.getRol().getNombre_rol() : "Sin rol"
                )
        );
    }

    @Override
    public void saveUsuario(Usuario usuario) {
        iUsuarioRepository.save(usuario);
    }

    @Override
    public void deleteUsuarioById(Integer id) {
        if (iUsuarioRepository.existsById(id)) {
            iUsuarioRepository.deleteById(id);
        }else {
            throw new UsernameNotFoundException("No se encontro el usuario");
        }
    }

    @Override
    public void editUsuario(Usuario usuario) {
        Usuario user = getUsuarioById(usuario.getId_usuario());

        user.setNombre(usuario.getNombre() != null ? usuario.getNombre() : user.getNombre());
        user.setEmail(usuario.getEmail() != null ? usuario.getEmail() : user.getEmail());
        user.setEstado(usuario.getEstado());

        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        this.saveUsuario(user);
    }

    @Override
    public Usuario getUsuarioByEmail(String email) {
        Usuario usuario = iUsuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("No se encontro el usuario"));
        return usuario;
    }

    @Override
    public int totalUsuarios() {
        return iUsuarioRepository.totalUsuarios();
    }

    @Override
    public int totalUsuariosActivos() {
        return iUsuarioRepository.totalUsuariosActivos();
    }
}
