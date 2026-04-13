package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.UsuarioDTO;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IUsuarioService {
    public Usuario getUsuarioById(Integer id);
    public Page<UsuarioDTO> getUsuarios(Pageable pageable, String nombre);
    public void saveUsuario(Usuario usuario);
    public void deleteUsuarioById(Integer id);
    public void editUsuario(Usuario usuario);
    public Usuario getUsuarioByEmail(String email);
    public int totalUsuarios();
    public int totalUsuariosActivos();
}
