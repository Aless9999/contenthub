package org.macnigor.contenthub.dto;

import lombok.Data;

@Data
public class UserRegisterDto {

    private String username;
    private String name;
    private String lastname;
    private String email;
    private String password;

}
