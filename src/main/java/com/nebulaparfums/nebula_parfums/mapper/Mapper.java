package com.nebulaparfums.nebula_parfums.mapper;

import com.nebulaparfums.nebula_parfums.dto.*;
import com.nebulaparfums.nebula_parfums.model.*;

import java.util.List;

public class Mapper {

    /**
     * Convierte un Producto en su representación DTO.
     *
     * @param producto entidad Producto que viene de la base de datos
     * @return ProductoDTO con los datos mapeados
     */
    public static ProductoDTO toDTO(Producto producto) {
        if (producto == null) {
            return null;
        }

        return ProductoDTO.builder()
                .id(producto.getId_producto())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock_actual(producto.getStock_actual())
                .stock_minimo(producto.getStock_minimo())
                .categoria(producto.getCategoria().getId_categoria())
                .proveedor(producto.getProveedor().getId_proveedor())
                .imagen(producto.getImagen())
                .build();
    }

    /**
     * Convierte un Proveedor en su representación DTO
     *
     * @param proveedor entidad proveedor que viene de la base de datos
     * @return ProveedorDTO con los datos mapeados
     */
    public static ProveedorDTO toDTO(Proveedor proveedor) {
        if (proveedor == null) {
            return null;
        }

        return ProveedorDTO.builder()
                .nombre(proveedor.getNombre())
                .contacto(proveedor.getContacto())
                .telefono(proveedor.getTelefono())
                .email(proveedor.getEmail())
                .build();
    }

    /**
     * Convierte un usuario en su representación DTO
     *
     * @param usuario entidad Usuario que viene de la base de datos
     * @return UsuarioDTO con los datos mapeados
     */
    public static UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return UsuarioDTO.builder()
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .estado(usuario.getEstado())
                .rol(usuario.getRol())
                .fecha_creacion(usuario.getFecha_creacion())
                .build();
    }

    /**
     * Convierte una Categoria en su representación DTO
     *
     * @param categoria entidad categoria que viene de la base de datos
     * @return CategoriaDTO con los datos mapeados
     */
    public static CategoriaDTO toDTO(Categoria categoria) {
        if (categoria == null) {
            return null;
        }

        return CategoriaDTO.builder()
                .id(categoria.getId_categoria())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .build();
    }

    /**
     * Convierte una Direccion en su representación DTO
     *
     * @param direccion entidad DireccionEnvio que viene de la base de datos
     * @return DireccionDTO con los datos mapeados
     */
    public static DireccionDTO toDTO(DireccionEnvio direccion){
        if (direccion == null) {
            return null;
        }

        return DireccionDTO.builder()
                .id_direccion(direccion.getId_direccion())
                .direccion(direccion.getDireccion())
                .ciudad(direccion.getCiudad())
                .provincia(direccion.getProvincia())
                .codigo_postal(direccion.getCodigo_postal())
                .telefono(direccion.getTelefono())
                .build();
    }

    /**
     * Convierte un Log en su representación DTO
     *
     * @param log entidad LogActividad que viene de la base de datos
     * @return LogDTO con los datos mapeados
     */
    public static LogDTO toDTO(LogActividad log) {
        if (log == null) {
            return null;
        }

        return LogDTO.builder()
                .id_log(log.getId_log())
                .accion(log.getAccion())
                .detalle(log.getDetalle())
                .fecha_actualizacion(log.getFecha_actualizacion())
                .usuario_id(log.getUsuario().getId_usuario())
                .build();
    }

    /**
     * Convierte un movimiento de inventario en su representación DTO
     *
     * @param movimiento entidad MovimientoInventario que viene de la base de datos
     * @return MovimientoDTO con los datos mapeados
     */
    public static MovimientoDTO toDTO(MovimientoInventario movimiento) {
        if (movimiento == null) {
            return null;
        }

        return MovimientoDTO.builder()
                .tipo_movimiento(movimiento.getTipo_movimiento())
                .cantidad(movimiento.getCantidad())
                .fecha_movimiento(movimiento.getFecha_movimiento())
                .id_usuario(movimiento.getUsuario().getId_usuario())
                .id_producto(movimiento.getProducto().getId_producto())
                .build();
    }


    public static CarritoDTO toDTO(Carrito carrito) {
        if (carrito == null) {
            return null;
        }

        List<CarritoDetalleDTO> listaDetalles = carrito.getListaCarritoDetalles().stream().map( det ->
                CarritoDetalleDTO.builder()
                        .id_carrito_detalle(det.getId_carrito_detalle())
                        .cantidad(det.getCantidad())
                        .precio(det.getPrecio())
                        .id_producto(det.getProducto().getId_producto())
                        .id_carrito(det.getCarrito().getId_carrito())
                        .build()
        ).toList();



        return CarritoDTO.builder()
                .id_carrito(carrito.getId_carrito())
                .fecha_actualizacion(carrito.getFecha_actualizacion())
                .listaCarritoDetalles(listaDetalles)
                .id_usuario(carrito.getUsuario().getId_usuario())
                .total(listaDetalles.stream().map(CarritoDetalleDTO::getPrecio)
                        .reduce(0.0, Double::sum))
                .build();
    }

    /**
     * Convierte un detalle del carrito en su representación DTO
     *
     * @param carritoDetalle entidad CarritoDetalle que viene de la base de datos
     * @return CarritoDetalleDTO con los datos mapeados
     */
    public static CarritoDetalleDTO toDTO(CarritoDetalle carritoDetalle) {
        if (carritoDetalle == null) {
            return null;
        }

        return CarritoDetalleDTO.builder()
                .id_carrito_detalle(carritoDetalle.getId_carrito_detalle())
                .cantidad(carritoDetalle.getCantidad())
                .precio(carritoDetalle.getPrecio())
                .id_producto(carritoDetalle.getProducto().getId_producto())
                .id_carrito(carritoDetalle.getCarrito().getId_carrito())
                .build();
    }

    /**
     * Convierte un detalle de una orden en su representación DTO
     *
     * @param ordenDetalle entidad OrdenDetalle que viene de la base de datos
     * @return OrdenDetalleDTO con los datos mapeados
     */
    public static OrdenDetalleDTO toDTO(OrdenDetalle ordenDetalle) {
        if (ordenDetalle == null) {
            return null;
        }

        return OrdenDetalleDTO.builder()
                .id_orden_detalle(ordenDetalle.getId_orden_detalle())
                .cantidad(ordenDetalle.getCantidad())
                .precio(ordenDetalle.getPrecio())
                .id_orden(ordenDetalle.getOrden().getId_orden())
                .id_producto(ordenDetalle.getProducto().getId_producto())
                .build();
    }
}
