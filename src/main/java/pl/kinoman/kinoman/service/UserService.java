package pl.kinoman.kinoman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // IMPORT
import org.springframework.stereotype.Service;
import pl.kinoman.kinoman.model.User;
import pl.kinoman.kinoman.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // DODANE

    public void registerUser(User user) {
        // Przed zapisem szyfrujemy hasło!
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Metoda do aktualizacji widoczności profilu
    public void updateUserProfileVisibility(String username, boolean isPublic) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setPublicProfile(isPublic);
            userRepository.save(user); // Zapisujemy zaktualizowanego użytkownika
        }
    }
}