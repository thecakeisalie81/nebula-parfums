package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements IUsuarioService{
    @Autowired
    private IUsuarioRepository iUsuarioRepository;


    @Override
    public Usuario getUsuarioById(Integer id) {
        Usuario usuario = iUsuarioRepository.findById(id).orElse(null);
        return usuario;
    }

    @Override
    public List<Usuario> getUsuarios() {
        List<Usuario> usuarios = iUsuarioRepository.findAll();
        return usuarios;
    }

    @Override
    public void saveUsuario(Usuario usuario) {
        iUsuarioRepository.save(usuario);
    }

    @Override
    public void deleteUsuarioById(Integer id) {
        iUsuarioRepository.deleteById(id);
    }

    @Override
    public void editUsuario(Usuario usuario) {
        this.saveUsuario(usuario);
    }
}
