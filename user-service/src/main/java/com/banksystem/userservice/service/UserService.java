package com.banksystem.userservice.service;

import com.banksystem.userservice.dto.UserRegisterRequest;
import com.banksystem.userservice.dto.UserResponse;
import com.banksystem.userservice.entity.User;
import com.banksystem.userservice.exception.UserAlreadyExistsException;
import com.banksystem.userservice.exception.UserNotFoundException;
import com.banksystem.userservice.repository.UserRepository;
import com.banksystem.userservice.dto.UserUpdateRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse registerUser(UserRegisterRequest request) {

        Objects.requireNonNull(
            request,
            "Dados do usuário são obrigatórios"
        );

        String email = Objects.requireNonNull(
            request.getEmail(),
            "Email é obrigatório"
        ).trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(
                "Email já cadastrado"
            );
        }

        if (userRepository.existsByCpf(request.getCpf())) {
            throw new UserAlreadyExistsException(
                "CPF já cadastrado"
            );
        }

        User user = User.builder()
            .email(email)
            .password(
                passwordEncoder.encode(request.getPassword())
            )
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .cpf(request.getCpf())
            .phone(request.getPhone())
            .address(request.getAddress())
            .active(true)
            .build();

        User savedUser = userRepository.save(user);

        return UserResponse.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        Objects.requireNonNull(
            id,
            "ID do usuário é obrigatório"
        );

        User user = findUserById(id);

        return UserResponse.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {

        Objects.requireNonNull(
            email,
            "Email é obrigatório"
        );

        User user = findUserByEmail(email);

        return UserResponse.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
            .stream()
            .map(UserResponse::fromEntity)
            .toList();
    }

    public UserResponse updateUser(
        Long id,
        UserUpdateRequest request
    ) {

        Objects.requireNonNull(
            id,
            "ID do usuário é obrigatório"
        );

        Objects.requireNonNull(
            request,
            "Dados do usuário são obrigatórios"
        );

        User user = findUserById(id);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        User updatedUser = userRepository.save(user);

        return UserResponse.fromEntity(updatedUser);
    }

    public void deleteUser(Long id) {

        Objects.requireNonNull(
            id,
            "ID do usuário é obrigatório"
        );

        User user = findUserById(id);

        if (!Boolean.TRUE.equals(user.getActive())) {
            return;
        }

        user.setActive(false);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {

        Objects.requireNonNull(
            email,
            "Email é obrigatório"
        );

        String normalizedEmail =
            email.trim().toLowerCase();

        return userRepository
            .findByEmail(normalizedEmail)
            .orElseThrow(() ->
                new UserNotFoundException(
                    "Usuário não encontrado"
                )
            );
    }

    private User findUserById(Long id) {

        return userRepository
            .findById(id)
            .orElseThrow(() ->
                new UserNotFoundException(
                    "Usuário não encontrado"
                )
            );
    }
}