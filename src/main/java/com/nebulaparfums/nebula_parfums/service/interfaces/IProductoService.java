package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductoService {
    public Page<Producto> getProductos(Pageable pageable);
    Page<Producto> getProductosFiltrados(
            Pageable pageable,
            String nombre,
            Integer idCategoria,
            Integer idProveedor,
            String estadoStock,
            Integer precioMinimo,
            Integer precioMaximo
    );
    public Page<Producto> getProductosBusqueda(Pageable pageable,String nombre);
    public List<Producto> getProductosCategoria(Integer categoria);
    public List<Producto> getProductosReporte();
    public Producto getProductoById(Integer id);
    public void editProducto(Producto producto);
    public void deleteProducto(Integer id);
    public void saveProducto(Producto producto);
    public Integer getProductosLowStock();
    public Integer getProductosSinStock();
    public Integer getTotalStock();
    public Integer getProductosConStock();
    public List<Producto> get4ProductosBajoStock();

}
