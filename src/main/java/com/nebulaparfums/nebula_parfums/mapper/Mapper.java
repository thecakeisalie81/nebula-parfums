package com.nebulaparfums.nebula_parfums.mapper;

import com.nebulaparfums.nebula_parfums.dto.ProductoDTO;
import com.nebulaparfums.nebula_parfums.model.Producto;

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
}
