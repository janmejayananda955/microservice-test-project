package com.userService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AddressRequestDto {

    @NotBlank(message = "Address is required")
    private String address;
}
