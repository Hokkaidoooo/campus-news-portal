package com.citywy.domain;

import lombok.Data;

@Data
public class User {
    private int id;
    private String name;
    private String uname;
    private String password;
    private String role;
    private String valid;
}