package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.CreateOrdenDTO;
import com.nebulaparfums.nebula_parfums.dto.OrdenDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductosPendientesProceso;
import com.nebulaparfums.nebula_parfums.model.Orden;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/orden/total")
    public Double totalMes(
            @RequestParam(value = "fechaInicio", required = false) LocalDateTime fechaInicio,
            @RequestParam(value = "fechaFin", required = false) LocalDateTime fechaFin) {
        return iOrdenService.sumaTotalesMes(fechaInicio, fechaFin);
    }

    @GetMapping("/orden/recientes")
    public List<Orden> recientes(){
        return iOrdenService.getUltimasOrdenesPendiente();
    }

    @GetMapping("/orden/totales")
    public ProductosPendientesProceso totales(){
        return iOrdenService.getPendientesProcesos();
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

    @GetMapping("/orden/useractual")
    public List<Orden> userActual(@RequestParam("id") Integer id){
        return iOrdenService.getOrdenesUsuario(id);
    }

    @PostMapping("/orden/crear")
    public String crearOrden(@RequestBody CreateOrdenDTO orden){
        iOrdenService.crearOrden(orden);
        return "Orden creado con sucesso";
    }


    @PutMapping("/orden/editar")
    public String editarOrden(@RequestBody OrdenDTO orden){
        iOrdenService.editOrden(orden);
        return "Orden editado con sucesso";
    }

    @GetMapping("/reportes/pedidos/pdf")
    public ResponseEntity<byte[]> exportarPedidosPdf(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin
    ) {
        byte[] archivo = iOrdenService.exportarPedidosPdf(fechaInicio, fechaFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("reporte_pedidos.pdf")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(archivo);
    }

    @GetMapping("/reportes/pedidos/excel")
    public ResponseEntity<byte[]> exportarPedidosExcel(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin
    ) {
        byte[] archivo = iOrdenService.exportarPedidosExcel(fechaInicio, fechaFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        );
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("reporte_pedidos.xlsx")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(archivo);
    }

}
