package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.UsuarioDTO;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IUsuarioService {
    Usuario getUsuarioById(Integer id);
    Page<UsuarioDTO> getUsuarios(Pageable pageable, String nombre);
    UsuarioDTO saveUsuario(UsuarioDTO usuario);
    void deleteUsuarioById(Integer id);
    UsuarioDTO editUsuario(Integer id, UsuarioDTO usuario);
    Usuario getUsuarioByEmail(String email);
    int totalUsuarios();
    int totalUsuariosActivos();
}
