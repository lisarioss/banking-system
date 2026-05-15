package com.banksystem.userservice.service;

import com.banksystem.userservice.dto.UserRegisterRequest;
import com.banksystem.userservice.dto.UserResponse;
import com.banksystem.userservice.entity.User;
import com.banksystem.userservice.exception.UserAlreadyExistsException;
import com.banksystem.userservice.exception.UserNotFoundException;
import com.banksystem.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse registerUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email já cadastrado");
        }

        if (userRepository.existsByCpf(request.getCpf())) {
            throw new UserAlreadyExistsException("CPF já cadastrado");
        }

        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
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

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        return UserResponse.fromEntity(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        return UserResponse.fromEntity(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public UserResponse updateUser(Long id, UserRegisterRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        user.setActive(false);
        userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
    }
}
