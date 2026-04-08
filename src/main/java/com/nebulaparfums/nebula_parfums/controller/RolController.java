package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.Rol;
import com.nebulaparfums.nebula_parfums.service.interfaces.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RolController {
    @Autowired
    private IRolService iRolService;

    @GetMapping("/rol/traer")
    public List<Rol> traerRol() {
        return iRolService.getRols();
    }

    @GetMapping("/rol/buscar")
    public Rol buscarRol(@RequestParam("id") Integer id) {
        return iRolService.getRolById(id);
    }

    @PostMapping("/rol/crear")
    public String crearRol(@RequestBody Rol rol) {
        iRolService.saveRol(rol);
        return "Rol creado exitosamente";
    }

    @DeleteMapping("/rol/borrar")
    public String borrarRol(@RequestParam("id") Integer id) {
        iRolService.deleteRol(id);
        return "Rol borrado exitosamente";
    }

    @PutMapping("/rol/editar")
    public String editarRol(@RequestBody Rol rol) {
        iRolService.editRol(rol);
        return "Rol editado exitosamente";
    }
}
