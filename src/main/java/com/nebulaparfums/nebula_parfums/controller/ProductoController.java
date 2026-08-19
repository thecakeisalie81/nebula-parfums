package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.MovimientoDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductoDTO;
import com.nebulaparfums.nebula_parfums.dto.UsuarioDTO;
import com.nebulaparfums.nebula_parfums.service.interfaces.IMovimientoInventarioService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/producto")
@RequiredArgsConstructor
public class ProductoController {
    private final IProductoService iProductoService;
    private final IMovimientoInventarioService iMovimientoInventarioService;


    @GetMapping("/productos")
    public ResponseEntity<Page<ProductoDTO>> getProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer idCategoria,
            @RequestParam(required = false) Integer idProveedor,
            @RequestParam(required = false) String estadoStock,
            @RequestParam(required = false) Integer precioMinimo,
            @RequestParam(required = false) Integer precioMaximo,
            @RequestParam(required = false) Integer disponible,
            Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(iProductoService.getProductosFiltrados(
                pageable, nombre, idCategoria, idProveedor, estadoStock, precioMinimo, precioMaximo, disponible
        ));
    }

    @GetMapping("/inventario")
    public ResponseEntity<List<ProductoDTO>> getProductosReporte() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iProductoService.getProductos());
    }

    @GetMapping("/lowstock")
    public Integer lowStock() {
        return iProductoService.getProductosLowStock();
    }

    @GetMapping("/producto/bajostock")
    public ResponseEntity<Page<ProductoDTO>> bajosStock(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iProductoService.getProductosBajoStock(pageable));
    }

    @GetMapping("/nostock")
    public ResponseEntity<Integer> noStock() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iProductoService.getProductosSinStock());
    }

    @GetMapping("/totalproductos")
    public ResponseEntity<Integer> totalProductos() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iProductoService.getTotalStock());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ProductoDTO> buscarProducto(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(iProductoService.getProductoById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> borrarProducto(@PathVariable Integer id) {
        iProductoService.deleteProducto(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Producto borrado");
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearProducto(@RequestBody ProductoDTO producto,
                                                             @AuthenticationPrincipal UsuarioDTO usuario) {

        ProductoDTO newProducto = iProductoService.saveProducto(producto);

        MovimientoDTO movimientoDTO = new MovimientoDTO();
        movimientoDTO.setId_producto(newProducto.getId());
        movimientoDTO.setCantidad(producto.getStock_actual());
        movimientoDTO.setId_usuario(usuario.getId());

        MovimientoDTO movimientoRegistro = iMovimientoInventarioService.registrarRegistroProducto(movimientoDTO);

        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("Producto", newProducto);
        respuesta.put("Registro", movimientoRegistro);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<Map<String, Object>> editarProducto(@PathVariable Integer id_producto,
                                                              @RequestBody ProductoDTO DTO,
                                                              @AuthenticationPrincipal UsuarioDTO usuario) {
        //cantidad de producto previa a la edicion
        int cantidadOld = iProductoService.getProductoById(id_producto).getStock_actual();
        ProductoDTO producto = iProductoService.editProducto(id_producto, DTO);

        //para registrar datos editados del producto
        MovimientoDTO movimientoDTO = new MovimientoDTO();
        movimientoDTO.setId_producto(producto.getId());
        movimientoDTO.setCantidad(producto.getStock_actual());
        movimientoDTO.setId_usuario(usuario.getId());
        movimientoDTO = iMovimientoInventarioService.registrarEdicionProducto(movimientoDTO);

        //Para registrar salida o entrada de stock
        MovimientoDTO movimiento =  new MovimientoDTO();
        movimiento.setId_producto(producto.getId());
        movimiento.setCantidad(producto.getStock_actual());
        movimiento.setId_usuario(usuario.getId());

        if (cantidadOld < producto.getStock_actual()) {
            movimiento = iMovimientoInventarioService.registrarEntrada(movimiento);
        } else if (cantidadOld > producto.getStock_actual()) {
            movimiento = iMovimientoInventarioService.registrarSalida(movimiento);
        }

        HashMap<String, Object> respuesta = new HashMap<>();
        respuesta.put("Producto", producto);
        respuesta.put("Actualización", movimientoDTO);
        respuesta.put("Movimiento", movimiento);

        return ResponseEntity.status(HttpStatus.OK)
                .body(respuesta);
    }
}
