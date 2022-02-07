package kma.topic2.junit.tests;

import kma.topic2.junit.exceptions.LoginExistsException;
import kma.topic2.junit.exceptions.UserNotFoundException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.model.User;
import kma.topic2.junit.repository.UserRepository;
import kma.topic2.junit.service.UserService;
import kma.topic2.junit.validation.UserValidator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @SpyBean
    private UserValidator userValidator;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void CreateUserTest() {
        String login = "login123";
        NewUser newUser = NewUser.builder()
                .login(login)
                .password("Pass123")
                .fullName("Name Surname")
                .build();
        userService.createNewUser(newUser);
        Mockito.verify(userValidator).validateNewUser(ArgumentMatchers.any());
        assertThat(userRepository.isLoginExists(login)).isTrue();
    }

    @Test
    void GetNonexistentUser() {
        String login = "NonexistentUser";
        assertThatThrownBy(() -> userService.getUserByLogin(login))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Can't find user by login: "+login);
    }

    @Test
    void CreateDuplicateUserTest() {
        NewUser newUser = NewUser.builder()
                .login("duplicate")
                .password("Pass123")
                .fullName("Name Surname")
                .build();
        userService.createNewUser(newUser);
        Mockito.verify(userValidator).validateNewUser(ArgumentMatchers.any());
        assertThatThrownBy(() -> userService.createNewUser(newUser))
            .isInstanceOf(LoginExistsException.class);
    }

    @Test
    void CheckSaveCorrectlyTest() {
        String login = "anotherUser123";
        String password = "Pass123";
        String fullName = "Name Surname";
        NewUser newUser = NewUser.builder()
                .login(login)
                .password(password)
                .fullName(fullName)
                .build();
        userService.createNewUser(newUser);
        Mockito.verify(userValidator).validateNewUser(ArgumentMatchers.any());
        User user = userService.getUserByLogin(login);
        assertThat(user.getLogin()).isEqualTo(login);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getFullName()).isEqualTo(fullName);
    }
}