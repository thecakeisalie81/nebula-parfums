package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.LogDTO;
import com.nebulaparfums.nebula_parfums.dto.TotalEventosyHoyDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.model.LogActividad;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.ILogActividadRepository;
import com.nebulaparfums.nebula_parfums.repository.IUsuarioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ILogActividadService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.openpdf.text.*;
import org.openpdf.text.Font;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para gestionar logs de actividad del sistema.
 * Provee operaciones de consulta, guardado, eliminación, edición y conversión a DTO.
 */
@Service
@AllArgsConstructor
@Builder
public class LogActividadService implements ILogActividadService {

    private final ILogActividadRepository logActividadRepository;
    private final IUsuarioRepository usuarioRepository;


    @Override
    public List<LogDTO> getLogsActividad() {
        return logActividadRepository.findAll().stream().map(Mapper::toDTO).toList();
    }

    /**
     * Devuelve la cantidad de eventos que hay registrados en el sistema y los que se han registrado hoy
     * @return DTO con el total de eventos y los de hoy
     */
    @Override
    public TotalEventosyHoyDTO getTotalEventosyHoyDTO() {
        return TotalEventosyHoyDTO.builder()
                .Hoy(logActividadRepository.contarLogsHoy())
                .Total(logActividadRepository.count())
                .build();
    }

    /**
     *Crea un nuevo log en la base de datos
     * @param logActividad DTO con los datos que se van a insertar en la db
     * @return DTO que tiene los datos recien insertados
     */
    @Override
    public LogDTO saveLogActividad(LogDTO logActividad) {

        LogActividad log = LogActividad.builder()
                .accion(logActividad.getAccion())
                .fecha_actualizacion(logActividad.getFecha_actualizacion())
                .detalle(logActividad.getDetalle())
                .usuario(usuarioRepository.findById(logActividad.getUsuario_id())
                        .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario")))
                .build();

        return Mapper.toDTO(logActividadRepository.save(log));
    }


    /**
     * Filtra los logs por tipo de accion y fecha
     * @param pageable información de paginación (número de página, tamaño, orden)
     * @param accion accion realizada por el usuario
     * @param fechaInicio fecha inicial para el filtrado
     * @param fechaFin fecha final para el filtrado
     * @return Paginado de DTO de los logs que cumplan con el filtrado
     */
    @Override
    public Page<LogDTO> filtrarLogs(Pageable pageable, String accion, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return logActividadRepository.filtrarLogActividades(
                pageable,
                accion,
                fechaInicio,
                fechaFin
        ).map(Mapper::toDTO);
    }


    /**
     * Filtra los logs por fecha para exportarlos en pdf
     * @param fechaInicio fecha inicial para el filtrado
     * @param fechaFin fecha final para el filtrado
     * @return lista con todos los DTO que cumplan el filtrado
     */
    @Override
    public List<LogDTO> filtrarLogsPdf(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return logActividadRepository.filtrarLogsPDF(
                fechaInicio,
                fechaFin
        ).stream().map(Mapper::toDTO).toList();
    }

    /**
     * Este es el metodo principal que genera un PDF con los logs de auditoría filtrados por un rango de fechas.
     * @param fechaInicio fecha inicial para el filtrado
     * @param fechaFin fecha final para el filtrado
     * @return
     */
    public byte[] exportarLogsPdf(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<LogDTO> logs = filtrarLogsPdf(fechaInicio, fechaFin);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);

            document.open();

            Font tituloFont = new Font(Font.HELVETICA, 16, Font.BOLD, Color.BLACK);
            Font subtituloFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
            Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
            Font bodyFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);

            Paragraph titulo = new Paragraph("Reporte de Logs de Auditoría", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10f);
            document.add(titulo);

            String filtros = "Desde: " + (fechaInicio != null ? fechaInicio : "sin filtro")
                    + " | Hasta: " + (fechaFin != null ? fechaFin : "sin filtro");

            Paragraph subtitulo = new Paragraph(filtros, subtituloFont);
            subtitulo.setSpacingAfter(15f);
            document.add(subtitulo);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.5f, 2f, 2f, 4f});

            agregarHeader(table, "Fecha/Hora", headerFont);
            agregarHeader(table, "Usuario", headerFont);
            agregarHeader(table, "Acción", headerFont);
            agregarHeader(table, "Detalle", headerFont);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            if (logs != null && !logs.isEmpty()) {
                for (LogDTO log : logs) {
                    String fecha = log.getFecha_actualizacion() != null
                            ? log.getFecha_actualizacion().format(formatter)
                            : "Sin fecha";

                    Usuario user = usuarioRepository.findById(log.getUsuario_id()).orElse(null);
                    String usuario = "Sin usuario";
                    if (user != null) {
                        usuario = user.getNombre();
                    }

                    String accion = log.getAccion() != null ? log.getAccion() : "Sin acción";
                    String detalle = log.getDetalle() != null ? log.getDetalle() : "Sin detalle";

                    table.addCell(new Phrase(fecha, bodyFont));
                    table.addCell(new Phrase(usuario, bodyFont));
                    table.addCell(new Phrase(accion, bodyFont));
                    table.addCell(new Phrase(detalle, bodyFont));
                }
            } else {
                PdfPCell emptyCell = new PdfPCell(new Phrase("No hay logs para exportar", bodyFont));
                emptyCell.setColspan(4);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                emptyCell.setPadding(10f);
                table.addCell(emptyCell);
            }

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de logs", e);
        }
    }

    /**
     * Este es un metodo auxiliar que se usa dentro de exportarLogsPdf para crear las celdas de encabezado de la tabla.
     * @param table Es la tabla del PDF donde se van a añadir las celdas de encabezado.
     * @param texto Es el contenido textual que aparecerá en la celda del encabezado.
     * @param font Es el estilo tipográfico que se aplicará al texto del encabezado.
     */
    private void agregarHeader(PdfPTable table, String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(Color.DARK_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8f);
        table.addCell(cell);
    }
}
