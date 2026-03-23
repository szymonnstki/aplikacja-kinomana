package pl.kinoman.kinoman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kinoman.kinoman.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}