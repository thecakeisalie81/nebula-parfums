package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.LogDTO;
import com.nebulaparfums.nebula_parfums.dto.MovimientoDTO;
import com.nebulaparfums.nebula_parfums.exception.InvalidQuantityException;
import com.nebulaparfums.nebula_parfums.exception.QuantityBelowZeroException;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;
import com.nebulaparfums.nebula_parfums.model.Producto;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.IMovimientoInventarioRepository;
import com.nebulaparfums.nebula_parfums.repository.IProductoRepository;
import com.nebulaparfums.nebula_parfums.repository.IUsuarioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ILogActividadService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IMovimientoInventarioService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.jspecify.annotations.NonNull;
import org.openpdf.text.Paragraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * Servicio para gestionar movimientos de inventario.
 * Provee operaciones de consulta, guardado, eliminación, edición, conversión a DTO y exportación de datos a pdf y excel.
 */
@Service
@AllArgsConstructor
public class MovimientoInventarioService implements IMovimientoInventarioService {

    private final IMovimientoInventarioRepository iMovimientoInventarioRepository;
    private final IProductoRepository iProductoRepository;
    protected final IUsuarioRepository iUsuarioRepository;
    private final ILogActividadService iLogActividadService;
    

    /**
     * Devuelve una cantidad de movimientos determinada por el parámetro de límite, los movimientos están ordenados
     * por fecha de forma descendente
     * @param limite número de elementos que devuelve
     * @return lista de MovimientoDTO
     */
    @Override
    public List<MovimientoDTO> ultimosMovimientos(Integer limite) {
        return iMovimientoInventarioRepository.ultimosMovimientoInventario(
                PageRequest.of(0, limite)).stream().map(Mapper::toDTO).toList();
    }

    /**
     * Filtra los movimientos por diversos parámetros y los devuelve paginados
     * @param pageable información de paginación (número de página, tamaño, orden)
     * @param producto nombre del producto al que se le hizo un movimiento
     * @param tipo tipo de movimiento registrado
     * @param fechaInicio fecha inicial del filtrado
     * @param fechaFin fecha final del filtrado
     * @return paginado con MovimientoDTO que encajen con el filtrado
     */
    @Override
    public Page<MovimientoDTO> filtrarMovimientos(
            Pageable pageable,
            String producto,
            String tipo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    ) {
        return iMovimientoInventarioRepository.filtrarMovimientos(
                pageable,
                producto,
                tipo,
                fechaInicio,
                fechaFin
        ).map(Mapper::toDTO);
    }

    /**
     * Filtra los movimientos por rango de fecha
     * @param fechaInicio fecha inicial del filtrado
     * @param fechaFin fecha final del filtrado
     * @return lista de MovimientosDTO en el rango de fecha designado
     */
    @Override
    public List<MovimientoDTO> filtrarMovimientosReportes(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return iMovimientoInventarioRepository.filtrarMovimientosReportes(fechaInicio, fechaFin)
                .stream().map(Mapper::toDTO).toList();
    }


    /**
     *Registra un nuevo movimiento en la base de datos
     * @param movimientoInventario MovimientoDTO con los datos a insertar
     * @return MovimientoDTO con los datos recién insertados
     */
    @Override
    public MovimientoDTO saveMovimientoInventario(MovimientoDTO movimientoInventario) {
        MovimientoInventario movimiento = MovimientoInventario.builder()
                .tipo_movimiento(movimientoInventario.getTipo_movimiento())
                .cantidad(movimientoInventario.getCantidad())
                .fecha_movimiento(movimientoInventario.getFecha_movimiento())
                .producto(iProductoRepository.findById(movimientoInventario.getId_producto())
                        .orElseThrow(()-> new ResourceNotFoundException("No se encontró el producto")))
                .usuario(iUsuarioRepository.findById(movimientoInventario.getId_usuario())
                        .orElseThrow(()-> new ResourceNotFoundException("No se encontró el usuario")))
                .build();
        return Mapper.toDTO(iMovimientoInventarioRepository.save(movimiento));
    }


    /**
     * Registra una salida y el log de actividad
     * @param movimiento MovimientoDTO con los datos a registrar en la salida
     * @return MovimientoDTO con los datos que se acaban de insertar en la db
     */
    @Transactional
    public MovimientoDTO registrarSalida(MovimientoDTO movimiento){

        Usuario usuario = iUsuarioRepository.findById(movimiento.getId_usuario())
                .orElseThrow(()-> new ResourceNotFoundException("No se encontró el usuario"));

        Producto producto = iProductoRepository.findById(movimiento.getId_producto())
                .orElseThrow(()-> new ResourceNotFoundException("No se encontró el producto"));

        if (movimiento.getCantidad() <= 0) {
            throw new InvalidQuantityException("Cantidad inválida");
        }

        if (producto.getStock_actual()< movimiento.getCantidad()) {
            throw new QuantityBelowZeroException("No hay cantidad suficiente del producto: " + producto.getNombre()+
                    "Solo quedan "+producto.getStock_actual());
        }

        producto.setStock_actual(producto.getStock_actual()-movimiento.getCantidad());
        iProductoRepository.save(producto);

        MovimientoDTO movimientoDTO = saveMovimientoInventario(movimiento);

        LogDTO log = LogDTO.builder()
                .accion("Modificación de inventario")
                .fecha_actualizacion(LocalDateTime.now())
                .detalle("Usuario " + usuario.getNombre() + " realizo una salida de producto " + producto.getNombre())
                .usuario_id(movimiento.getId_usuario())
                .build();

        iLogActividadService.saveLogActividad(log);

        return movimientoDTO;
    }

    /**
     * Registra una entrada y el log de actividad
     * @param movimiento MovimientoDTO con los datos a registrar en la entrada
     * @return MovimientoDTO con los datos que se acaban de insertar en la db
     */
    @Transactional
    public MovimientoDTO registrarEntrada(MovimientoDTO movimiento){

        Usuario usuario = iUsuarioRepository.findById(movimiento.getId_usuario())
                .orElseThrow(()-> new ResourceNotFoundException("No se encontró el usuario"));

        Producto producto = iProductoRepository.findById(movimiento.getId_producto())
                .orElseThrow(()-> new ResourceNotFoundException("No se encontró el producto"));

        if (movimiento.getCantidad() <= 0) {
            throw new InvalidQuantityException("Cantidad inválida");
        }

        producto.setStock_actual(producto.getStock_actual()+movimiento.getCantidad());
        iProductoRepository.save(producto);

        MovimientoDTO movimientoDTO = saveMovimientoInventario(movimiento);

        LogDTO log = LogDTO.builder()
                .accion("Modificación de inventario")
                .fecha_actualizacion(LocalDateTime.now())
                .detalle("Usuario " + usuario.getNombre() + " realizo una entrada de producto " + producto.getNombre())
                .usuario_id(movimiento.getId_usuario())
                .build();

        iLogActividadService.saveLogActividad(log);

        return movimientoDTO;
    }

    /**
     * Registra el registro de un producto y el log de actividad
     * @param movimiento MovimientoDTO con los datos a registrar en el registro del producto
     * @return MovimientoDTO con los datos que se acaban de insertar en la db
     */
    @Override
    @Transactional
    public MovimientoDTO registrarRegistroProducto(MovimientoDTO movimiento) {

        Usuario usuario = iUsuarioRepository.findById(movimiento.getId_usuario())
                .orElseThrow(()-> new ResourceNotFoundException("No se encontró el usuario"));

        Producto producto = iProductoRepository.findById(movimiento.getId_producto())
                .orElseThrow(()-> new ResourceNotFoundException("No se encontró el producto"));

        if (movimiento.getCantidad() < 0) {
            throw new InvalidQuantityException("Cantidad inválida");
        }

        MovimientoDTO movimientoDTO = saveMovimientoInventario(movimiento);

        LogDTO log = LogDTO.builder()
                .accion("Registro de producto")
                .fecha_actualizacion(LocalDateTime.now())
                .detalle("Usuario " + usuario.getNombre() + " realizo el registro del producto " + producto.getNombre())
                .usuario_id(movimiento.getId_usuario())
                .build();

        iLogActividadService.saveLogActividad(log);

        return movimientoDTO;
    }

    /**
     * Registra la modification de un producto y el log de actividad
     * @param movimientoDTO DTO con los datos a registrar en el registro del producto
     * @return MovimientoDTO con los datos que se acaban de insertar en la db
     */
    @Override
    @Transactional
    public MovimientoDTO registrarEdicionProducto(MovimientoDTO movimientoDTO) {

        Usuario usuario = iUsuarioRepository.findById(movimientoDTO.getId_usuario())
                .orElseThrow(()-> new ResourceNotFoundException("No se encontró el usuario"));

        Producto producto = iProductoRepository.findById(movimientoDTO.getId_producto())
                .orElseThrow(()-> new ResourceNotFoundException("No se encontró el producto"));

        if (movimientoDTO.getCantidad() < 0) {
            throw new InvalidQuantityException("Cantidad inválida");
        }

        MovimientoDTO movimiento = saveMovimientoInventario(movimientoDTO);

        LogDTO log = LogDTO.builder()
                .accion("Edición de producto")
                .fecha_actualizacion(LocalDateTime.now())
                .detalle("Usuario " + usuario.getNombre() + " modifico los datos del producto " + producto.getNombre())
                .usuario_id(movimiento.getId_usuario())
                .build();

        iLogActividadService.saveLogActividad(log);

        return movimientoDTO;
    }


    /**
     *Filtra los movimientos por rango de fecha en formato de excel
     * @param fechaInicio fecha inicial del filtrado
     * @param fechaFin final inicial del filtrado
     * @return Archivo Excel en memoria representado como arreglo de bytes
     */
    @Override
    public byte[] exportarMovimientosExcel(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<MovimientoDTO> movimientos = filtrarMovimientosReportes(fechaInicio, fechaFin);

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

            for (MovimientoDTO mov : movimientos) {
                Row row = sheet.createRow(rowIndex++);
                Optional<Producto> producto = iProductoRepository.findById(mov.getId_producto());
                Optional<Usuario> usuario = iUsuarioRepository.findById(mov.getId_usuario());

                row.createCell(0).setCellValue(
                        mov.getFecha_movimiento() != null ? mov.getFecha_movimiento().toString() : ""
                );
                row.createCell(1).setCellValue(
                        mov.getTipo_movimiento() != null ? mov.getTipo_movimiento().name() : ""
                );
                row.createCell(2).setCellValue(
                        producto.isPresent() ? producto.get().getNombre() : ""
                );
                row.createCell(3).setCellValue(
                        mov.getCantidad() != null ? mov.getCantidad() : 0
                );
                row.createCell(4).setCellValue(
                        usuario.isPresent() ? usuario.get().getNombre() : ""
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

    /**
     *Filtra los movimientos por rango de fecha en formato de pdf
     * @param fechaInicio fecha inicial del filtrado
     * @param fechaFin fecha final del filtrado
     * @return Archivo PDF en memoria representado como arreglo de bytes
     */
    @Override
    public byte[] exportarMovimientosPdf(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<MovimientoDTO> movimientos = filtrarMovimientosReportes(fechaInicio, fechaFin);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            org.openpdf.text.Document document = new org.openpdf.text.Document();
            org.openpdf.text.pdf.PdfWriter.getInstance(document, baos);

            document.open();

            Paragraph titulo = getTitulo();
            document.add(titulo);

            org.openpdf.text.pdf.PdfPTable table = new org.openpdf.text.pdf.PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.5f, 2f, 3f, 1.5f, 2.5f});

            table.addCell("Fecha");
            table.addCell("Tipo");
            table.addCell("Producto");
            table.addCell("Cantidad");
            table.addCell("Usuario");

            for (MovimientoDTO mov : movimientos) {
                Optional<Producto> producto = iProductoRepository.findById(mov.getId_producto());
                Optional<Usuario> usuario = iUsuarioRepository.findById(mov.getId_usuario());

                table.addCell(mov.getFecha_movimiento() != null ? mov.getFecha_movimiento().toString() : "");
                table.addCell(mov.getTipo_movimiento() != null ? mov.getTipo_movimiento().name() : "");
                table.addCell(producto.isPresent() ? producto.get().getNombre() : "");
                table.addCell(mov.getCantidad() != null ? String.valueOf(mov.getCantidad()) : "0");
                table.addCell(usuario.isPresent() ? usuario.get().getNombre() : "");
            }

            document.add(table);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de movimientos", e);
        }
    }

    /**
     * Metodo auxiliar para definir las fuentes usadas en el documento PDF
     * y construir el título principal del reporte.
     * @return Párrafo configurado con estilo de título para el documento PDF
     */
    private static @NonNull Paragraph getTitulo() {
        org.openpdf.text.Font tituloFont =
                new org.openpdf.text.Font(org.openpdf.text.Font.HELVETICA, 16, org.openpdf.text.Font.BOLD);
        org.openpdf.text.Font bodyFont =
                new org.openpdf.text.Font(org.openpdf.text.Font.HELVETICA, 10, org.openpdf.text.Font.NORMAL);

        Paragraph titulo =
                new Paragraph("Reporte de Movimientos", tituloFont);
        titulo.setAlignment(org.openpdf.text.Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        return titulo;
    }
}
