package com.app.ewalletapi.api.response;

import com.app.ewalletapi.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class UserResponse {

    private long userId;
    private String name;
    private UserStatus userStatus;
    private Set<Long> walletsIds;
}
