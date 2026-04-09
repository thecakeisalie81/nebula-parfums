package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.IUsuarioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements IUsuarioService {
    @Autowired
    private IUsuarioRepository iUsuarioRepository;


    @Override
    public Usuario getUsuarioById(Integer id) {
        return iUsuarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro el usuario"));
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
        if (iUsuarioRepository.existsById(id)) {
            iUsuarioRepository.deleteById(id);
        }else {
            throw new UsernameNotFoundException("No se encontro el usuario");
        }
    }

    @Override
    public void editUsuario(Usuario usuario) {
        this.saveUsuario(usuario);
    }

    @Override
    public Usuario getUsuarioByEmail(String email) {
        Usuario usuario = iUsuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("No se encontro el usuario"));
        return usuario;
    }
}
