package com.app.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Valid
public record AuthCreateUserRequest(@NotBlank String name,

                                    @NotBlank String lastName,
                                    @NotBlank String password,
                                    @NotBlank String email,
                                    @NotBlank String phone,
                                    @Valid AuthCreateRoleRequest roleRequest) {

}
