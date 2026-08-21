package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.ProveedorDTO;
import com.nebulaparfums.nebula_parfums.dto.UsuarioDTO;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/proveedor")
public class ProveedorController {
    private final IProveedorService iProveedorService;

    @GetMapping
    public ResponseEntity<Page<ProveedorDTO>> traerProveedores(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK)
                .body(iProveedorService.getProveedores(pageable));
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ProveedorDTO>> traerTodosProveedores(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(iProveedorService.getAllProveedores());
    }

    @GetMapping("/total")
    public int totalProveedores(){
        return iProveedorService.totalProveedores();
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ProveedorDTO> buscarProveedor(@PathVariable Integer id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(iProveedorService.getProveedorById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','EMPLEADO')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<ProveedorDTO> editarProveedor(@RequestBody ProveedorDTO proveedor,
                                                        @PathVariable Integer id,
                                                        @AuthenticationPrincipal Usuario usuario){
        return ResponseEntity.status(HttpStatus.OK)
                .body(iProveedorService.editProveedor(proveedor, usuario.getEmail(), id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','EMPLEADO')")
    @PostMapping("/crear")
    public ResponseEntity<ProveedorDTO> crearProveedor(@RequestBody ProveedorDTO proveedor, @AuthenticationPrincipal UsuarioDTO usuario){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iProveedorService.saveProveedor(proveedor, usuario.getEmail()));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','EMPLEADO')")
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> borrarProveedor(@PathVariable Integer id){
        iProveedorService.deleteProveedor(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Proveedor eliminado");
    }
}
