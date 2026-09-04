package com.banksystem.userservice.service;

import com.banksystem.userservice.dto.AuthResponse;
import com.banksystem.userservice.dto.UserLoginRequest;
import com.banksystem.userservice.dto.UserResponse;
import com.banksystem.userservice.entity.User;
import com.banksystem.userservice.exception.InvalidCredentialsException;
import com.banksystem.userservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse authenticate(UserLoginRequest request) {
        User user = userService.findUserByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Email ou senha inválidos");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        UserResponse userResponse = UserResponse.fromEntity(user);

        return new AuthResponse(
            token,
            userResponse,
            "Autenticação bem-sucedida"
        );
    }

    public AuthResponse refreshToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidCredentialsException("Token inválido ou expirado");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userService.findUserByEmail(email);
        String newToken = jwtTokenProvider.generateToken(email);

        return new AuthResponse(
            newToken,
            UserResponse.fromEntity(user),
            "Token renovado com sucesso"
        );
    }
}
