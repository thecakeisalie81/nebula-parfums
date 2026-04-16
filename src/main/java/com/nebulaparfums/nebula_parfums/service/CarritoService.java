package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.Carrito;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.ICarritoRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CarritoService implements ICarritoService {

    @Autowired
    private ICarritoRepository carritoRepository;

    @Autowired
    private IUsuarioService iUsuarioService;

    @Override
    public Carrito getCarritoById(Integer id) {
        Carrito carrito = carritoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro el carrito"));;
        return carrito;
    }

    @Override
    public void deleteCarritoById(Integer id) {
        if (carritoRepository.existsById(id)) {
            carritoRepository.deleteById(id);
        }
        else {
            throw new ResourceNotFoundException("No se encontro el Carrito");
        }
    }

    @Override
    public void editCarrito(Carrito carrito) {
        this.saveCarrito(carrito);
    }

    @Override
    public void saveCarrito(Carrito carrito) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = iUsuarioService.getUsuarioByEmail(username);

        carrito.setUsuario(usuario);

        carritoRepository.save(carrito);
    }

    @Override
    public Carrito obtenerCarritoPorEmail(String email) {
        Usuario usuario = iUsuarioService.getUsuarioByEmail(email);
        return usuario.getCarrito();
    }
}
