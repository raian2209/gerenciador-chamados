package br.com.dunnastecnologia.chamados.infrastructure.controller.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeWebController {

    private final WebControllerSupport support;

    public HomeWebController(WebControllerSupport support) {
        this.support = support;
    }

    @GetMapping
    public String home(Authentication authentication) {
        if (!support.isAuthenticated(authentication)) {
            return "redirect:/login";
        }

        return "redirect:" + support.homePathForRole(support.authenticatedUser(authentication).role());
    }
}
