package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.model.LogActividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ILogActividadService {
    public void saveLogActividad(LogActividad logActividad);
    public void saveLogout( String correo);
    public List<LogActividad> getLogsActividad();
    public Page<LogActividad> filtrarLogs(Pageable pageable,
                                               @Param("accion") String accion,
                                               @Param("fechaInicio") LocalDate fechaInicio,
                                               @Param("fechaFin") LocalDate fechaFin);

    public List<LogActividad> filtrarLogsPdf(@Param("fechaInicio") LocalDate fechaInicio,
                                             @Param("fechaFin") LocalDate fechaFin);

    byte[] exportarLogsPdf(LocalDate fechaInicio, LocalDate fechaFin);
}
