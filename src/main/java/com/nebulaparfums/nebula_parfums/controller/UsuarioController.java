package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.UsuarioDTO;
import com.nebulaparfums.nebula_parfums.model.Proveedor;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UsuarioController {
    @Autowired
    private IUsuarioService iUsuarioService;

    @GetMapping("/usuario/traer")
    public Page<UsuarioDTO> traerUsuarios(Pageable pageable,String nombre) {
        return iUsuarioService.getUsuarios(pageable, nombre);
    }

    @GetMapping("/usuarioAutenticado")
    public Usuario usuarioAutenticado(Authentication authentication) {
        Usuario user = iUsuarioService.getUsuarioByEmail(authentication.getName());
        return user;
    }

    @GetMapping("/usuario/total")
    public int totalUsuarios() {
        return iUsuarioService.totalUsuarios();
    }

    @GetMapping("/usuario/activos")
    public int activos() {
        return iUsuarioService.totalUsuariosActivos();
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

    @PutMapping("/usuario/editar")
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
