package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.Proveedor;
import com.nebulaparfums.nebula_parfums.repository.IProveedorRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorService implements IProveedorService {
    @Autowired
    private IProveedorRepository iProveedorRepository;


    @Override
    public Page<Proveedor> getProveedores(Pageable pageable) {
        return iProveedorRepository.findAll(pageable);
    }

    @Override
    public List<Proveedor> getAllProveedores() {
        return iProveedorRepository.findAll();
    }

    @Override
    public Proveedor getProveedorById(Integer id) {
        Proveedor proveedor = iProveedorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro el proveedor"));
        return proveedor;
    }

    @Override
    public void saveProveedor(Proveedor proveedor) {
        iProveedorRepository.save(proveedor);
    }

    @Override
    public void deleteProveedor(Integer id) {
        if (iProveedorRepository.existsById(id)) {
            iProveedorRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se encontro el proveedor");
        }
    }

    @Override
    public void editProveedor(Proveedor proveedor) {
        this.saveProveedor(proveedor);
    }

    @Override
    public int totalProveedores() {
        return iProveedorRepository.findAll().size();
    }
}
