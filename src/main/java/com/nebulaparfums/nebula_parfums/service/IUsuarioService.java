package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.Usuario;

import java.util.List;

public interface IUsuarioService {
    public Usuario getUsuarioById(Integer id);
    public List<Usuario> getUsuarios();
    public void saveUsuario(Usuario usuario);
    public void deleteUsuarioById(Integer id);
    public void editUsuario(Usuario usuario);
}
