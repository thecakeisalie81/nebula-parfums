package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.CarritoDTO;
import com.nebulaparfums.nebula_parfums.dto.CarritoDetalleDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.model.Carrito;
import com.nebulaparfums.nebula_parfums.model.CarritoDetalle;
import com.nebulaparfums.nebula_parfums.repository.ICarritoRepository;
import com.nebulaparfums.nebula_parfums.repository.IUsuarioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoDetalleService;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Servicio para gestionar los carritos.
 * Provee operaciones de consulta, guardado, eliminación, edición y conversión a DTO.
 */
@Service
@RequiredArgsConstructor
public class CarritoService implements ICarritoService {

    private final ICarritoRepository carritoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final ICarritoService carritoService;
    private final ICarritoDetalleService carritoDetalleService;


    @Override
    public Carrito getCarritoById(Integer id) {
        return carritoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el carrito"));
    }

    /**
     * Elimina los elementos de un carrito
     * @param id id del carrito
     */
    @Override
    public void deleteCarritoById(Integer id) {
        Carrito carrito = getCarritoById(id);
        carrito.setFecha_actualizacion(LocalDateTime.now());
        for (CarritoDetalle detalle : carrito.getListaCarritoDetalles()){
            carritoService.deleteCarritoById(detalle.getId_carrito_detalle());
        }
        carrito.setListaCarritoDetalles(null);
        carritoRepository.save(carrito);
    }

    /**
     * Edita los detalles de un carrito
     * @param carrito DTO con los nuevos datos del carrito
     * @param id id del carrito a editar
     */
    @Override
    public CarritoDTO editCarrito(Integer id, CarritoDTO carrito) {
        Carrito cart = carritoRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No carrito el usuario"));

        List<CarritoDetalle> listDetalles = new ArrayList<>();
        for (CarritoDetalleDTO detalle : carrito.getListaCarritoDetalles()){
            CarritoDetalle det = carritoDetalleService.getCarritoDetalleById(detalle.getId_carrito_detalle());
            listDetalles.add(det);
        }

        cart.setListaCarritoDetalles(listDetalles);
        cart.setFecha_actualizacion(LocalDateTime.now());

        return Mapper.toDTO(carritoRepository.save(cart));
    }

    /**
     * Guarda un nuevo carrito en la base de datos
     * @param carrito DTO con los datos del nuevo carrito
     */
    @Override
    public CarritoDTO saveCarrito(CarritoDTO carrito) {
        Carrito cart = Carrito.builder()
                .fecha_actualizacion(LocalDateTime.now())
                .usuario(usuarioRepository.findById(carrito.getId_usuario())
                        .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario")))
                .build();

        return Mapper.toDTO(carritoRepository.save(cart));
    }
}
