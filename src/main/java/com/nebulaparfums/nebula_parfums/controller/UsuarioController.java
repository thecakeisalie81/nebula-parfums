package com.nebulaparfums.nebula_parfums.controller;
import com.nebulaparfums.nebula_parfums.dto.UsuarioDTO;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.service.interfaces.IDireccionEnvioService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/usuario")
@RestController
@AllArgsConstructor
public class UsuarioController {
    private final IUsuarioService iUsuarioService;
    private final IDireccionEnvioService iDireccionEnvioService;

    @GetMapping("/usuarios")
    public ResponseEntity<Page<UsuarioDTO>> traerUsuarios(Pageable pageable,String nombre) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iUsuarioService.getUsuarios(pageable, nombre));
    }

    @GetMapping("/autenticacion")
    public ResponseEntity<Usuario> usuarioAutenticado(@AuthenticationPrincipal UsuarioDTO usuario) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iUsuarioService.getUsuarioByEmail(usuario.getEmail()));
    }

    @GetMapping("/contador")
    public ResponseEntity<Integer> totalUsuarios() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iUsuarioService.totalUsuarios());
    }

    @GetMapping("/activos")
    public ResponseEntity<Integer> activos() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iUsuarioService.totalUsuariosActivos());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuario(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(Mapper.toDTO(iUsuarioService.getUsuarioById(id)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/crear")
    public ResponseEntity<UsuarioDTO> crearUsuario(@RequestBody UsuarioDTO usuario) {
        DireccionEnvio dto = new DireccionEnvio();
        DireccionEnvio direccion = iDireccionEnvioService.saveDireccion(dto);
        usuario.setDireccionEnvio(direccion);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iUsuarioService.saveUsuario(usuario));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<UsuarioDTO> editarUsuario(@PathVariable Integer id, @RequestBody UsuarioDTO usuario) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iUsuarioService.editUsuario(id, usuario));
    }

    @DeleteMapping("/borrar")
    public ResponseEntity<String> borrarUsuario(@PathVariable Integer id) {
        iUsuarioService.deleteUsuarioById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Usuario borrado");
    }
}
