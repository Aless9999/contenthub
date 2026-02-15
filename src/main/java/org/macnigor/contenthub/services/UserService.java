package org.macnigor.contenthub.services;

import lombok.extern.slf4j.Slf4j;
import org.macnigor.contenthub.dto.UserDto;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.entity.enums.ERole;
import org.macnigor.contenthub.exception.UserAlreadyExistsException;
import org.macnigor.contenthub.repositories.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class UserService implements UserDetailsService {



    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {




        try {
            User user = userRepository.findUserByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    getAuthorities(user.getRoles())
            );
        } catch (DataAccessException e) {
            log.error("Database access error while loading user by username: {}", username, e);
            throw new RuntimeException("Error accessing user data", e); // или другое подходящее исключение
        } catch (Exception e) {
            log.error("Unexpected error while loading user by username: {}", username, e);
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }


    private Collection<? extends GrantedAuthority> getAuthorities(Set<ERole> roles) {
        log.debug("Converting roles {} to authorities", roles);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority( role.name()))
                .collect(Collectors.toList());
    }

    public User createUser(UserDto registerDto) {
        Set<ERole> roles = new HashSet<>();
        try {
            if (existsByEmail(registerDto.getEmail()) || existsByUsername(registerDto.getUsername())) {
                throw new UserAlreadyExistsException("User with this email or username already exists");
            }

            User newUser = new User();
            newUser.setUsername(registerDto.getUsername());
            newUser.setName(registerDto.getName());
            newUser.setLastname(registerDto.getLastname());
            newUser.setEmail(registerDto.getEmail());
            newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            newUser.setPostList(new ArrayList<>());
            if(registerDto.getUsername().equals("admin")){
                roles.add(ERole.ROLE_ADMIN);
            }
            roles.add(ERole.ROLE_USER);
            newUser.setRoles(roles);

            userRepository.save(newUser);
            return newUser;
        } catch (DataAccessException e) {
            log.error("Database error while creating user with email: {}", registerDto.getEmail(), e);
            throw new RuntimeException("Database error occurred while creating user", e);
        } catch (UserAlreadyExistsException e) {
            log.warn("User creation failed: {}", e.getMessage());
            throw e; // Перебрасываем исключение, если оно уже конкретное
        } catch (Exception e) {
            log.error("Unexpected error while creating user", e);
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }


    boolean existsByUsername(String username) {
        boolean exists = userRepository.findUserByUsername(username).isPresent();
        log.debug("Checking if username {} exists: {}", username, exists);
        return exists;
    }

    boolean existsByEmail(String email) {
        boolean exists = userRepository.findUserByEmail(email).isPresent();
        log.debug("Checking if email {} exists: {}", email, exists);
        return exists;
    }

    public User findByUsername(String username) {
        try {
            return userRepository.findUserByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
        } catch (DataAccessException e) {
            log.error("Database error while searching for user by username: {}", username, e);
            throw new RuntimeException("Database error occurred while finding user", e);
        } catch (Exception e) {
            log.error("Unexpected error while searching for user by username: {}", username, e);
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }
    public void deleteUser(Long userId ){
        User user = userRepository.findUserById(userId).orElseThrow(()->new UsernameNotFoundException("User with id "+userId+" not found"));
        userRepository.delete(user);
    }
    public void deleteUser(String username ){
        User user = userRepository.findUserByUsername(username).orElseThrow(()->new UsernameNotFoundException("User with username "+username+" not found"));
        userRepository.delete(user);
    }

    public void redactorUser(User user){
        User newUser = new User();

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
