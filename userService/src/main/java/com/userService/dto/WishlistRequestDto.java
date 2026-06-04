package com.userService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class WishlistRequestDto {

    @NotBlank(message = "Product ID is required")
    private String productId;
}
