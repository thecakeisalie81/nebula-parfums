package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.model.Producto;

import java.util.List;

public interface IProductoService {
    public List<Producto> getProductos();
    public List<Producto> getProductosBusqueda(String nombre);
    public List<Producto> getProductosCategoria(Integer categoria);
    public Producto getProductoById(Integer id);
    public void editProducto(Producto producto);
    public void deleteProducto(Integer id);
    public void saveProducto(Producto producto);
}
