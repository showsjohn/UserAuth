package com.TimeTracker.UserAuth.controllers;
import com.TimeTracker.UserAuth.models.Role;
import com.TimeTracker.UserAuth.models.UserEntity;
import com.TimeTracker.UserAuth.services.AuthenticationService;
import com.TimeTracker.UserAuth.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    private final AuthenticationService authService;
    private final JwtService jwtService;

    public RegisterController(AuthenticationService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }


    @GetMapping("/register")
    public String showRegisterPage(){
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage(HttpServletRequest req){
        // Check if the user already has a valid auth_token cookie
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    UserEntity entity = new UserEntity(null, null, jwtService.extractUsername(token), null);
                    System.out.println(entity.getUsername());
                    System.out.println(token);
                    if (jwtService.isValid(token, entity)) {  // You need to create an `isTokenValid` method in the service
                        // If the token is valid, redirect to the dashboard or home page
                        System.out.println("much success");

                        return "success";
                    }
                }
            }
        }

        return "login";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String firstName,
                               @RequestParam String lastName)
    {

        UserEntity user = new UserEntity(firstName, lastName, username, password);
        authService.register(user);

        return "login";
    }

    @PostMapping("/login")
    public String login( @RequestParam String username,
                         @RequestParam String password, HttpServletResponse response)
    {

        // Authenticate and get the token
        String token = authService.authenticate(new UserEntity(null, null, username, password)).getToken();

        if (token != null) {
            // Create the cookie and set its value
            Cookie cookie = new Cookie("auth_token", token);
            cookie.setHttpOnly(true);  // Prevents client-side access to the cookie (mitigates XSS attacks)
            cookie.setSecure(true);    // Ensure cookie is only sent over HTTPS
            cookie.setPath("/");       // Makes the cookie accessible to the entire application
            cookie.setMaxAge(60 * 60 * 24);  // Cookie expires in 1 day (set to your preferred expiration)

            // Add the cookie to the response
            response.addCookie(cookie);

            return "success";
        } else {
            return "login";
        }
    }


}
