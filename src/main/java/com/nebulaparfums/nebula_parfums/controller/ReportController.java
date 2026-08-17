package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.service.interfaces.ILogActividadService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenDetalleService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reportes")
public class ReportController {
    private final ILogActividadService iLogActividadService;
    private final IOrdenService iOrdenService;
    private final IOrdenDetalleService iOrdenDetalleService;

    @GetMapping("/logs/pdf")
    public ResponseEntity<byte[]> crearLogPDF(
            @RequestParam(required = false) LocalDateTime fechaInicio,
            @RequestParam(required = false) LocalDateTime fechaFin
    ) {
        byte[] pdf = iLogActividadService.exportarLogsPdf(fechaInicio, fechaFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("logs_auditoria.pdf")
                        .build()
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdf);
    }

    @GetMapping("/ordenes/excel")
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

    @GetMapping("/ordenes/pdf")
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

    @GetMapping("/ventas/pdf")
    public ResponseEntity<byte[]> exportarVentasPdf(
            @RequestParam(required = false) LocalDateTime fechaInicio,
            @RequestParam(required = false) LocalDateTime fechaFin
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

    @GetMapping("/ventas/excel")
    public ResponseEntity<byte[]> exportarVentasExcel(
            @RequestParam(required = false) LocalDateTime fechaInicio,
            @RequestParam(required = false) LocalDateTime fechaFin
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
