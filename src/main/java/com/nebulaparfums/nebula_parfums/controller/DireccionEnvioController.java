package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.DireccionDTO;
import com.nebulaparfums.nebula_parfums.dto.UsuarioDTO;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.service.interfaces.IDireccionEnvioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/direccion")
public class DireccionEnvioController {

    private final IDireccionEnvioService iDireccionEnvioService;

    @GetMapping("/buscar/{id}")
    public ResponseEntity<DireccionDTO> buscarDireccionEnvio(@PathVariable Integer id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(iDireccionEnvioService.getDireccionEnvioById(id));
    }

    @GetMapping("/midireccion")
    public ResponseEntity<DireccionDTO> obtenerMiDireccion(@AuthenticationPrincipal UsuarioDTO usuario) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(Mapper.toDTO(usuario.getDireccionEnvio()));
    }

    @PutMapping("/editar")
    public ResponseEntity<DireccionDTO> editarMiDireccion(
                                    @RequestBody DireccionDTO direccionEnvio,
                                    @AuthenticationPrincipal UsuarioDTO usuario) {

        DireccionDTO direccion = iDireccionEnvioService.editarDireccionEnvio(
                usuario.getDireccionEnvio().getId_direccion(), direccionEnvio
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(direccion);
    }

    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> borrarDireccionEnvio(@PathVariable Integer id){
        iDireccionEnvioService.deleteDireccionEnvioById(id);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Dirección de envío borrada correctamente");
    }
}
