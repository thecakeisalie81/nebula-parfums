package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.QuantityBelowZeroException;
import com.nebulaparfums.nebula_parfums.model.LogActividad;
import com.nebulaparfums.nebula_parfums.model.MovimientoInventario;
import com.nebulaparfums.nebula_parfums.model.Producto;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.IMovimientoInventarioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ILogActividadService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IMovimientoInventarioService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProductoService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoInventarioService implements IMovimientoInventarioService {

    @Autowired
    private IMovimientoInventarioRepository iMovimientoInventarioRepository;
    @Autowired
    private IProductoService iProductoService;
    @Autowired
    protected IUsuarioService iUsuarioService;
    @Autowired
    private ILogActividadService iLogActividadService;

    @Override
    public List<MovimientoInventario> getMovimientoInventario() {
        List<MovimientoInventario> listaMovimientos =  iMovimientoInventarioRepository.findAll();
        return listaMovimientos;
    }

    @Override
    public void saveMovimientoInventario(MovimientoInventario movimientoInventario) {
        iMovimientoInventarioRepository.save(movimientoInventario);
    }

    @Transactional
    public String registrarSalida(Integer productoId, int cantidad){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = iUsuarioService.getUsuarioByEmail(username);

        Producto producto = iProductoService.getProductoById(productoId);

        if (cantidad <= 0) {
            throw new RuntimeException("Cantidad inválida");
        }

        if (producto.getStock_actual()<cantidad){
            throw new QuantityBelowZeroException("No hay cantidad suficiente de el producto: " + producto.getNombre()+
                    "Solo quedan "+producto.getStock_actual());
        }

        producto.setStock_actual(producto.getStock_actual()-cantidad);
        iProductoService.saveProducto(producto);

        MovimientoInventario movimientoInventario = new MovimientoInventario();
        movimientoInventario.setProducto(producto);
        movimientoInventario.setCantidad(cantidad);
        movimientoInventario.setTipo_movimiento("SALIDA");
        movimientoInventario.setFecha_movimiento(LocalDateTime.now());
        movimientoInventario.setUsuario(usuario);

        saveMovimientoInventario(movimientoInventario);

        LogActividad logActividad = new LogActividad();
        logActividad.setUsuario(usuario);
        logActividad.setAccion("Modificacion de inventario");
        logActividad.setDetalle("Usuario " + usuario.getNombre() + " realizo una salida de producto " + producto.getNombre());
        logActividad.setFecha_actualizacion(LocalDateTime.now());

        iLogActividadService.saveLogActividad(logActividad);

        return "Se registro el movimiento correctamente";
    }

    @Transactional
    public String registrarEntrada(Integer productoId, int cantidad){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = iUsuarioService.getUsuarioByEmail(username);

        Producto producto = iProductoService.getProductoById(productoId);

        if (cantidad <= 0) {
            throw new RuntimeException("Cantidad inválida");
        }

        producto.setStock_actual(producto.getStock_actual()+cantidad);
        iProductoService.saveProducto(producto);

        MovimientoInventario movimientoInventario = new MovimientoInventario();
        movimientoInventario.setProducto(producto);
        movimientoInventario.setCantidad(cantidad);
        movimientoInventario.setTipo_movimiento("ENTRADA");
        movimientoInventario.setFecha_movimiento(LocalDateTime.now());
        movimientoInventario.setUsuario(usuario);

        saveMovimientoInventario(movimientoInventario);

        LogActividad logActividad = new LogActividad();
        logActividad.setUsuario(usuario);
        logActividad.setAccion("Modificacion de inventario");
        logActividad.setDetalle("Usuario " + usuario.getNombre() + " realizo una entrada de producto "+ producto.getNombre());
        logActividad.setFecha_actualizacion(LocalDateTime.now());

        iLogActividadService.saveLogActividad(logActividad);

        return "Se registro el movimiento correctamente";
    }
}
