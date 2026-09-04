package com.banksystem.userservice.dto;

import com.banksystem.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String cpf;
    private String phone;
    private String address;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getCpf(),
            user.getPhone(),
            user.getAddress(),
            user.getActive(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
