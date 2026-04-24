package com.ims.ui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ims.util.Helpers;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginForm {

    @Value("${ims.auth.username:admin}")
    private String configuredUsername;

    @Value("${ims.auth.password:admin123}")
    private String configuredPassword;

    @GetMapping({ "/", "/login" })
    public String showLogin(HttpSession session) {
        if (Helpers.isAuthenticated(session)) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (configuredUsername.equals(username) && configuredPassword.equals(password)) {
            session.setAttribute("authenticated", true);
            session.setAttribute("displayName", "System Administrator");
            return "redirect:/dashboard";
        }

        redirectAttributes.addFlashAttribute("errorMessage", "Invalid username or password.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
