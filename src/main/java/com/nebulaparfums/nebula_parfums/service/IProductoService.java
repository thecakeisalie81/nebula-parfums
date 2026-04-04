package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.Producto;

import java.util.List;

public interface IProductoService {
    public List<Producto> getProductos();
    public Producto getProductoById(Integer id);
    public void editProducto(Producto producto);
    public void deleteProducto(Integer id);
    public void saveProducto(Producto producto);
}
