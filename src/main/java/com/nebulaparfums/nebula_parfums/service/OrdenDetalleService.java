package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.OrdenDetalleDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductoCantidadDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.model.OrdenDetalle;
import com.nebulaparfums.nebula_parfums.repository.IOrdenDetalleRepository;
import com.nebulaparfums.nebula_parfums.repository.IOrdenRepository;
import com.nebulaparfums.nebula_parfums.repository.IProductoRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenDetalleService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jspecify.annotations.NonNull;
import org.openpdf.text.pdf.PdfPTable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class OrdenDetalleService implements IOrdenDetalleService {
    private IOrdenDetalleRepository ordenDetalleRepository;
    private IOrdenRepository iOrdenRepository;
    private IProductoRepository iProductoRepository;


    @Override
    public OrdenDetalle getOrdenDetalleById(Integer id) {
        return ordenDetalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la orden"));
    }

    public OrdenDetalleDTO saveOrdenDetalle(OrdenDetalleDTO ordenDetalle) {
        OrdenDetalle orden = OrdenDetalle.builder()
                .cantidad(ordenDetalle.getCantidad())
                .precio(ordenDetalle.getPrecio())
                .producto(iProductoRepository.findById(ordenDetalle.getId_producto())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado")))
                .orden(iOrdenRepository.findById(ordenDetalle.getId_orden())
                        .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada")))
                .build();

        return Mapper.toDTO(ordenDetalleRepository.save(orden));
    }

    /**
     * Borra físicamente un detalle de una orden
     * @param id id del detalle a eliminar
     */
    @Override
    public void deleteOrdenDetalleById(Integer id) {
        ordenDetalleRepository.deleteById(id);
    }

    /**
     * Edita los datos de un detalle de una orden
     * @param id id del detalle a editar
     * @param ordenDetalle DTO con los nuevos datos
     * @return OrdenDetalleDTO actualizado
     */
    @Override
    public OrdenDetalleDTO editOrdenDetalle(Integer id,OrdenDetalleDTO ordenDetalle) {

        OrdenDetalle orden = ordenDetalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrado"));
        orden.setCantidad(ordenDetalle.getCantidad());
        orden.setPrecio(ordenDetalle.getPrecio());

        return Mapper.toDTO(ordenDetalleRepository.save(orden));
    }

    /**
     * Devuelve la cantidad de cada producto que se haya vendido en un rango de fechas
     * @param fechaInicio fecha inicial del filtrado
     * @param fechaFin fecha final del filtrado
     * @return lista de DTO con las cantidades vendidas de cada producto
     */
    @Override
    public List<ProductoCantidadDTO> getProductoCantidadDTO(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ordenDetalleRepository.contarCantidadPorProducto(fechaInicio, fechaFin);
    }

    /**
     * Filtra las ventas por rango de fecha en formato de excel
     * @param fechaInicio fecha inicial del filtrado
     * @param fechaFin fecha final del filtrado
     * @return Archivo Excel en memoria representado como arreglo de bytes
     */
    @Override
    public byte[] exportarVentasExcel(LocalDateTime fechaInicio, LocalDateTime fechaFin) {

        List<ProductoCantidadDTO> ventas = getProductoCantidadDTO(fechaInicio, fechaFin);

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

    /**
     * Filtra las ventas por rango de fecha en formato de pdf
     * @param fechaInicio fecha inicial del filtrado
     * @param fechaFin fecha final del filtrado
     * @return Archivo PDF en memoria representado como arreglo de bytes
     */
    @Override
    public byte[] exportarVentasPdf(LocalDateTime fechaInicio, LocalDateTime fechaFin) {

        List<ProductoCantidadDTO> ventas = getProductoCantidadDTO(fechaInicio, fechaFin);

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

            PdfPTable table = createTable(ventas);

            document.add(table);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de ventas", e);
        }
    }

    /**
     * Construye una tabla PDF con dos columnas: nombre del producto y total de unidades vendidas.
     * @param ventas lista de objetos ProductoCantidadDTO que contienen el nombre del producto y la cantidad total vendida
     * @return objeto PdfPTable con los datos organizados en formato de tabla
     */
    private static @NonNull PdfPTable createTable(List<ProductoCantidadDTO> ventas) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f, 2f});

        table.addCell("Producto");
        table.addCell("Total unidades");

        for (ProductoCantidadDTO item : ventas) {
            table.addCell(item.getProducto() != null ? item.getProducto() : "");
            table.addCell(item.getTotalUnidades() != null ? String.valueOf(item.getTotalUnidades()) : "0");
        }
        return table;
    }
}