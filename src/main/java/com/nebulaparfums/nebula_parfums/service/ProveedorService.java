package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.Proveedor;
import com.nebulaparfums.nebula_parfums.repository.IProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorService implements IProveedorService{
    @Autowired
    private IProveedorRepository iProveedorRepository;


    @Override
    public List<Proveedor> getProveedores() {
        List<Proveedor> proveedores = iProveedorRepository.findAll();
        return proveedores;
    }

    @Override
    public Proveedor getProveedorById(Integer id) {
        Proveedor proveedor = iProveedorRepository.findById(id).get();
        return proveedor;
    }

    @Override
    public void saveProveedor(Proveedor proveedor) {
        iProveedorRepository.save(proveedor);
    }

    @Override
    public void deleteProveedor(Integer id) {
        iProveedorRepository.deleteById(id);
    }

    @Override
    public void editProveedor(Proveedor proveedor) {
        this.saveProveedor(proveedor);
    }
}
