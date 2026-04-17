package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.OrdendetalleDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductoCantidadDTO;
import com.nebulaparfums.nebula_parfums.model.OrdenDetalle;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping("/ordendetalle/contar")
    public List<ProductoCantidadDTO> getProductoCantidadDTO(@RequestParam(required = false) LocalDateTime fechainicio,
                                                            @RequestParam(required = false) LocalDateTime fechafinal) {
        return iOrdenDetalleService.getProductoCantidadDTO(fechainicio, fechafinal);
    }

    @DeleteMapping("/ordendetalle/borrar")
    public String borrarOrdenDetalle(@RequestParam Integer id) {
        iOrdenDetalleService.deleteOrdenDetalleById(id);
        return "Orden detalle borrado con sucesso";
    }

    @GetMapping("/reportes/ventas/pdf")
    public ResponseEntity<byte[]> exportarVentasPdf(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin
    ) {
        byte[] archivo = iOrdenDetalleService.exportarVentasPdf(fechaInicio, fechaFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("reporte_ventas.pdf")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(archivo);
    }

    @GetMapping("/reportes/ventas/excel")
    public ResponseEntity<byte[]> exportarVentasExcel(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin
    ) {
        byte[] archivo = iOrdenDetalleService.exportarVentasExcel(fechaInicio, fechaFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ));
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("reporte_ventas.xlsx")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(archivo);
    }
}
