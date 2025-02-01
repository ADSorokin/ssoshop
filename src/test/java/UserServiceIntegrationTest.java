import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.alexds.ccoshop.dto.UserCreateDTO;
import ru.alexds.ccoshop.dto.UserDTO;
import ru.alexds.ccoshop.dto.UserUpdateDTO;
import ru.alexds.ccoshop.entity.Role;
import ru.alexds.ccoshop.entity.User;
import ru.alexds.ccoshop.repository.UserRepository;
import ru.alexds.ccoshop.service.UserService;
import ru.alexds.ccoshop.PasswordEncoderStub;


import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import({UserService.class, PasswordEncoderStub.class})
public class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testRegisterNewUser() {
        // Подготовка тестовых данных
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail("test@example.com");
        userCreateDTO.setPassword("password");
        userCreateDTO.setRole(Role.USER);

        // Вызов метода
        UserCreateDTO registeredUser = userService.registerNewUser(userCreateDTO);

        // Проверка результата
        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getEmail()).isEqualTo("test@example.com");

        // Проверка сохранения в базе данных
        Optional<User> userInDB = userRepository.findByEmail("test@example.com");
        assertThat(userInDB).isNotNull();
        assertThat(userInDB.get().getPassword()).isEqualTo(passwordEncoder.encode("password"));
    }

    @Test
    public void testGetAllUsers() {
        // Подготовка тестовых данных
        User user1 = new User();
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setEmail("user2@example.com");

        userRepository.save(user1);
        userRepository.save(user2);

        // Вызов метода
        List<UserDTO> users = userService.getAllUsers();

        // Проверка результата
        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0).getEmail()).isEqualTo("user1@example.com");
        assertThat(users.get(1).getEmail()).isEqualTo("user2@example.com");
    }

    @Test
    public void testGetUserById() {
        // Подготовка тестовых данных
        User user = new User();
        user.setEmail("user1@example.com");

        userRepository.save(user);

        // Вызов метода
        Optional<UserDTO> userDTO = userService.getUserById(user.getId());

        // Проверка результата
        assertThat(userDTO).isPresent();
        assertThat(userDTO.get().getEmail()).isEqualTo("user1@example.com");
    }

    @Test
    public void testGetUserById_NotFound() {
        // Вызов метода
        Optional<UserDTO> userDTO = userService.getUserById(1L);

        // Проверка результата
        assertThat(userDTO).isEmpty();
    }

    @Test
    public void testUpdateUser() {
        // Подготовка тестовых данных
        User user = new User();
        user.setEmail("user1@example.com");

        userRepository.save(user);

        // Подготовка данных для обновления
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setId(user.getId());
        userUpdateDTO.setEmail("newemail@example.com");
        userUpdateDTO.setFirstName("John");
        userUpdateDTO.setLastName("Doe");

        // Вызов метода
        UserUpdateDTO updatedUser = userService.updateUser(userUpdateDTO);

        // Проверка результата
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@example.com");
        assertThat(updatedUser.getFirstName()).isEqualTo("John");
        assertThat(updatedUser.getLastName()).isEqualTo("Doe");

        // Проверка сохранения в базе данных
        User updatedUserInDB = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUserInDB.getEmail()).isEqualTo("newemail@example.com");
        assertThat(updatedUserInDB.getFirstName()).isEqualTo("John");
        assertThat(updatedUserInDB.getLastName()).isEqualTo("Doe");
    }

    @Test
    public void testDeleteUser() {
        // Подготовка тестовых данных
        User user = new User();
        user.setEmail("user1@example.com");

        userRepository.save(user);

        // Вызов метода
        userService.deleteUser(user.getId());

        // Проверка результата
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    public void testChangeUserStatus() {
        // Подготовка тестовых данных
        User user = new User();
        user.setEmail("user1@example.com");

        userRepository.save(user);

        // Вызов метода
        userService.changeUserStatus(user.getId(), false);

        // Проверка результата
        User userInDB = userRepository.findById(user.getId()).orElseThrow();
        assertThat(userInDB.isActive()).isFalse();
    }

    @Test
    public void testChangePassword() {
        // Подготовка тестовых данных
        User user = new User();
        user.setEmail("user1@example.com");

        userRepository.save(user);

        // Вызов метода
        userService.changePassword(user.getId(), "new_password");

        // Проверка результата
        User userInDB = userRepository.findById(user.getId()).orElseThrow();
        assertThat(userInDB.getPassword()).isEqualTo(passwordEncoder.encode("new_password"));
    }
}