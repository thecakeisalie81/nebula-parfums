package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.OrdendetalleDTO;
import com.nebulaparfums.nebula_parfums.model.OrdenDetalle;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrdenDetalleController {
    @Autowired
    private IOrdenDetalleService iOrdenDetalleService;

    @GetMapping("/ordendetalle/buscar")
    public OrdenDetalle buscarOrdenDetalleById(@RequestParam Integer id) {
        return iOrdenDetalleService.getOrdenDetalleById(id);
    }

    @PostMapping("/ordendetalle/crear")
    public String crearOrdenDetalle(@RequestBody OrdendetalleDTO ordendetalleDTO) {
        iOrdenDetalleService.createOrdenDetalle(ordendetalleDTO);
        return "Orden detalle creado con sucesso";
    }

    @PutMapping("/ordendetalle/editar")
    public String editarOrdenDetalle(@RequestBody OrdenDetalle ordenDetalle) {
        iOrdenDetalleService.editOrdenDetalle(ordenDetalle);
        return "Orden detalle editado con sucesso";
    }

    @DeleteMapping("/ordendetalle/borrar")
    public String borrarOrdenDetalle(@RequestParam Integer id) {
        iOrdenDetalleService.deleteOrdenDetalleById(id);
        return "Orden detalle borrado con sucesso";
    }
}
