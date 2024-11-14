package com.app.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AuthCreateRoleRequest(@Size(max = 3,message = "the user cannot have more than 3 roles") List<String> roleListName)  {
}
