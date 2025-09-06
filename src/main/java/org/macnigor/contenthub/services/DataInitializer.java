package org.macnigor.contenthub.services;

import org.macnigor.contenthub.dto.UserDto;
import org.macnigor.contenthub.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


public class DataInitializer implements CommandLineRunner {
    private final UserService userService;
 private final UserRepository repository;
    public DataInitializer(UserService userService, UserRepository repository) {
        this.userService = userService;
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("admin");
        userDto.setLastname("admin");
        userDto.setUsername("admin");
        userDto.setEmail("jaga@cmail.com");
        userDto.setPassword("100");
        if(userService.existsByUsername("admin")){

            userService.deleteUser("admin");
        };
        userService.createUser(userDto);

    }
}
