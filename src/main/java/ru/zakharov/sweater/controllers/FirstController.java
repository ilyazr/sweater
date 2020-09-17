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

import java.util.Calendar;
import java.util.List;

@Controller
public class FirstController {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private MessagesRepo messagesRepo;

    @GetMapping("/")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name,
                           Model model) {
        model.addAttribute("name", name);
        return "starter";
    }

    @GetMapping("/home")
    public String homePage(@AuthenticationPrincipal User principal,
                           @RequestParam(name = "filter", required = false) String filter,
                           Model model) {
        User user = usersRepo.findById(principal.getId()).get();
        List<Message> messages = null;
        model.addAttribute("user", user);
        if (filter != null && !filter.isEmpty()) {
            messages = messagesRepo.findMessagesByTagAndAuthorUsername(filter, user.getUsername());

        }
        else {
            messages = user.getMessages();
        }
        model.addAttribute("messages", messages);
        return "home";
    }

    @GetMapping("/create/message")
    public String createMessageGet(Model model) {
        model.addAttribute("newMessage", new Message());
        return "createmsg";
    }

    @PostMapping("/create/message")
    public String createMessagePost(@ModelAttribute("newMessage") Message message,
                                    @AuthenticationPrincipal User principal) {
        User user = usersRepo.findById(principal.getId()).get();
        message.setCreated_at(Calendar.getInstance());
        user.addMessage(message);
        usersRepo.save(user);
        return "redirect:/home";
    }

    @GetMapping("/users")
    public String allUsersOnTheServer(@AuthenticationPrincipal User user,
                                      Model model) {
        List<User> allUsers = usersRepo.findAll();
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("currentUser", user);
        return "users";
    }

    @GetMapping("/add/friend/{id}")
    public String addFriendGet(@PathVariable("id") int id) {
        return null;
    }

}
