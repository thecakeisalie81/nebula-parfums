package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.OrdenDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;
import com.nebulaparfums.nebula_parfums.model.Orden;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.IOrdenRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IDireccionEnvioService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenService implements IOrdenService {
    @Autowired
    private IOrdenRepository ordenRepository;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IDireccionEnvioService direccionEnvioService;

    @Override
    public List<Orden> getOrdenes() {
        List<Orden> ordenes = ordenRepository.findAll();
        return ordenes;
    }

    @Override
    public Orden getOrdenById(Integer ordenId) {
        Orden orden = ordenRepository.findById(ordenId).orElseThrow(() -> new ResourceNotFoundException("No se encontro la orden"));
        return orden;
    }

    @Override
    public void saveOrden(Orden orden) {
        ordenRepository.save(orden);
    }

    @Override
    public void crearOrden(OrdenDTO dto) {

        Usuario usuario = usuarioService.getUsuarioById(dto.getId_cliente());
        DireccionEnvio  direccionEnvio = direccionEnvioService.getDireccionEnvioById(dto.getId_direccion());
        Orden orden = new Orden();
        orden.setFecha_creacion(LocalDateTime.now());
        orden.setEstado("PENDIENTE");
        orden.setUsuario(usuario);
        orden.setDireccion(direccionEnvio);
        ordenRepository.save(orden);
    }

    @Override
    public List<Orden> getUltimasOrdenesPendiente() {
        Pageable  pageable = PageRequest.of(0, 5, Sort.by("fecha_creacion").descending());
        return ordenRepository.ultimasOrdenesPendiente(pageable);
    }

    @Override
    public Page<Orden> filtrarOrden(Pageable pageable, String estado, LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = null;
        LocalDateTime fin = null;

        if (fechaInicio != null) {
            inicio = fechaInicio.atStartOfDay();
        }

        if (fechaFin != null) {
            fin = fechaFin.atTime(LocalTime.MAX);
        }

        if (estado != null && estado.isBlank()) {
            estado = null;
        }

        return ordenRepository.filtrarOrden(pageable, estado, inicio, fin);
    }

    @Override
    public void deleteOrden(Integer ordenId) {
        if (ordenRepository.existsById(ordenId)) {
            ordenRepository.deleteById(ordenId);
        }else {
            throw new ResourceNotFoundException("No se encontro la orden");
        }
    }

    @Override
    public void editOrden(OrdenDTO orden) {
        if (orden.getId_orden() != null) {
            Optional<Orden> optionalOrder = ordenRepository.findById(orden.getId_orden());
            if (optionalOrder.isPresent()) {
                Orden order = optionalOrder.get();
                order.setEstado(orden.getEstado());
                ordenRepository.save(order);
            }
        }
    }

}
