package com.amalitech.quickpoll.dto;

import com.amalitech.quickpoll.model.enums.Role;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String email;
    private String name;
    private Role role;
}
