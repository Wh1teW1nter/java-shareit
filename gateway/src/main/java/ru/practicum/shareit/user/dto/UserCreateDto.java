package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserCreateDto {

    @NotEmpty
    @NotBlank
    private String name;
    @Email
    @NotNull
    private String email;
}