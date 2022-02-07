package kma.topic2.junit.tests;

import kma.topic2.junit.exceptions.ConstraintViolationException;
import kma.topic2.junit.exceptions.LoginExistsException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.repository.UserRepository;
import kma.topic2.junit.validation.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserValidator userValidator;

    @Test
    public void ValidUserTest() {
        Mockito.when(userRepository.isLoginExists(ArgumentMatchers.anyString())).thenReturn(false);
        NewUser newUser = NewUser.builder()
                .login("login123")
                .password("Pass123")
                .fullName("Name Surname")
                .build();
        assertThatCode(() -> userValidator.validateNewUser(newUser)).doesNotThrowAnyException();
    }

    @Test
    public void LoginExistsTest() {
        Mockito.when(userRepository.isLoginExists(ArgumentMatchers.anyString())).thenReturn(true);
        NewUser newUser = NewUser.builder()
                .login("login123")
                .password("Pass123")
                .fullName("Name Surname")
                .build();
        assertThatThrownBy(() -> userValidator.validateNewUser(newUser))
                .isInstanceOf(LoginExistsException.class)
                .hasMessageContaining(newUser.getLogin());

    }

    @Test
    public void InvalidSizePasswordTest() {
        Mockito.when(userRepository.isLoginExists(ArgumentMatchers.anyString())).thenReturn(false);
        NewUser newUser = NewUser.builder()
                .login("login123")
                .password("12")
                .fullName("Name Surname")
                .build();
        NewUser newUser2 = NewUser.builder()
                .login("login123")
                .password("123456789")
                .fullName("Name Surname")
                .build();
        ConstraintViolationException throwable = catchThrowableOfType(() -> userValidator.validateNewUser(newUser), ConstraintViolationException.class);
        ConstraintViolationException throwable2 = catchThrowableOfType(() -> userValidator.validateNewUser(newUser2), ConstraintViolationException.class);
        assertThat(throwable.getMessage()).isEqualTo("You have errors in you object");
        assertThat(throwable.getErrors()).containsExactly("Password has invalid size");
        assertThat(throwable2.getMessage()).isEqualTo("You have errors in you object");
        assertThat(throwable2.getErrors()).containsExactly("Password has invalid size");
    }

    @Test
    public void InvalidFormatPasswordTest() {
        Mockito.when(userRepository.isLoginExists(ArgumentMatchers.anyString())).thenReturn(false);
        NewUser newUser = NewUser.builder()
                .login("login123")
                .password("12-$312")
                .fullName("Name Surname")
                .build();
        NewUser newUser2 = NewUser.builder()
                .login("login123")
                .password("1234_567-$89")
                .fullName("Name Surname")
                .build();
        ConstraintViolationException throwable = catchThrowableOfType(() -> userValidator.validateNewUser(newUser), ConstraintViolationException.class);
        ConstraintViolationException throwable2 = catchThrowableOfType(() -> userValidator.validateNewUser(newUser2), ConstraintViolationException.class);
        assertThat(throwable.getMessage()).isEqualTo("You have errors in you object");
        assertThat(throwable.getErrors()).containsExactly("Password doesn't match regex");
        assertThat(throwable2.getMessage()).isEqualTo("You have errors in you object");
        assertThat(throwable2.getErrors()).containsExactly("Password has invalid size", "Password doesn't match regex");
    }
}