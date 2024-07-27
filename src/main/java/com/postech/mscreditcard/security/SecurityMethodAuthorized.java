package com.postech.mscreditcard.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SecurityMethodAuthorized {
    String method;
    String role;
}