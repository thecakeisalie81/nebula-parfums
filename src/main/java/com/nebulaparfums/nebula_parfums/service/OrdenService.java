package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.CreateOrdenDTO;
import com.nebulaparfums.nebula_parfums.dto.OrdenDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductosPendientesProceso;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.*;
import com.nebulaparfums.nebula_parfums.repository.IOrdenRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenService implements IOrdenService {
    @Autowired
    private IOrdenRepository ordenRepository;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IMovimientoInventarioService  movimientoInventarioService;

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IDireccionEnvioService direccionEnvioService;

    @Autowired
    private ICarritoDetalleService carritoDetalleService;

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
    public List<Orden> getOrdenesUsuario(Integer id) {
        return ordenRepository.getOrdenesUsuario(id);
    }

    @Override
    public void saveOrden(Orden orden) {
        ordenRepository.save(orden);
    }


    @Override
    public void crearOrden(CreateOrdenDTO dto) {

        Usuario usuario = usuarioService.getUsuarioById(dto.getId_usuario());
        DireccionEnvio  direccionEnvio = direccionEnvioService.getDireccionEnvioById(dto.getId_direccion());
        Carrito carrito = usuario.getCarrito();
        Orden orden = new Orden();
        orden.setFecha_creacion(LocalDateTime.now());
        orden.setEstado("PENDIENTE");
        orden.setUsuario(usuario);
        orden.setDireccion(direccionEnvio);
        orden.setTotal(dto.getTotal());

        List<OrdenDetalle>  ordenDetalles = new ArrayList<>();

        for (CarritoDetalle detalle : carrito.getListaCarritoDetalles()){
            OrdenDetalle ordenDetalle = new OrdenDetalle();
            Producto producto = detalle.getProducto();
            movimientoInventarioService.registrarSalida(producto.getId_producto(), detalle.getCantidad());
            ordenDetalle.setOrden(orden);
            ordenDetalle.setPrecio(detalle.getPrecio());
            ordenDetalle.setCantidad(detalle.getCantidad());
            ordenDetalle.setProducto(detalle.getProducto());
            ordenDetalles.add(ordenDetalle);
            carritoDetalleService.deleteCarritoDetalleById(detalle.getId_carrito_detalle());
            productoService.saveProducto(producto);
        }

        orden.setListaOrdenDetalle(ordenDetalles);
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
    public Double sumaTotalesMes(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ordenRepository.sumaTotalesMesActual(fechaInicio, fechaFin);
    }

    @Override
    public ProductosPendientesProceso getPendientesProcesos() {
        ProductosPendientesProceso cuenta = new ProductosPendientesProceso();
        cuenta.setPendientes(ordenRepository.countByEstado("PENDIENTE"));
        cuenta.setProceso(ordenRepository.countByEstado("EN PROCESO"));
        return cuenta;
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

                if (orden.getEstado().equals("CANCELADO")) {
                    for (OrdenDetalle ordenDetalle : order.getListaOrdenDetalle()) {
                        Producto producto = ordenDetalle.getProducto();
                        movimientoInventarioService.registrarEntrada(producto.getId_producto(), ordenDetalle.getCantidad());
                    }
                }

                ordenRepository.save(order);
            }
        }
    }

}
