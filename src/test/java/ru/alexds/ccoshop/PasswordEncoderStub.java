package ru.alexds.ccoshop;



import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderStub implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString(); // Просто возвращает пароль без шифрования
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword); // Сравнивает пароли
    }
}
