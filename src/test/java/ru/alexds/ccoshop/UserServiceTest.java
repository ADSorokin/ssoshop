package ru.alexds.ccoshop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.alexds.ccoshop.dto.UserCreateDTO;
import ru.alexds.ccoshop.dto.UserDTO;
import ru.alexds.ccoshop.dto.UserUpdateDTO;
import ru.alexds.ccoshop.entity.Role;
import ru.alexds.ccoshop.entity.User;
import ru.alexds.ccoshop.repository.UserRepository;
import ru.alexds.ccoshop.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterNewUser() {
        // Подготовка тестовых данных
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail("test@example.com");
        userCreateDTO.setPassword("password");
        userCreateDTO.setRole(Role.USER);

        // Мокирование репозитория
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // Мокирование кодировщика паролей
        when(passwordEncoder.encode("password")).thenReturn("hashed_password");

        // Вызов метода
        UserCreateDTO registeredUser = userService.registerNewUser(userCreateDTO);

        // Проверка результата
        assertNotNull(registeredUser);
        assertEquals(userCreateDTO.getEmail(), registeredUser.getEmail());
        assertEquals("hashed_password", registeredUser.getPassword());
    }

    @Test
    public void testGetAllUsers() {
        // Подготовка тестовых данных
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Вызов метода
        List<UserDTO> users = userService.getAllUsers();

        // Проверка результата
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("user1@example.com", users.get(0).getEmail());
        assertEquals("user2@example.com", users.get(1).getEmail());
    }

    @Test
    public void testGetUserById() {
        // Подготовка тестовых данных
        User user = new User();
        user.setId(1L);
        user.setEmail("user1@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Вызов метода
        Optional<UserDTO> userDTO = userService.getUserById(1L);

        // Проверка результата
        assertTrue(userDTO.isPresent());
        assertEquals(Optional.of(1L), userDTO.get().getId());
        assertEquals("user1@example.com", userDTO.get().getEmail());
    }

    @Test
    public void testGetUserById_NotFound() {
        // Подготовка тестовых данных
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Вызов метода
        Optional<UserDTO> userDTO = userService.getUserById(1L);

        // Проверка результата
        assertFalse(userDTO.isPresent());
    }

    @Test
    public void testUpdateUser() {
        // Подготовка тестовых данных
        User user = new User();
        user.setId(1L);
        user.setEmail("user1@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setId(1L);
        userUpdateDTO.setEmail("newemail@example.com");
        userUpdateDTO.setFirstName("John");
        userUpdateDTO.setLastName("Doe");

        // Вызов метода
        UserUpdateDTO updatedUser = userService.updateUser(userUpdateDTO);

        // Проверка результата
        assertNotNull(updatedUser);
        assertEquals("newemail@example.com", updatedUser.getEmail());
        assertEquals("John", updatedUser.getFirstName());
        assertEquals("Doe", updatedUser.getLastName());
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        // Подготовка тестовых данных
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Вызов метода
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.updateUser(new UserUpdateDTO());
        });
    }

    @Test
    public void testDeleteUser() {
        // Подготовка тестовых данных
        when(userRepository.existsById(1L)).thenReturn(true);

        // Вызов метода
        userService.deleteUser(1L);

        // Проверка результата
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testChangeUserStatus() {
        // Подготовка тестовых данных
        User user = new User();
        user.setId(1L);
        user.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Вызов метода
        userService.changeUserStatus(1L, false);

        // Проверка результата
        verify(userRepository, times(1)).save(user);
        assertFalse(user.isActive());
    }

    @Test
    public void testChangePassword() {
        // Подготовка тестовых данных
        User user = new User();
        user.setId(1L);
        user.setPassword("old_password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new_password")).thenReturn("hashed_new_password");

        // Вызов метода
        userService.changePassword(1L, "new_password");

        // Проверка результата
        verify(userRepository, times(1)).save(user);
        assertEquals("hashed_new_password", user.getPassword());
    }
}