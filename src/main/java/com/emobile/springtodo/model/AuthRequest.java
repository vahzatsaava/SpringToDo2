package com.emobile.springtodo.model;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
