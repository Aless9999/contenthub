package org.macnigor.contenthub.services;

import org.macnigor.contenthub.dto.UserRegisterDto;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.entity.enums.ERole;
import org.macnigor.contenthub.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));


        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                getAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<ERole> roles) {
        // Преобразуем роли в объект SimpleGrantedAuthority
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    public User createUser(UserRegisterDto registerDto){
        if (existsByEmail(registerDto.getEmail()) || existsByUsername(registerDto.getUsername())) {
            throw new IllegalArgumentException("User with this email or username already exists");
        }
        User newUser = new User();
        newUser.setUsername(registerDto.getUsername());
        newUser.setName(registerDto.getName());
        newUser.setLastname(registerDto.getLastname());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        newUser.setPostList(new ArrayList<>());
        Set<ERole>roles = new HashSet<>();
        roles.add(ERole.ROLE_USER);
        newUser.setRoles(roles);
        userRepository.save(newUser);
        return newUser;
    }

    private boolean existsByUsername(String username) {
        return (userRepository.findUserByUsername(username)).isPresent();
    }

    private boolean existsByEmail(String email) {
        return (userRepository.findUserByEmail(email)).isPresent();
    }

    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(()->new UsernameNotFoundException("Username "+username+" not found"));
    }
}

