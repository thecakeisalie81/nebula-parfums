package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.OrdenDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductosPendientesProceso;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orden")
@RequiredArgsConstructor
public class OrdenController {
    private final IOrdenService iOrdenService;

    @GetMapping("/ordenes")
    public ResponseEntity<List<OrdenDTO>> traerOrdenes(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(iOrdenService.getOrdenes());
    }

    @GetMapping("/filtrar")
    public ResponseEntity<Page<OrdenDTO>> filtrarOrdenes(
            Pageable pageable,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) LocalDateTime fechaInicio,
            @RequestParam(required = false) LocalDateTime fechaFin
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iOrdenService.filtrarOrden(pageable, estado, fechaInicio, fechaFin));
    }

    @GetMapping("/total")
    public ResponseEntity<Double> totalMes(
            @RequestParam(value = "fechaInicio", required = false) LocalDateTime fechaInicio,
            @RequestParam(value = "fechaFin", required = false) LocalDateTime fechaFin) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iOrdenService.sumaTotalesMes(fechaInicio, fechaFin));
    }

    @GetMapping("/recientes")
    public ResponseEntity<List<OrdenDTO>> recientes(Pageable page){
        return ResponseEntity.status(HttpStatus.OK)
                .body(iOrdenService.getUltimasOrdenesPendiente(page));
    }

    @GetMapping("/totales")
    public ResponseEntity<ProductosPendientesProceso> totales(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(iOrdenService.getPendientesProcesos());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<OrdenDTO> buscarOrden(@PathVariable Integer id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(Mapper.toDTO(iOrdenService.getOrdenById(id)));
    }

    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> borrarOrden(@PathVariable int id){
        iOrdenService.deleteOrden(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Orden eliminada");
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<OrdenDTO>> userActual(@RequestParam("id") Integer id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(iOrdenService.getOrdenesUsuario(id));
    }

    @PostMapping("/crear")
    public ResponseEntity<OrdenDTO> crearOrden(@RequestBody OrdenDTO orden){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iOrdenService.saveOrden(orden));
    }


    @PutMapping("/editar/{id}")
    public ResponseEntity<OrdenDTO> editarOrden(@PathVariable Integer id, @RequestBody OrdenDTO orden){
        return ResponseEntity.status(HttpStatus.OK)
                .body(iOrdenService.editOrden(id, orden));

    }
}
