package com.musicboxd.server.dto;

import lombok.Data;

@Data
public class ChangePassword {
    private String password;
    private String repeatPassword;
}
