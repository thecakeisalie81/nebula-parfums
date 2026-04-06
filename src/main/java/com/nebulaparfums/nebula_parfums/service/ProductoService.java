package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.QuantityBelowZeroException;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
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
        return iProductoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro el producto"));
    }

    @Override
    public void editProducto(Producto producto) {
        if (producto.getStock_actual() < 0){
            throw new QuantityBelowZeroException("No hay suficientes unidades   ");
        }

        this.saveProducto(producto);
    }

    @Override
    public void deleteProducto(Integer id) {
        if (iProductoRepository.existsById(id)){
            iProductoRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se encontro el producto");
        }

    }

    @Override
    public void saveProducto(Producto producto) {
        iProductoRepository.save(producto);
    }
}
