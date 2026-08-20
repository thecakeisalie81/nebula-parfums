package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.CarritoDetalleDTO;
import com.nebulaparfums.nebula_parfums.model.CarritoDetalle;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoDetalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carritodetalle")
public class CarritoDetalleController {
    private final ICarritoDetalleService iCarritoDetalleService;

    @GetMapping("/buscar/{id}")
    public ResponseEntity<CarritoDetalle> getCarrito(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iCarritoDetalleService.getCarritoDetalleById(id));
    }

    @PostMapping("/crear")
    public ResponseEntity<CarritoDetalleDTO> createCarritoDetalle(@RequestBody CarritoDetalleDTO detalle) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iCarritoDetalleService.saveCarritoDetalle(detalle));
    }


    @PutMapping("/editar/{id}")
    public ResponseEntity<CarritoDetalleDTO> editCarritoDetalle(@PathVariable Integer id,
                                                                @RequestBody CarritoDetalleDTO detalle) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iCarritoDetalleService.editCarritoDetalle(id, detalle));
    }

    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> borrarCarritoDetalle(@PathVariable Integer id) {
        iCarritoDetalleService.deleteCarritoDetalleById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Detalle eliminado del carrito");
    }
}
