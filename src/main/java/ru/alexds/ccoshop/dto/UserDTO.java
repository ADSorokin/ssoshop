package ru.alexds.ccoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.Role;
import ru.alexds.ccoshop.entity.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private Role role;
    private boolean active;

    private String password;


    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.active = user.isActive();
    }


}

