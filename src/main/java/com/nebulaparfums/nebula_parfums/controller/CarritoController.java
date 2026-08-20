package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.CarritoDTO;
import com.nebulaparfums.nebula_parfums.dto.UsuarioDTO;
import com.nebulaparfums.nebula_parfums.model.Carrito;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carrito")
public class CarritoController {
    private final ICarritoService iCarritoService;

    @GetMapping("/buscar/{id}")
    public Carrito getCarrito(@RequestParam("id") Integer id) {
        return iCarritoService.getCarritoById(id);
    }

    @PostMapping("carrito/crear")
    public ResponseEntity<CarritoDTO> createCarrito(@RequestBody CarritoDTO carrito) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(iCarritoService.saveCarrito(carrito));
    }

    @GetMapping("/micarrito")
    public ResponseEntity<Carrito> obtenerMiCarrito(@AuthenticationPrincipal UsuarioDTO usuario) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iCarritoService.getCarritoById(usuario.getCarrito().getId_carrito()));
    }

    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> deleteCarrito(@PathVariable Integer id) {
        iCarritoService.deleteCarritoById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Carrito limpiado exitosamente");
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<CarritoDTO> updateCarrito(@PathVariable Integer id, @RequestBody CarritoDTO carrito) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iCarritoService.editCarrito(id, carrito));
    }
}
