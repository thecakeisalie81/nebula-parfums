package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.auth.*;
import com.nebulaparfums.nebula_parfums.dto.LogDTO;
import com.nebulaparfums.nebula_parfums.dto.UsuarioDTO;
import com.nebulaparfums.nebula_parfums.exception.InvalidPasswordException;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.model.*;
import com.nebulaparfums.nebula_parfums.repository.IPasswordResetTokenRepository;
import com.nebulaparfums.nebula_parfums.repository.IUsuarioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IUsuarioRepository usuarioRepository;
    private final LogActividadService logActividadService;
    private final IUsuarioService usuarioService;
    private final IPasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()
                    )
            );

            UserDetails user = usuarioRepository.findByEmail(loginRequest.getEmail()).map(Mapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            String token = jwtService.getToken(user);

            LogActividad logActividad = new LogActividad();

            Usuario usuario = usuarioService.getUsuarioByEmail(loginRequest.getEmail());

            if (usuario.getRol() != Rol.CLIENTE) {
                logActividad.setUsuario(usuario);
                logActividad.setAccion("Login");
                logActividad.setDetalle("Usuario " + usuario.getNombre() + " ingreso a su cuenta");
                logActividad.setFecha_actualizacion(LocalDateTime.now());
                logActividadService.saveLogActividad(Mapper.toDTO(logActividad));
            }
            return AuthResponse.builder().token(token).build();

        } catch (BadCredentialsException e) {
            throw new InvalidPasswordException(loginRequest.getEmail());
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (resetToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El token ha expirado");
        }

        if (request.getNuevaPassword() == null || request.getNuevaPassword().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(request.getNuevaPassword()));
        usuarioRepository.save(usuario);

        passwordResetTokenRepository.delete(resetToken);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un correo electrónico");
        }

        String email = request.getEmail().trim();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta asociada a ese correo"));

        if (!usuario.getRol().equals(Rol.CLIENTE)) {
            throw new IllegalArgumentException("Este usuario no puede recuperar la contraseña desde aquí. Contacte al administrador");
        }

        String token = UUID.randomUUID().toString();
        String enlace = "http://localhost:8080/reset-password.html?token=" + token;

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
    }


    public AuthResponse register(RegisterRequest registerRequest) {

        DireccionEnvio direccionEnvio = new DireccionEnvio();
        Carrito carrito = new Carrito();

        UsuarioDTO usuario = UsuarioDTO.builder()
                .nombre(registerRequest.getNombre())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .estado(true)
                .fecha_creacion(LocalDate.now())
                .rol(Rol.CLIENTE)
                .carrito(carrito)
                .direccionEnvio(direccionEnvio)
                .build();

        UsuarioDTO dto = usuarioService.saveUsuario(usuario);

        return AuthResponse.builder().token(jwtService.getToken(dto)).build();
    }

    @Transactional
    public UsuarioDTO registrarEmpleado(RegisterRequest registerRequest, UsuarioDTO usuarioLogueado) {

        UsuarioDTO usuario = UsuarioDTO.builder()
                .nombre(registerRequest.getNombre())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .estado(true)
                .fecha_creacion(LocalDate.now())
                .rol(registerRequest.getRol())
                .build();

        usuario = usuarioService.saveUsuario(usuario);

        LogDTO log = LogDTO.builder()
                .accion("Registro de empleado")
                .usuario_id(usuario.getId())
                .detalle("Usuario administrador " + usuarioLogueado.getNombre() + " registro un nuevo empleado")
                .fecha_actualizacion(LocalDateTime.now())
                .build();

        logActividadService.saveLogActividad(log);

        return usuario;
    }
}
