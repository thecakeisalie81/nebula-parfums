package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.auth.*;
import com.nebulaparfums.nebula_parfums.controller.RolController;
import com.nebulaparfums.nebula_parfums.controller.UsuarioController;
import com.nebulaparfums.nebula_parfums.exception.InvalidPasswordException;
import com.nebulaparfums.nebula_parfums.model.*;
import com.nebulaparfums.nebula_parfums.repository.IPasswordResetTokenRepository;
import com.nebulaparfums.nebula_parfums.repository.IUsuarioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICarritoService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IDireccionEnvioService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    private IPasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

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

    public ResponseEntity<?> resetPassword(ResetPasswordRequest request) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(request.getToken());

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Token inválido"
            ));
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "El token ha expirado"
            ));
        }

        if (request.getNuevaPassword() == null || request.getNuevaPassword().length() < 8) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "La contraseña debe tener al menos 8 caracteres"
            ));
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(request.getNuevaPassword()));
        usuarioRepository.save(usuario);

        passwordResetTokenRepository.delete(resetToken);

        return ResponseEntity.ok(Map.of(
                "message", "Contraseña actualizada correctamente"
        ));
    }

    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Debe ingresar un correo electrónico"
            ));
        }

        String email = request.getEmail().trim();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "No existe una cuenta asociada a ese correo"
            ));
        }

        Usuario usuario = usuarioOpt.get();

        if (!"ROLE_CLIENTE".equals(usuario.getRol().getNombre_rol())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Este usuario no puede recuperar la contraseña desde aquí. Contacte al administrador"
            ));
        }

        String token = UUID.randomUUID().toString();
        String enlace = "http://localhost:8080/reset-password.html?token=" + token;

        try {
            emailService.enviarCorreo(
                    usuario.getEmail(),
                    "Recuperación de contraseña",
                    "Haga clic en el siguiente enlace para restablecer su contraseña: " + enlace
            );

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUsuario(usuario);
            resetToken.setFechaExpiracion(LocalDateTime.now().plusMinutes(30));

            passwordResetTokenRepository.save(resetToken);

            return ResponseEntity.ok(Map.of(
                    "message", "Se ha enviado un enlace de recuperación a su correo"
            ));

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.internalServerError().body(Map.of(
                    "message", "No se pudo enviar el correo de recuperación. Verifique la configuración del correo."
            ));
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
