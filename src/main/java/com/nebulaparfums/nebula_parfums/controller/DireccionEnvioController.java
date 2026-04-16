package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.service.interfaces.IDireccionEnvioService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class DireccionEnvioController {
    @Autowired
    private IDireccionEnvioService iDireccionEnvioService;

    @Autowired
    private IUsuarioService iUsuarioService;

    @GetMapping("/direccion/buscar")
    public DireccionEnvio buscarDireccionEnvio(@RequestParam("id") Integer id){
        return iDireccionEnvioService.getDireccionEnvioById(id);
    }

    @PostMapping("/direccion/crear")
    public String crearDireccionEnvio(@RequestBody DireccionEnvio direccionEnvio){
        iDireccionEnvioService.saveDireccionEnvio(direccionEnvio);
        return "Direccion Envio creado con sucesso";
    }

    @GetMapping("/direccion/mia")
    public DireccionEnvio obtenerMiDireccion(Authentication authentication) {
        String email = authentication.getName();
        Usuario user = iUsuarioService.getUsuarioByEmail(email);
        return user.getDireccionEnvio();
    }

    @PutMapping("/direccion/editar")
    public String editarMiDireccion(@RequestBody DireccionEnvio direccionEnvio, Authentication authentication) {
        String email = authentication.getName();
        iDireccionEnvioService.editarDireccionPorEmail(email, direccionEnvio);
        return "Direccion Envio editado con sucesso";
    }

    @DeleteMapping("/direccion/borrar")
    public String borrarDireccionEnvio(@RequestParam("id") Integer id){
        iDireccionEnvioService.deleteDireccionEnvioById(id);
        return "Direccion Envio borrado con sucesso";
    }
}
