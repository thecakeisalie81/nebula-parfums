package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.CarritoDetalleDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.model.CarritoDetalle;
import com.nebulaparfums.nebula_parfums.model.Producto;
import com.nebulaparfums.nebula_parfums.repository.ICarritoDetalleRepository;
import com.nebulaparfums.nebula_parfums.repository.ICarritoRepository;
import com.nebulaparfums.nebula_parfums.repository.IProductoRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoDetalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarritoDetalleService implements ICarritoDetalleService {

    private final ICarritoDetalleRepository  carritoDetalleRepository;
    private final ICarritoRepository iCarritoRepository;
    private final IProductoRepository iProductoRepository;

    @Override
    public CarritoDetalle getCarritoDetalleById(Integer id) {
        return carritoDetalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto en el carrito"));
    }

    /**
     * Guarda un nuevo carrito detalle en la base de datos
     * @param carritoDetalle DTO con los detalles del nuevo elemento
     * @return CarritoDetalleDTO con los datos recién guardados
     */
    @Override
    public CarritoDetalleDTO saveCarritoDetalle(CarritoDetalleDTO carritoDetalle) {

        CarritoDetalle carrito = CarritoDetalle.builder()
                .cantidad(carritoDetalle.getCantidad())
                .precio(carritoDetalle.getPrecio())
                .carrito(iCarritoRepository.findById(carritoDetalle.getId_carrito())
                        .orElseThrow(() -> new ResourceNotFoundException("No se encontró el carrito")))
                .producto(iProductoRepository.findById(carritoDetalle.getId_producto())
                        .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto")))
                .build();

        return Mapper.toDTO(carritoDetalleRepository.save(carrito));
    }

    /**
     * Borra físicamente un elemento del carrito
     * @param id id del elemento a eliminar
     */
    @Override
    public void deleteCarritoDetalleById(Integer id) {
        carritoDetalleRepository.deleteById(id);
    }

    /**
     * Edita los detalles de un elemento del carrito
     * @param id id del elemento a modificar
     * @param Detalle DTO con los datos nuevos
     * @return CarritoDetalleDTO con los datos que se actualizaron
     */
    @Override
    public CarritoDetalleDTO editCarritoDetalle(Integer id, CarritoDetalleDTO Detalle) {

        CarritoDetalle carritoDetalle = getCarritoDetalleById(id);
        Producto producto = iProductoRepository.findById(Detalle.getId_producto())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto"));

        carritoDetalle.setCantidad(Detalle.getCantidad());
        carritoDetalle.setPrecio(producto.getPrecio() * carritoDetalle.getCantidad());

        if (carritoDetalle.getCantidad() <= 0){
            deleteCarritoDetalleById(id);

        }
        return Mapper.toDTO(carritoDetalleRepository.save(carritoDetalle));
    }
}
