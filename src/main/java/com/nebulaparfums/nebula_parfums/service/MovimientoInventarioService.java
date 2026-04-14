package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.MovimientoDTO;
import com.nebulaparfums.nebula_parfums.exception.QuantityBelowZeroException;
import com.nebulaparfums.nebula_parfums.model.LogActividad;
import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;
import com.nebulaparfums.nebula_parfums.model.Producto;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.IMovimientoInventarioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ILogActividadService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IMovimientoInventarioService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProductoService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class MovimientoInventarioService implements IMovimientoInventarioService {

    @Autowired
    private IMovimientoInventarioRepository iMovimientoInventarioRepository;
    @Autowired
    private IProductoService iProductoService;
    @Autowired
    protected IUsuarioService iUsuarioService;
    @Autowired
    private ILogActividadService iLogActividadService;


    @Override
    public Page<MovimientoInventario> getMovimientoInventario(Pageable pageable) {
        return iMovimientoInventarioRepository.findAll(pageable);
    }

    @Override
    public void saveMovimientoInventario(MovimientoInventario movimientoInventario) {
        iMovimientoInventarioRepository.save(movimientoInventario);
    }

    @Transactional
    public String registrarSalida(Integer productoId, int cantidad){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = iUsuarioService.getUsuarioByEmail(username);

        Producto producto = iProductoService.getProductoById(productoId);

        if (cantidad <= 0) {
            throw new RuntimeException("Cantidad inválida");
        }

        if (producto.getStock_actual()<cantidad){
            throw new QuantityBelowZeroException("No hay cantidad suficiente de el producto: " + producto.getNombre()+
                    "Solo quedan "+producto.getStock_actual());
        }

        producto.setStock_actual(producto.getStock_actual()-cantidad);
        iProductoService.saveProducto(producto);

        MovimientoInventario movimientoInventario = new MovimientoInventario();
        movimientoInventario.setProducto(producto);
        movimientoInventario.setCantidad(cantidad);
        movimientoInventario.setTipo_movimiento("SALIDA");
        movimientoInventario.setFecha_movimiento(LocalDateTime.now());
        movimientoInventario.setUsuario(usuario);

        saveMovimientoInventario(movimientoInventario);

        LogActividad logActividad = new LogActividad();
        logActividad.setUsuario(usuario);
        logActividad.setAccion("Modificacion de inventario");
        logActividad.setDetalle("Usuario " + usuario.getNombre() + " realizo una salida de producto " + producto.getNombre());
        logActividad.setFecha_actualizacion(LocalDateTime.now());

        iLogActividadService.saveLogActividad(logActividad);

        return "Se registro el movimiento correctamente";
    }

    @Transactional
    public String registrarEntrada(Integer productoId, int cantidad){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = iUsuarioService.getUsuarioByEmail(username);

        Producto producto = iProductoService.getProductoById(productoId);

        if (cantidad <= 0) {
            throw new RuntimeException("Cantidad inválida");
        }

        producto.setStock_actual(producto.getStock_actual()+cantidad);
        iProductoService.saveProducto(producto);

        MovimientoInventario movimientoInventario = new MovimientoInventario();
        movimientoInventario.setProducto(producto);
        movimientoInventario.setCantidad(cantidad);
        movimientoInventario.setTipo_movimiento("ENTRADA");
        movimientoInventario.setFecha_movimiento(LocalDateTime.now());
        movimientoInventario.setUsuario(usuario);

        saveMovimientoInventario(movimientoInventario);

        LogActividad logActividad = new LogActividad();
        logActividad.setUsuario(usuario);
        logActividad.setAccion("Modificacion de inventario");
        logActividad.setDetalle("Usuario " + usuario.getNombre() + " realizo una entrada de producto "+ producto.getNombre());
        logActividad.setFecha_actualizacion(LocalDateTime.now());

        iLogActividadService.saveLogActividad(logActividad);

        return "Se registro el movimiento correctamente";
    }

    @Override
    @Transactional
    public String registrarRegistroProducto(MovimientoDTO movimientoDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = iUsuarioService.getUsuarioByEmail(username);

        Producto producto = iProductoService.getProductoById(movimientoDTO.getId_producto());

        MovimientoInventario movimientoInventario = new MovimientoInventario();
        movimientoInventario.setProducto(producto);
        movimientoInventario.setCantidad(producto.getStock_actual());
        movimientoInventario.setTipo_movimiento("REGISTRO");
        movimientoInventario.setFecha_movimiento(LocalDateTime.now());
        movimientoInventario.setUsuario(usuario);

        saveMovimientoInventario(movimientoInventario);

        LogActividad logActividad = new LogActividad();
        logActividad.setUsuario(usuario);
        logActividad.setAccion("Registro de producto");
        logActividad.setDetalle("Usuario " + usuario.getNombre() + " realizo un registro de producto "+ producto.getNombre());
        logActividad.setFecha_actualizacion(LocalDateTime.now());

        iLogActividadService.saveLogActividad(logActividad);

        return  "Se registro el movimiento correctamente";
    }

    @Override
    public List<MovimientoInventario> ultimos5Movimientos() {
        Pageable limiteCinco = PageRequest.of(0, 5);
        return iMovimientoInventarioRepository.ultimosMovimientoInventario(limiteCinco);
    }

    @Override
    public Page<MovimientoInventario> filtrarMovimientos(
            Pageable pageable,
            String producto,
            String tipo,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        LocalDateTime inicio = null;
        LocalDateTime fin = null;

        if (fechaInicio != null) {
            inicio = fechaInicio.atStartOfDay();
        }

        if (fechaFin != null) {
            fin = fechaFin.atTime(LocalTime.MAX);
        }

        if (producto != null && producto.isBlank()) {
            producto = null;
        }

        if (tipo != null && tipo.isBlank()) {
            tipo = null;
        }

        return iMovimientoInventarioRepository.filtrarMovimientos(
                pageable,
                producto,
                tipo,
                inicio,
                fin
        );
    }

    @Override
    public List<MovimientoInventario> filtrarMovimientosReportes(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = null;
        LocalDateTime fin = null;

        if (fechaInicio != null) {
            inicio = fechaInicio.atStartOfDay();
        }

        if (fechaFin != null) {
            fin = fechaFin.atTime(LocalTime.MAX);
        }

        return iMovimientoInventarioRepository.filtrarMovimientosReportes(inicio, fin);
    }


    @Override
    public byte[] exportarMovimientosExcel(LocalDate fechaInicio, LocalDate fechaFin) {
        List<MovimientoInventario> movimientos = filtrarMovimientosReportes(fechaInicio, fechaFin);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Movimientos");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Fecha");
            header.createCell(1).setCellValue("Tipo");
            header.createCell(2).setCellValue("Producto");
            header.createCell(3).setCellValue("Cantidad");
            header.createCell(4).setCellValue("Usuario");

            int rowIndex = 1;

            for (MovimientoInventario mov : movimientos) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(
                        mov.getFecha_movimiento() != null ? mov.getFecha_movimiento().toString() : ""
                );
                row.createCell(1).setCellValue(
                        mov.getTipo_movimiento() != null ? mov.getTipo_movimiento() : ""
                );
                row.createCell(2).setCellValue(
                        mov.getProducto() != null ? mov.getProducto().getNombre() : ""
                );
                row.createCell(3).setCellValue(
                        mov.getCantidad() != null ? mov.getCantidad() : 0
                );
                row.createCell(4).setCellValue(
                        mov.getUsuario() != null ? mov.getUsuario().getNombre() : ""
                );
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el Excel de movimientos", e);
        }
    }

    @Override
    public byte[] exportarMovimientosPdf(LocalDate fechaInicio, LocalDate fechaFin) {
        List<MovimientoInventario> movimientos = filtrarMovimientosReportes(fechaInicio, fechaFin);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            org.openpdf.text.Document document = new org.openpdf.text.Document();
            org.openpdf.text.pdf.PdfWriter.getInstance(document, baos);

            document.open();

            org.openpdf.text.Font tituloFont =
                    new org.openpdf.text.Font(org.openpdf.text.Font.HELVETICA, 16, org.openpdf.text.Font.BOLD);
            org.openpdf.text.Font bodyFont =
                    new org.openpdf.text.Font(org.openpdf.text.Font.HELVETICA, 10, org.openpdf.text.Font.NORMAL);

            org.openpdf.text.Paragraph titulo =
                    new org.openpdf.text.Paragraph("Reporte de Movimientos", tituloFont);
            titulo.setAlignment(org.openpdf.text.Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10f);
            document.add(titulo);

            org.openpdf.text.pdf.PdfPTable table = new org.openpdf.text.pdf.PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.5f, 2f, 3f, 1.5f, 2.5f});

            table.addCell("Fecha");
            table.addCell("Tipo");
            table.addCell("Producto");
            table.addCell("Cantidad");
            table.addCell("Usuario");

            for (MovimientoInventario mov : movimientos) {
                table.addCell(mov.getFecha_movimiento() != null ? mov.getFecha_movimiento().toString() : "");
                table.addCell(mov.getTipo_movimiento() != null ? mov.getTipo_movimiento() : "");
                table.addCell(mov.getProducto() != null ? mov.getProducto().getNombre() : "");
                table.addCell(mov.getCantidad() != null ? String.valueOf(mov.getCantidad()) : "0");
                table.addCell(mov.getUsuario() != null ? mov.getUsuario().getNombre() : "");
            }

            document.add(table);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de movimientos", e);
        }
    }

}
