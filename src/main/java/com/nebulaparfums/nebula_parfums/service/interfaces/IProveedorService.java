package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.ProveedorDTO;
import com.nebulaparfums.nebula_parfums.model.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProveedorService {
    Page<ProveedorDTO> getProveedores(Pageable pageable);
    List<ProveedorDTO> getAllProveedores();
    ProveedorDTO getProveedorById(Integer id);
    ProveedorDTO saveProveedor(ProveedorDTO proveedor, String email);
    void deleteProveedor(Integer id);
    ProveedorDTO editProveedor(ProveedorDTO proveedor, String email, Integer id);
    int totalProveedores();
}
