package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.model.Proveedor;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProveedorController {
    @Autowired
    private IProveedorService iProveedorService;

    @GetMapping("/proveedor/traer")
    public List<Proveedor> traerProveedores(){
        return iProveedorService.getProveedores();
    }

    @GetMapping("/proveedor/buscar")
    public Proveedor buscarProveedor(@RequestParam("id") Integer id){
        return iProveedorService.getProveedorById(id);
    }

    @PutMapping("/proveedor/editar")
    public String editarProveedor(@RequestBody Proveedor proveedor){
        iProveedorService.editProveedor(proveedor);
        return "Proveedor editado com sucesso";
    }

    @PostMapping("/proveedor/crear")
    public String crearProveedor(@RequestBody Proveedor proveedor){
        iProveedorService.saveProveedor(proveedor);
        return "Proveedor creado exitosamente";
    }

    @DeleteMapping("/proveedor/borrar")
    public String borrarProveedor(@RequestParam("id") Integer id){
        iProveedorService.deleteProveedor(id);
        return "Proveedor eliminado exitosamente";
    }
}
