package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.Producto;
import com.nebulaparfums.nebula_parfums.repository.IProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService implements IProductoService{
    @Autowired
    private IProductoRepository iProductoRepository;

    @Override
    public List<Producto> getProductos() {
        List<Producto> productos = iProductoRepository.findAll();
        return productos;
    }

    @Override
    public Producto getProductoById(Integer id) {
        Producto producto = iProductoRepository.findById(id).orElse(null);
        return producto;
    }

    @Override
    public void editProducto(Producto producto) {
        iProductoRepository.save(producto);
    }

    @Override
    public void deleteProducto(Integer id) {
        iProductoRepository.deleteById(id);
    }

    @Override
    public void saveProducto(Producto producto) {
        iProductoRepository.save(producto);
    }
}
