package com.financeTracker.financeTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UserDetailsResponseDTO {

    private String fullName;
    private String email;
    private String timezone;
    private String currency;
}
