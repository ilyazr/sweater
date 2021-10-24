package ru.zakharov.registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest req) {
        return registrationService.register(req);
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }

    @PostMapping("/recreate")
    public String recreateConfirmationToken(@RequestBody RegistrationRequest req) {
        return registrationService.recreateConfirmationToken(req);
    }

}
