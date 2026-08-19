package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.ProductoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductoService {
    List<ProductoDTO> getProductos();
    Page<ProductoDTO> getProductosFiltrados(
            Pageable pageable,
            String nombre,
            Integer idCategoria,
            Integer idProveedor,
            String estadoStock,
            Integer precioMinimo,
            Integer precioMaximo,
            Integer disponible
    );
    ProductoDTO getProductoById(Integer id);
    ProductoDTO editProducto(Integer id,ProductoDTO producto);
    void deleteProducto(Integer id);
    ProductoDTO saveProducto(ProductoDTO producto);
    Integer getProductosLowStock();
    Integer getProductosSinStock();
    Integer getTotalStock();
    Page<ProductoDTO> getProductosBajoStock(Pageable pageable);
}
