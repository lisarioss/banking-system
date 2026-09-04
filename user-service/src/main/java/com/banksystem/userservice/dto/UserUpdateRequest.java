package com.banksystem.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @NotBlank(message = "Primeiro nome é obrigatório")
    private String firstName;

    @NotBlank(message = "Último nome é obrigatório")
    private String lastName;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(
        regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}",
        message = "Telefone deve estar no formato: (xx) xxxxx-xxxx"
    )
    private String phone;

    @NotBlank(message = "Endereço é obrigatório")
    private String address;
}