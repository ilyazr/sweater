package ru.zakharov.sweater.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.zakharov.sweater.model.User;
import ru.zakharov.sweater.repos.UsersRepo;

@Controller
public class RegistrationController {

    @Autowired
    private UsersRepo usersRepo;

    @GetMapping("/registration")
    public String registrationGet(Model model) {
        model.addAttribute("newUser", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String registrationPost(@ModelAttribute("newUser") User newUser,
                                   Model model) {
        User checkUser = usersRepo.findByUsername(newUser.getUsername());
        if (checkUser == null) {
            newUser.setActive(true);
            usersRepo.save(newUser);
            return "redirect:/";
        }
        else {
            model.addAttribute("errorReg", "Это имя уже занято! Выберите другое!");
            return "registration";
        }
    }

}
