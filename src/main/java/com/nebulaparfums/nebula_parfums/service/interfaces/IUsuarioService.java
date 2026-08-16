package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.UsuarioDTO;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IUsuarioService {
    public Usuario getUsuarioById(Integer id);
    public Page<UsuarioDTO> getUsuarios(Pageable pageable, String nombre);
    public UsuarioDTO saveUsuario(UsuarioDTO usuario);
    public void deleteUsuarioById(Integer id);
    public UsuarioDTO editUsuario(Integer id, UsuarioDTO usuario);
    public Usuario getUsuarioByEmail(String email);
    public int totalUsuarios();
    public int totalUsuariosActivos();
}
