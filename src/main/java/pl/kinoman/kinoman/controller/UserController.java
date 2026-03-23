package pl.kinoman.kinoman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.kinoman.kinoman.model.User;
import pl.kinoman.kinoman.service.UserService;
import pl.kinoman.kinoman.service.RatingService;
import pl.kinoman.kinoman.service.WatchlistService;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class UserController {

    @Autowired private UserService userService;
    @Autowired private RatingService ratingService;
    @Autowired private WatchlistService watchlistService; // DODANE

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.registerUser(user);
        return "redirect:/users";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/user/{username}")
    public String showUserProfile(@PathVariable String username, Model model, Principal principal) {
        User user = userService.findByUsername(username);

        if (user == null) {
            return "redirect:/";
        }

        boolean isOwner = principal != null && principal.getName().equals(username);
        model.addAttribute("profileUser", user);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("user", principal); // Dodajemy do poprawnego działania paska nawigacji

        if (!user.isPublicProfile() && !isOwner) {
            model.addAttribute("isPrivate", true);
        } else {
            model.addAttribute("isPrivate", false);
            model.addAttribute("userRatings", ratingService.getRatingsByUser(username));
            // DODANE: Przekazujemy watchlistę użytkownika
            model.addAttribute("watchlist", watchlistService.getUserWatchlist(username));
        }

        return "user-profile";
    }
    // Wyświetla stronę ustawień zalogowanego użytkownika
    @GetMapping("/user/settings")
    public String showSettings(Model model, Principal principal) {
        if (principal == null) return "redirect:/login"; // Zabezpieczenie

        User user = userService.findByUsername(principal.getName());
        model.addAttribute("profileUser", user);
        model.addAttribute("user", principal); // Do działania paska nawigacji
        return "user-settings";
    }

    // Odbiera zmianę z formularza i zapisuje w bazie
    @PostMapping("/user/settings")
    public String updateSettings(@RequestParam(defaultValue = "false") boolean publicProfile, Principal principal) {
        if (principal != null) {
            userService.updateUserProfileVisibility(principal.getName(), publicProfile);
            return "redirect:/user/" + principal.getName(); // Powrót na profil
        }
        return "redirect:/";
    }
}