package org.macnigor.contenthub.services;



import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.macnigor.contenthub.dto.UserDto;
import org.macnigor.contenthub.entity.User;
import org.macnigor.contenthub.entity.enums.ERole;
import org.macnigor.contenthub.exeption.UserAlreadyExistsException;
import org.macnigor.contenthub.repositories.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@Slf4j
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализируем тестовые данные
        userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setName("Test");
        userDto.setLastname("User");
        userDto.setEmail("testuser@example.com");
        userDto.setPassword("password");
    }

    @Test
    void testCreateUser_Success() {
        // Мокируем поведение репозитория для отсутствия пользователя с таким username и email
        when(userRepository.findUserByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findUserByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");

        // Мокируем сохранение пользователя
        User savedUser = new User();
        savedUser.setUsername(userDto.getUsername());
        savedUser.setName(userDto.getName());
        savedUser.setLastname(userDto.getLastname());
        savedUser.setEmail(userDto.getEmail());
        savedUser.setPassword("encodedPassword");
        savedUser.setRoles(Set.of(ERole.ROLE_USER));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Вызов метода
        User createdUser = userService.createUser(userDto);

        // Проверки
        assertNotNull(createdUser);
        assertEquals(userDto.getUsername(), createdUser.getUsername());
        assertEquals(userDto.getEmail(), createdUser.getEmail());
        assertEquals(ERole.ROLE_USER, createdUser.getRoles().iterator().next());

        // Проверяем, что метод save был вызван один раз
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        // Мокируем, чтобы пользователь с таким username уже существовал
        when(userRepository.findUserByUsername(userDto.getUsername())).thenReturn(Optional.of(new User()));

        // Проверка на исключение
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userDto));

        // Проверяем, что метод save не был вызван
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Мокируем поиск пользователя по username
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of(ERole.ROLE_USER));

        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));

        // Вызов метода
        UserDetails userDetails = userService.loadUserByUsername("testuser");

        // Проверки
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Мокируем ситуацию, когда пользователя не существует
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.empty());

        // Проверка на исключение
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("testuser"));
    }

    @Test
    void testExistsByUsername() {
        // Мокируем поведение для проверки существования пользователя
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(new User()));

        // Вызов метода
        boolean exists = userService.existsByUsername("testuser");

        // Проверка
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail() {
        // Мокируем поведение для проверки существования email
        when(userRepository.findUserByEmail("testuser@example.com")).thenReturn(Optional.of(new User()));

        // Вызов метода
        boolean exists = userService.existsByEmail("testuser@example.com");

        // Проверка
        assertTrue(exists);
    }



}

