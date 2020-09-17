package ru.zakharov.sweater.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.zakharov.sweater.model.Message;
import ru.zakharov.sweater.model.User;
import ru.zakharov.sweater.repos.MessagesRepo;
import ru.zakharov.sweater.repos.UsersRepo;

import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private MessagesRepo messagesRepo;

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", usersRepo.findById(user.getId()).get());
        return "profile";
    }

    @GetMapping("/profile/{id}")
    public String showOtherProfile(@PathVariable("id") int id,
                              @AuthenticationPrincipal User principal,
                              @RequestParam(value = "filter", required = false) String filter,
                              Model model) {
        if (id == principal.getId()) {
            return "redirect:/home";
        }
        else {
            User owner = usersRepo.findById(id).orElse(null);
            if (owner != null) {
                List<Message> foreignMsgs = null;
                if (filter != null && !filter.isEmpty()) {
                    foreignMsgs = messagesRepo.findMessagesByTagAndAuthorUsername(filter, owner.getUsername());

                }
                else {
                    foreignMsgs = owner.getMessages();
                }
                model.addAttribute("foreignMsgs", foreignMsgs);
                model.addAttribute("owner", owner);
                return "foreignhome";
            }
            else {
                return "redirect:/home";
            }
        }
    }

    @GetMapping("/profile/{id}/details")
    public String showOtherDetails(@PathVariable("id") int id,
                                   @AuthenticationPrincipal User principal,
                                   Model model) {
        User owner = usersRepo.findById(id).orElse(null);
        if (owner != null) {
            if (owner.getId() == principal.getId()) {
                return "redirect:/profile";
            }
            else {
                model.addAttribute("owner", owner);
                return "otherdetails";
            }
        }
        else {
            return "redirect:/home";
        }
    }

    @GetMapping("/edit/{id}")
    public String editProfileGet(@PathVariable("id") int id,
                                 @AuthenticationPrincipal User principal,
                                 Model model) {
        User user = usersRepo.findById(id).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("owner", principal.getId()==user.getId());
            return "editprofile";
        }
        else {
            return "redirect:/home";
        }
    }

    @PostMapping("/edit/{id}")
    public String editProfilePost(@PathVariable("id") int id,
                                  @RequestParam("username") String username,
                                  @RequestParam("firstName") String firstName,
                                  @RequestParam("lastName") String lastName,
                                  @RequestParam("email") String email,
                                  @RequestParam("phoneNumber") String phoneNumber,
                                  @RequestParam("password") String password) {
        User user = usersRepo.findById(id).get();
        if (username != null && !username.isEmpty()) user.setUsername(username);
        if (firstName != null && !firstName.isEmpty()) user.setFirstName(firstName);
        if (lastName != null && !lastName.isEmpty()) user.setLastName(lastName);
        if (email != null && !email.isEmpty()) user.setEmail(email);
        if (phoneNumber != null && !phoneNumber.isEmpty()) user.setPhoneNumber(phoneNumber);
        if (password != null && !password.isEmpty()) user.setPassword(password);
        usersRepo.save(user);
        return "redirect:/profile";
    }

}
