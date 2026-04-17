package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.OrdendetalleDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductoCantidadDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.Orden;
import com.nebulaparfums.nebula_parfums.model.OrdenDetalle;
import com.nebulaparfums.nebula_parfums.model.Producto;
import com.nebulaparfums.nebula_parfums.repository.IOrdenDetalleRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenDetalleService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProductoService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class OrdenDetalleService implements IOrdenDetalleService {

    @Autowired
    private IOrdenDetalleRepository ordenDetalleRepository;

    @Autowired
    private IOrdenService ordenService;

    @Autowired
    private IProductoService productoService;

    @Override
    public void saveOrdenDetalle(OrdenDetalle ordenDetalle) {
        ordenDetalleRepository.save(ordenDetalle);
    }

    @Override
    public void deleteOrdenDetalleById(Integer id) {
        ordenDetalleRepository.deleteById(id);
    }

    @Override
    public void editOrdenDetalle(OrdenDetalle ordenDetalle) {
        this.saveOrdenDetalle(ordenDetalle);
    }

    @Override
    public OrdenDetalle getOrdenDetalleById(Integer id) {
        OrdenDetalle ordenDetalle = ordenDetalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el producto en la orden"));
        return ordenDetalle;
    }

    @Override
    public void createOrdenDetalle(OrdendetalleDTO detalleDTO) {
        Orden orden = ordenService.getOrdenById(detalleDTO.getId_orden());
        Producto producto = productoService.getProductoById(detalleDTO.getId_producto());

        OrdenDetalle ordenDetalle = new OrdenDetalle();
        ordenDetalle.setOrden(orden);
        ordenDetalle.setProducto(producto);
        ordenDetalle.setCantidad(detalleDTO.getCantidad());
        ordenDetalle.setPrecio(detalleDTO.getPrecio());
        saveOrdenDetalle(ordenDetalle);
    }

    @Override
    public List<ProductoCantidadDTO> getProductoCantidadDTO(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ordenDetalleRepository.contarCantidadPorProducto(fechaInicio, fechaFin);
    }

    @Override
    public byte[] exportarVentasExcel(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = null;
        LocalDateTime fin = null;

        if (fechaInicio != null) {
            inicio = fechaInicio.atStartOfDay();
        }

        if (fechaFin != null) {
            fin = fechaFin.atTime(LocalTime.MAX);
        }

        List<ProductoCantidadDTO> ventas = getProductoCantidadDTO(inicio, fin);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Ventas");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Producto");
            header.createCell(1).setCellValue("Total unidades");

            int rowIndex = 1;

            for (ProductoCantidadDTO item : ventas) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(
                        item.getProducto() != null ? item.getProducto() : ""
                );
                row.createCell(1).setCellValue(
                        item.getTotalUnidades() != null ? item.getTotalUnidades() : 0
                );
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el Excel de ventas", e);
        }
    }

    @Override
    public byte[] exportarVentasPdf(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = null;
        LocalDateTime fin = null;

        if (fechaInicio != null) {
            inicio = fechaInicio.atStartOfDay();
        }

        if (fechaFin != null) {
            fin = fechaFin.atTime(LocalTime.MAX);
        }

        List<ProductoCantidadDTO> ventas = getProductoCantidadDTO(inicio, fin);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            org.openpdf.text.Document document = new org.openpdf.text.Document();
            org.openpdf.text.pdf.PdfWriter.getInstance(document, baos);

            document.open();

            org.openpdf.text.Font tituloFont =
                    new org.openpdf.text.Font(org.openpdf.text.Font.HELVETICA, 16, org.openpdf.text.Font.BOLD);

            org.openpdf.text.Paragraph titulo =
                    new org.openpdf.text.Paragraph("Reporte de Ventas", tituloFont);
            titulo.setAlignment(org.openpdf.text.Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10f);
            document.add(titulo);

            org.openpdf.text.pdf.PdfPTable table = new org.openpdf.text.pdf.PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 2f});

            table.addCell("Producto");
            table.addCell("Total unidades");

            for (ProductoCantidadDTO item : ventas) {
                table.addCell(item.getProducto() != null ? item.getProducto() : "");
                table.addCell(item.getTotalUnidades() != null ? String.valueOf(item.getTotalUnidades()) : "0");
            }

            document.add(table);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de ventas", e);
        }
    }
}