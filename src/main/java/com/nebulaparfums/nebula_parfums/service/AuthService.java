package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.auth.AuthResponse;
import com.nebulaparfums.nebula_parfums.auth.LoginRequest;
import com.nebulaparfums.nebula_parfums.auth.RegisterRequest;
import com.nebulaparfums.nebula_parfums.controller.RolController;
import com.nebulaparfums.nebula_parfums.controller.UsuarioController;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        UserDetails user = usuarioRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
        String token = jwtService.getToken(user);
        return AuthResponse.builder().token(token).build();
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setRol(rolController.buscarRol(3));
        usuario.setEstado(true);

        usuarioController.crearUsuario(usuario);

        return new AuthResponse().builder().token(jwtService.getToken(usuario)).build();
    }

    public AuthResponse registrarEmpleado(RegisterRequest registerRequest) {
        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setRol(rolController.buscarRol(2));
        usuario.setEstado(true);

        usuarioController.crearUsuario(usuario);

        return new AuthResponse().builder().token(jwtService.getToken(usuario)).build();
    }


}
