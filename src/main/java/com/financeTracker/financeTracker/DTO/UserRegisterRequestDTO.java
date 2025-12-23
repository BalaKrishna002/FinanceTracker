package com.financeTracker.financeTracker.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequestDTO {

    private String fullName;
    private String email;
    private String password;
    private String timezone;
    private String currency;
    private String role = "USER";
}

