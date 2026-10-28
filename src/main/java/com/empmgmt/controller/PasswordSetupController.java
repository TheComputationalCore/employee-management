package com.empmgmt.controller;

import com.empmgmt.security.service.PasswordSetupService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PasswordSetupController {

    private final PasswordSetupService passwordSetupService;

    @GetMapping("/account/setup")
    public String setupForm() {
        return "accounts/setup";
    }

    @PostMapping("/account/setup")
    public String completeSetup(@RequestParam String username,
                                @RequestParam String setupCode,
                                @RequestParam String password,
                                @RequestParam String confirmPassword,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (password.length() < 12 || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            model.addAttribute("error", "Choose a password of at least 12 characters and no more than 72 UTF-8 bytes.");
            model.addAttribute("username", username);
            model.addAttribute("setupCode", setupCode);
            return "accounts/setup";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "The passwords do not match.");
            model.addAttribute("username", username);
            model.addAttribute("setupCode", setupCode);
            return "accounts/setup";
        }

        try {
            passwordSetupService.completeSetup(username, setupCode, password);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", "Setup code is invalid or expired. Ask HR for a new code.");
            model.addAttribute("username", username);
            return "accounts/setup";
        }

        redirectAttributes.addFlashAttribute("setupComplete", true);
        return "redirect:/login";
    }

    @PostMapping("/web/accounts/setup-code/{employeeId}")
    public String issueSetupCode(@org.springframework.web.bind.annotation.PathVariable Long employeeId,
                                 Model model,
                                 HttpServletResponse response,
                                 HttpServletRequest request) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        try {
            PasswordSetupService.SetupCode setupCode = passwordSetupService.issueSetupCode(employeeId);
            model.addAttribute("setupCode", setupCode.code());
            model.addAttribute("employeeId", employeeId);
            model.addAttribute("username", setupCode.username());
            model.addAttribute("setupUrl", request.getContextPath() + "/account/setup");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("employeeId", employeeId);
        }
        return "accounts/setup-code";
    }
}
