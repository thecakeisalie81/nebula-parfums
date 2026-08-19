package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.ProductoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductoService {
    public List<ProductoDTO> getProductos();
    public Page<ProductoDTO> getProductosFiltrados(
            Pageable pageable,
            String nombre,
            Integer idCategoria,
            Integer idProveedor,
            String estadoStock,
            Integer precioMinimo,
            Integer precioMaximo,
            Integer disponible
    );
    public ProductoDTO getProductoById(Integer id);
    public ProductoDTO editProducto(Integer id,ProductoDTO producto);
    public void deleteProducto(Integer id);
    public ProductoDTO saveProducto(ProductoDTO producto);
    public Integer getProductosLowStock();
    public Integer getProductosSinStock();
    public Integer getTotalStock();
    public Page<ProductoDTO> getProductosBajoStock(Pageable pageable);
}
