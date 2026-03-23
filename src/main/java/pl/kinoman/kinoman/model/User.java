package pl.kinoman.kinoman.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity // To mówi Springowi: "Zrób z tego tabelę w bazie"
@Table(name = "users")
  // Magia Lomboka: automatyczne gettery i settery
public class User {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // I wygeneruj dla niego ponownie getter/setter. Będą wyglądać tak:
    public boolean isPublicProfile() {
        return publicProfile;
    }

    public void setPublicProfile(boolean publicProfile) {
        this.publicProfile = publicProfile;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    // Pole ze specyfikacji: profil publiczny (true) lub prywatny (false)
    private boolean publicProfile = true;
}


