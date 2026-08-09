package com.nebulaparfums.nebula_parfums.mapper;

import com.nebulaparfums.nebula_parfums.dto.*;
import com.nebulaparfums.nebula_parfums.model.*;

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
}
