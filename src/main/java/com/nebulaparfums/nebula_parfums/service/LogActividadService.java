package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.LogActividad;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.ILogActividadRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ILogActividadService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
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

@Service
public class LogActividadService implements ILogActividadService {
    @Autowired
    private ILogActividadRepository logActividadRepository;

    @Autowired
    private IUsuarioService usuarioService;

    @Override
    public void saveLogActividad(LogActividad logActividad) {
        logActividadRepository.save(logActividad);
    }

    @Override
    public void saveLogout(String logActividad) {

        Usuario user = usuarioService.getUsuarioByEmail(logActividad);

        LogActividad log =  new LogActividad();
        log.setFecha_actualizacion(LocalDateTime.now());
        log.setAccion("Logout");
        log.setDetalle("El usuario "+ user.getNombre() + " Cerro sesion");
        log.setUsuario(user);
        logActividadRepository.save(log);
    }

    @Override
    public List<LogActividad> getLogsActividad() {
        List<LogActividad> logsActividad = logActividadRepository.findAll();
        return logsActividad;
    }

    @Override
    public Page<LogActividad> filtrarLogs(Pageable pageable, String accion, LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = null;
        LocalDateTime fin = null;

        if (fechaInicio != null) {
            inicio = fechaInicio.atStartOfDay();
        }

        if (fechaFin != null) {
            fin = fechaFin.atTime(LocalTime.MAX);
        }
        if (accion != null && accion.isBlank()) {
            accion = null;
        }

        return logActividadRepository.filtrarLogActividades(
                pageable,
                accion,
                inicio,
                fin
        );
    }

    @Override
    public List<LogActividad> filtrarLogsPdf(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = null;
        LocalDateTime fin = null;

        if (fechaInicio != null) {
            inicio = fechaInicio.atStartOfDay();
        }

        if (fechaFin != null) {
            fin = fechaFin.atTime(LocalTime.MAX);
        }

        return logActividadRepository.filtrarLogsPDF(
                inicio,
                fin
        );
    }

    public byte[] exportarLogsPdf(LocalDate fechaInicio, LocalDate fechaFin) {
        List<LogActividad> logs = filtrarLogsPdf(fechaInicio, fechaFin);

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
                for (LogActividad log : logs) {
                    String fecha = log.getFecha_actualizacion() != null
                            ? log.getFecha_actualizacion().format(formatter)
                            : "Sin fecha";

                    String usuario = (log.getUsuario() != null && log.getUsuario().getNombre() != null)
                            ? log.getUsuario().getNombre()
                            : "Sin usuario";

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

    private void agregarHeader(PdfPTable table, String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(Color.DARK_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8f);
        table.addCell(cell);
    }
}
