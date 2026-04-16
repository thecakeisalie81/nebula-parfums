package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.auth.AuthResponse;
import com.nebulaparfums.nebula_parfums.auth.LoginRequest;
import com.nebulaparfums.nebula_parfums.auth.RegisterRequest;
import com.nebulaparfums.nebula_parfums.controller.RolController;
import com.nebulaparfums.nebula_parfums.controller.UsuarioController;
import com.nebulaparfums.nebula_parfums.exception.InvalidPasswordException;
import com.nebulaparfums.nebula_parfums.model.Carrito;
import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;
import com.nebulaparfums.nebula_parfums.model.LogActividad;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.IUsuarioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IDireccionEnvioService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private RolController rolController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private LogActividadService logActividadService;

    @Autowired
    private IUsuarioService usuarioService;







    public AuthResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()
                    )
            );

            UserDetails user = usuarioRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow();
            String token = jwtService.getToken(user);

            LogActividad logActividad = new LogActividad();

            Usuario usuario = usuarioService.getUsuarioByEmail(loginRequest.getEmail());

            if (usuario.getRol().getId_rol() == 1 || usuario.getRol().getId_rol() == 2) {
                logActividad.setUsuario(usuario);
                logActividad.setAccion("Login");
                logActividad.setDetalle("Usuario " + usuario.getNombre() + " ingreso a su cuenta");
                logActividad.setFecha_actualizacion(LocalDateTime.now());
                logActividadService.saveLogActividad(logActividad);
            }
            return AuthResponse.builder().token(token).build();

        } catch (BadCredentialsException e) {
            throw new InvalidPasswordException(loginRequest.getEmail());
        }
    }


    public AuthResponse register(RegisterRequest registerRequest) {


        DireccionEnvio direccionEnvio = new DireccionEnvio();
        Carrito carrito = new Carrito();

        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setRol(rolController.buscarRol(3));
        usuario.setFecha_creacion(LocalDate.now());
        usuario.setEstado(true);
        usuario.setDireccionEnvio(direccionEnvio);
        carrito.setUsuario(usuario);
        usuario.setCarrito(carrito);
        usuarioController.crearUsuario(usuario);
        return new AuthResponse().builder().token(jwtService.getToken(usuario)).build();
    }

    public AuthResponse registrarEmpleado(RegisterRequest registerRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioLogeado = (Usuario) authentication.getPrincipal();

        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        if (registerRequest.getRol().equals("ROLE_ADMIN")) {
            usuario.setRol(rolController.buscarRol(1));
        }else{
            usuario.setRol(rolController.buscarRol(2));
        }
        usuario.setFecha_creacion(LocalDate.now());
        usuario.setEstado(true);

        usuarioController.crearUsuario(usuario);

        LogActividad logActividad = new LogActividad();
        logActividad.setUsuario(usuario);
        logActividad.setAccion("Registro de empleado");
        logActividad.setDetalle("Usuario administrador " + usuarioLogeado.getNombre() + " registro un nuevo empleado");
        logActividad.setFecha_actualizacion(LocalDateTime.now());

        logActividadService.saveLogActividad(logActividad);

        return new AuthResponse().builder().token(jwtService.getToken(usuario)).build();
    }
}
