package pl.kinoman.kinoman;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kinoman.kinoman.model.User;
import pl.kinoman.kinoman.repository.UserRepository;
import pl.kinoman.kinoman.service.UserService;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_shouldEncodePasswordAndSaveUser() {
        User user = new User();
        user.setUsername("jan");
        user.setPassword("haslo123");
        when(passwordEncoder.encode("haslo123"))
                .thenReturn("zaszyfrowaneHaslo");
        userService.registerUser(user);
        assertEquals("zaszyfrowaneHaslo", user.getPassword());
        verify(passwordEncoder).encode("haslo123");
        verify(userRepository).save(user);
    }

    @Test
    void findByUsername_shouldReturnUser() {
        User user = new User();
        user.setUsername("jan");
        when(userRepository.findByUsername("jan"))
                .thenReturn(user);
        User result = userService.findByUsername("jan");
        assertNotNull(result);
        assertEquals("jan", result.getUsername());
    }

    @Test
    void updateUserProfileVisibility_shouldUpdateUser() {
        User user = new User();
        user.setUsername("jan");
        user.setPublicProfile(false);
        when(userRepository.findByUsername("jan"))
                .thenReturn(user);
        userService.updateUserProfileVisibility("jan", true);
        assertTrue(user.isPublicProfile());
        verify(userRepository).save(user);
    }
}