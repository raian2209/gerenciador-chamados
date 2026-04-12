package br.com.dunnastecnologia.chamados.infrastructure.controller.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthWebController {

    private final WebControllerSupport support;

    public AuthWebController(WebControllerSupport support) {
        this.support = support;
    }

    @GetMapping({"/login", "/login/"})
    public String login(Authentication authentication, Model model) {
        model.addAttribute("pageTitle", "Login");
        return "auth/login";
    }
}
