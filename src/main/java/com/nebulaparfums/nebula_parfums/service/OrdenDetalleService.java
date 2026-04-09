package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.OrdendetalleDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.Orden;
import com.nebulaparfums.nebula_parfums.model.OrdenDetalle;
import com.nebulaparfums.nebula_parfums.model.Producto;
import com.nebulaparfums.nebula_parfums.repository.IOrdenDetalleRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenDetalleService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IOrdenService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        OrdenDetalle ordenDetalle = ordenDetalleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro el producto en la orden"));;
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
}
