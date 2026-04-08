package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UsuarioController {
    @Autowired
    private IUsuarioService iUsuarioService;

    @GetMapping("/usuario/traer")
    public List<Usuario> traerUsuarios() {
        return iUsuarioService.getUsuarios();
    }

    @GetMapping("/usuario/buscar")
    public Usuario buscarUsuario(@RequestParam("id") Integer id) {
        return iUsuarioService.getUsuarioById(id);
    }

    @PostMapping("/usuario/crear")
    public String crearUsuario(@RequestBody Usuario usuario) {
        iUsuarioService.saveUsuario(usuario);
        return "Usuario creado exitosamente";
    }

    @PutMapping("usuario/editar")
    public String editarUsuario(@RequestBody Usuario usuario) {
        iUsuarioService.editUsuario(usuario);
        return "Usuario editado exitosamente";
    }

    @DeleteMapping("/usuario/borrar")
    public String borrarUsuario(@RequestParam("id") Integer id) {
        iUsuarioService.deleteUsuarioById(id);
        return "Usuario borrado exitosamente";
    }
}
