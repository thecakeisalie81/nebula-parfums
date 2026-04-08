package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;
import com.nebulaparfums.nebula_parfums.service.interfaces.IDireccionEnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DireccionEnvioController {
    @Autowired
    private IDireccionEnvioService iDireccionEnvioService;

    @GetMapping("/direccion/buscar")
    public DireccionEnvio buscarDireccionEnvio(@RequestParam("id") Integer id){
        return iDireccionEnvioService.getDireccionEnvioById(id);
    }

    @PostMapping("/direccion/crear")
    public String crearDireccionEnvio(@RequestBody DireccionEnvio direccionEnvio){
        iDireccionEnvioService.saveDireccionEnvio(direccionEnvio);
        return "Direccion Envio creado con sucesso";
    }

    @PutMapping("direccion/editar")
    public String editarDireccionEnvio(@RequestBody DireccionEnvio direccionEnvio){
        iDireccionEnvioService.editDireccionEnvio(direccionEnvio);
        return "Direccion Envio editado con sucesso";
    }

    @DeleteMapping("/direccion/borrar")
    public String borrarDireccionEnvio(@RequestParam("id") Integer id){
        iDireccionEnvioService.deleteDireccionEnvioById(id);
        return "Direccion Envio borrado con sucesso";
    }
}
