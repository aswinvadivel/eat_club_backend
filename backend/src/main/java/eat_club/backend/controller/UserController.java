package eat_club.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.eatclub.model.User;
import com.example.eatclub.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    // GET /users/profile?userId=123
    @GetMapping("/profile")
    public User getUserProfile(@RequestParam String userId) {
        return userService.getUserProfile(userId);
    }
}
