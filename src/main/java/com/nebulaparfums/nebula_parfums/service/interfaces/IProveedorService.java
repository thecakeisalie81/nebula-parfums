package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.ProveedorDTO;
import com.nebulaparfums.nebula_parfums.model.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProveedorService {
    public Page<ProveedorDTO> getProveedores(Pageable pageable);
    public List<ProveedorDTO> getAllProveedores();
    public ProveedorDTO getProveedorById(Integer id);
    public ProveedorDTO saveProveedor(ProveedorDTO proveedor, String email);
    public void deleteProveedor(Integer id);
    public ProveedorDTO editProveedor(ProveedorDTO proveedor, String email, Integer id);
    public int totalProveedores();
}
