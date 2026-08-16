package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.OrdenDTO;
import com.nebulaparfums.nebula_parfums.dto.OrdenDetalleDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductosPendientesProceso;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.model.*;
import com.nebulaparfums.nebula_parfums.repository.IOrdenRepository;
import com.nebulaparfums.nebula_parfums.repository.IProductoRepository;
import com.nebulaparfums.nebula_parfums.repository.IUsuarioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.*;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jspecify.annotations.NonNull;
import org.openpdf.text.pdf.PdfPTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar órdenes.
 * Provee operaciones de consulta, guardado, eliminación, edición, conversión a DTO, y exportación de datos a pdf y excel.
 */
@Service
@AllArgsConstructor
public class OrdenService implements IOrdenService {
    private final IOrdenRepository ordenRepository;
    private final IMovimientoInventarioService  movimientoInventarioService;
    private final IUsuarioRepository usuarioRepository;
    private final IProductoRepository productoRepository;


    @Override
    public List<OrdenDTO> getOrdenes() {
        return ordenRepository.findAll().stream().map(Mapper::toDTO).toList();
    }

    @Override
    public Orden getOrdenById(Integer ordenId) {
        return ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la orden"));
    }

    /**
     * Devuelve una lista con las órdenes de un usuario
     * @param id id del usuario
     * @return lista de ordenes
     */
    @Override
    public List<OrdenDTO> getOrdenesUsuario(Integer id) {
        return ordenRepository.getOrdenesUsuario(id).stream().map(Mapper::toDTO).toList();
    }

    @Override
    public List<OrdenDTO> getUltimasOrdenesPendiente(Pageable pageable) {
        return ordenRepository.ultimasOrdenesPendiente(pageable).stream().map(Mapper::toDTO).toList();
    }

    /**
     * Filtra las órdenes por diversos parámetros
     * @param pageable información de paginación (número de página, tamaño, orden)
     * @param estado estado de la orden
     * @param fechaInicio fecha inicial de filtrado
     * @param fechaFin fecha final de filtrado
     * @return DTOS de orden en formato paginado filtrado por los parámetros
     */
    @Override
    public Page<OrdenDTO> filtrarOrden(Pageable pageable, String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ordenRepository.filtrarOrden(pageable, estado, fechaInicio, fechaFin).map(Mapper::toDTO);
    }

    /**
     * Guarda una nueva orden en la base de datos
     * @param orden DTO con los datos de la nueva orden
     * @return DTO con los datos que fueron insertados
     */
    @Override
    public OrdenDTO saveOrden(OrdenDTO orden) {

        List<OrdenDetalle> listaOrdenes = new ArrayList<>();

        for (OrdenDetalleDTO orderDTO : orden.getOrdenDetalles()){
            OrdenDetalle ordenDetalle = OrdenDetalle.builder()
                    .id_orden_detalle(orderDTO.getId_orden_detalle())
                    .cantidad(orderDTO.getCantidad())
                    .precio(orderDTO.getPrecio())
                    .producto(productoRepository.findById(orderDTO.getId_producto())
                            .orElseThrow(() -> new ResourceNotFoundException("producto no encontrado")))
                    .orden(getOrdenById(orderDTO.getId_orden()))
                    .build();
            listaOrdenes.add(ordenDetalle);
        }

        Orden Order = Orden.builder()
                .total(orden.getTotal())
                .estado(orden.getEstado())
                .fecha_creacion(LocalDateTime.now())
                .direccion(orden.getDireccion())
                .usuario(usuarioRepository.findById(orden.getId_cliente())
                        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado")))
                .listaOrdenDetalle(listaOrdenes)
                .build();

        return Mapper.toDTO(ordenRepository.save(Order));
    }

    /**
     * Edita una orden de la base de datos
     * @param orden DTO con los nuevos datos de la orden
     * @return DTO con los datos recién actualizados
     */
    @Override
    public OrdenDTO editOrden(Integer id, OrdenDTO orden) {

        Orden order = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrado"));

        if (orden.getDireccion() != null) {
            order.setDireccion(orden.getDireccion());
        }
        if (orden.getEstado() != null) {
            order.setEstado(orden.getEstado());
        }
        if (orden.getTotal() != null) {
            order.setTotal(orden.getTotal());
        }
        if (orden.getOrdenDetalles() != null) {
            List<OrdenDetalle> listaOrdenes = new ArrayList<>();
            for (OrdenDetalleDTO orderDTO : orden.getOrdenDetalles()){
                OrdenDetalle ordenDetalle = OrdenDetalle.builder()
                        .id_orden_detalle(orderDTO.getId_orden_detalle())
                        .cantidad(orderDTO.getCantidad())
                        .precio(orderDTO.getPrecio())
                        .producto(productoRepository.findById(orderDTO.getId_producto())
                                .orElseThrow(() -> new ResourceNotFoundException("producto no encontrado")))
                        .orden(getOrdenById(orderDTO.getId_orden()))
                        .build();
                listaOrdenes.add(ordenDetalle);
            }

            order.setListaOrdenDetalle(listaOrdenes);
        }

        return Mapper.toDTO(ordenRepository.save(order));
    }


    /**
     * Sumatoria de las ventas totales de un rango de fechas
     * @param fechaInicio fecha inicial del filtrado
     * @param fechaFin fecha final del filtrado
     * @return Dinero total de ventas
     */
    @Override
    public Double sumaTotalesMes(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ordenRepository.sumaTotalesMesActual(fechaInicio, fechaFin);
    }

    /**
     * Cuenta cuantas órdenes están pendientes y en proceso
     * @return DTO con el número total de órdenes en esos estados
     */
    @Override
    public ProductosPendientesProceso getPendientesProcesos() {
        ProductosPendientesProceso cuenta = new ProductosPendientesProceso();
        cuenta.setPendientes(ordenRepository.countByEstado("PENDIENTE"));
        cuenta.setProceso(ordenRepository.countByEstado("EN PROCESO"));
        return cuenta;
    }

    /**
     *Filtra las órdenes por un rango de fechas
     * @param fechaInicio fecha inicial del filtrado
     * @param fechaFin fecha final del filtrado
     * @return Lista de DTO todas las órdenes según el rango de fecha
     */
    @Override
    public List<OrdenDTO> listarDatosOrdenes(LocalDate fechaInicio, LocalDate fechaFin) {
        return ordenRepository.findOrdenesByFecha(fechaInicio, fechaFin);
    }

    @Override
    public void deleteOrden(Integer ordenId) {
        if (ordenRepository.existsById(ordenId)) {
            ordenRepository.deleteById(ordenId);
        }else {
            throw new ResourceNotFoundException("No se encontró la orden");
        }
    }


    /**
     * Exporta los pedidos en formato Excel dentro de un rango de fechas.
     * @param fechaInicio fecha inicial del filtrado.
     * @param fechaFin fecha final del filtrado.
     * @return Archivo Excel en memoria representado como arreglo de bytes.
     * @throws RuntimeException si ocurre un error al generar el archivo.
     */
    @Override
    public byte[] exportarPedidosExcel(LocalDate fechaInicio, LocalDate fechaFin) {
        List<OrdenDTO> pedidos = listarDatosOrdenes(fechaInicio, fechaFin);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Pedidos");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID Orden");
            header.createCell(1).setCellValue("ID Cliente");
            header.createCell(2).setCellValue("ID Dirección");
            header.createCell(3).setCellValue("Estado");
            header.createCell(4).setCellValue("Total");

            int rowIndex = 1;

            for (OrdenDTO orden : pedidos) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(
                        orden.getId_orden() != null ? orden.getId_orden() : 0
                );
                row.createCell(1).setCellValue(
                        orden.getId_cliente() != null ? orden.getId_cliente() : 0
                );
                row.createCell(2).setCellValue(
                        orden.getDireccion() != null ? orden.getDireccion() : ""
                );
                row.createCell(3).setCellValue(
                        orden.getEstado() != null ? orden.getEstado().name() : ""
                );
                row.createCell(4).setCellValue(
                        orden.getTotal() != null ? orden.getTotal() : 0
                );
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el Excel de pedidos", e);
        }
    }


    /**
     * Exporta los pedidos en formato PDF dentro de un rango de fechas.
     * @param fechaInicio fecha inicial del filtrado.
     * @param fechaFin fecha final del filtrado.
     * @return Archivo PDF en memoria representado como arreglo de bytes.
     * @throws RuntimeException si ocurre un error al generar el archivo.
     */
    @Override
    public byte[] exportarPedidosPdf(LocalDate fechaInicio, LocalDate fechaFin) {
        List<OrdenDTO> pedidos = listarDatosOrdenes(fechaInicio, fechaFin);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            org.openpdf.text.Document document = new org.openpdf.text.Document();
            org.openpdf.text.pdf.PdfWriter.getInstance(document, baos);

            document.open();

            org.openpdf.text.Font tituloFont =
                    new org.openpdf.text.Font(org.openpdf.text.Font.HELVETICA, 16, org.openpdf.text.Font.BOLD);

            org.openpdf.text.Paragraph titulo =
                    new org.openpdf.text.Paragraph("Reporte de Pedidos", tituloFont);
            titulo.setAlignment(org.openpdf.text.Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10f);
            document.add(titulo);

            PdfPTable table = createTable(pedidos);

            document.add(table);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de pedidos", e);
        }
    }

    /**
     * Construye una tabla PDF con la información de los pedidos.
     * @param pedidos lista de pedidos a mostrar en la tabla.
     * @return objeto PdfPTable con las columnas ID Orden, ID Cliente, ID Dirección, Estado y Total.
     */
    private static @NonNull PdfPTable createTable(List<OrdenDTO> pedidos) {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 2f, 2f, 2.5f, 2f});

        table.addCell("ID Orden");
        table.addCell("ID Cliente");
        table.addCell("ID Dirección");
        table.addCell("Estado");
        table.addCell("Total");

        for (OrdenDTO orden : pedidos) {
            table.addCell(orden.getId_orden() != null ? String.valueOf(orden.getId_orden()) : "0");
            table.addCell(orden.getId_cliente() != null ? String.valueOf(orden.getId_cliente()) : "0");
            table.addCell(orden.getDireccion() != null ? orden.getDireccion() : "");
            table.addCell(orden.getEstado() != null ? orden.getEstado().name() : "");
            table.addCell(orden.getTotal() != null ? String.valueOf(orden.getTotal()) : "0");
        }
        return table;
    }
}
