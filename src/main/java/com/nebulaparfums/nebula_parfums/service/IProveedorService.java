package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.model.Proveedor;

import java.util.List;

public interface IProveedorService {
    public List<Proveedor> getProveedores();
    public Proveedor getProveedorById(Integer id);
    public void saveProveedor(Proveedor proveedor);
    public void deleteProveedor(Integer id);
    public void editProveedor(Proveedor proveedor);
}
