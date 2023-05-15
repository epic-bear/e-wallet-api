package com.app.ewalletapi.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private long userId;
    @NotBlank
    private String name;
    @NotBlank
    private String password;
}
