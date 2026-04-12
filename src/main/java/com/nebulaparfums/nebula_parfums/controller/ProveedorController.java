package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.Proveedor;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProveedorController {
    @Autowired
    private IProveedorService iProveedorService;

    @GetMapping("/proveedor/traer")
    public Page<Proveedor> traerProveedores(Pageable pageable){
        return iProveedorService.getProveedores(pageable);
    }

    @GetMapping("/proveedores/traer")
    public List<Proveedor> traerTodosProveedores(){
        return iProveedorService.getAllProveedores();
    }

    @GetMapping("proveedor/total")
    public int totalProveedores(){
        return iProveedorService.totalProveedores();
    }

    @GetMapping("/proveedor/buscar")
    public Proveedor buscarProveedor(@RequestParam("id") Integer id){
        return iProveedorService.getProveedorById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @PutMapping("/proveedor/editar")
    public String editarProveedor(@RequestBody Proveedor proveedor){
        iProveedorService.editProveedor(proveedor);
        return "Proveedor editado com sucesso";
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @PostMapping("/proveedor/crear")
    public String crearProveedor(@RequestBody Proveedor proveedor){
        iProveedorService.saveProveedor(proveedor);
        return "Proveedor creado exitosamente";
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @DeleteMapping("/proveedor/borrar")
    public String borrarProveedor(@RequestParam("id") Integer id){
        iProveedorService.deleteProveedor(id);
        return "Proveedor eliminado exitosamente";
    }
}
