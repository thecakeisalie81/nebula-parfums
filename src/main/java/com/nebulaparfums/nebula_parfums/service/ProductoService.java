package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.QuantityBelowZeroException;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.Producto;
import com.nebulaparfums.nebula_parfums.repository.IProductoRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService implements IProductoService {
    @Autowired
    private IProductoRepository iProductoRepository;

    @Override
    public Page<Producto> getProductos(Pageable pageable) {
        return iProductoRepository.findAll(pageable);
    }

    @Override
    public Page<Producto> getProductosFiltrados(
            Pageable pageable,
            String nombre,
            Integer idCategoria,
            Integer idProveedor,
            String estadoStock
    ) {
        if (nombre != null && nombre.isBlank()) {
            nombre = null;
        }

        if (estadoStock != null && estadoStock.isBlank()) {
            estadoStock = null;
        }

        return iProductoRepository.filtrarProductos(
                pageable,
                nombre,
                idCategoria,
                idProveedor,
                estadoStock
        );
    }


    @Override
    public Page<Producto> getProductosBusqueda(Pageable pageable,String nombre) {
        return iProductoRepository.findByNombre(pageable, nombre);
    }

    @Override
    public List<Producto> getProductosCategoria(Integer categoria) {
        List<Producto> resultados = iProductoRepository.findByCategoriaId_categoria(categoria);
        return resultados;
    }

    @Override
    public List<Producto> getProductosReporte() {
        return iProductoRepository.findAll();
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

    @Override
    public Integer getProductosLowStock() {
        return iProductoRepository.countProductosConStockBajo();
    }

    @Override
    public Integer getProductosSinStock() {
        return iProductoRepository.countProductosSinStock();
    }

    @Override
    public Integer getTotalStock() {
        return Math.toIntExact(iProductoRepository.count());
    }

    @Override
    public Integer getProductosConStock() {
        return iProductoRepository.countProductosConStock();
    }

    @Override
    public List<Producto> get4ProductosBajoStock() {
        Pageable limiteCuatro = PageRequest.of(0, 4);
        return iProductoRepository.findProductosConStockBajo(limiteCuatro);
    }

}
