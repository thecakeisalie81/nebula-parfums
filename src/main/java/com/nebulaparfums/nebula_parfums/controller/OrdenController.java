package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.Orden;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrdenController {
    @Autowired
    private IOrdenService iOrdenService;

    @GetMapping("/orden/traer")
    public List<Orden> traerOrdenes(){
        return iOrdenService.getOrdenes();
    }

    @GetMapping("/orden/buscar")
    public Orden buscarOrden(@RequestParam("id") int id){
        return iOrdenService.getOrdenById(id);
    }

    @DeleteMapping("/orden/borrar")
    public String borrarOrden(@RequestParam("id") int id){
        iOrdenService.deleteOrden(id);
        return "Orden borrado con sucesso";
    }

    @PostMapping("/orden/crear")
    public String crearOrden(@RequestBody Orden orden){
        iOrdenService.saveOrden(orden);
        return "Orden creado con sucesso";
    }

    @PutMapping("/orden/editar")
    public String editarOrden(@RequestBody Orden orden){
        iOrdenService.editOrden(orden);
        return "Orden editado con sucesso";
    }

}
