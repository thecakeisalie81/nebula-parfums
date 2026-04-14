package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.OrdenDTO;
import com.nebulaparfums.nebula_parfums.model.Orden;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class OrdenController {
    @Autowired
    private IOrdenService iOrdenService;

    @GetMapping("/orden/traer")
    public List<Orden> traerOrdenes(){
        return iOrdenService.getOrdenes();
    }

    @GetMapping("/orden/filtrar")
    public Page<Orden> filtrarOrdenes(
            Pageable pageable,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin
    ) {
        return iOrdenService.filtrarOrden(pageable, estado, fechaInicio, fechaFin);
    }

    @GetMapping("/orden/recientes")
    public List<Orden> recientes(){
        return iOrdenService.getUltimasOrdenesPendiente();
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
    public String crearOrden(@RequestBody OrdenDTO orden){
        iOrdenService.crearOrden(orden);
        return "Orden creado con sucesso";
    }


    @PutMapping("/orden/editar")
    public String editarOrden(@RequestBody OrdenDTO orden){
        iOrdenService.editOrden(orden);
        return "Orden editado con sucesso";
    }

}
