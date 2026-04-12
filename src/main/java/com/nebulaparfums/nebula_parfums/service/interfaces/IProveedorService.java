package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.model.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProveedorService {
    public Page<Proveedor> getProveedores(Pageable pageable);
    public List<Proveedor> getAllProveedores();
    public Proveedor getProveedorById(Integer id);
    public void saveProveedor(Proveedor proveedor);
    public void deleteProveedor(Integer id);
    public void editProveedor(Proveedor proveedor);
    public int totalProveedores();
}
