package ru.zakharov.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.zakharov.models.UploadedFile;
import ru.zakharov.models.User;
import ru.zakharov.services.UserService;
import ru.zakharov.utils.ControllersUtil;
import ru.zakharov.utils.PrincipalUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Enumeration;
import java.util.Map;

@Controller
@RequestMapping("/user")
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/get")
    public ResponseEntity<?> testGet() {
        return ResponseEntity.ok(userService.getUserWithAvatarUriById(PrincipalUtil.getPrincipalId()));
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<?> testGet(@PathVariable(name = "userId") int userId) {
        return ResponseEntity.ok(userService.getUserWithAvatarUriById(userId));
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getDataOfUser(@PathVariable(name = "username") String username) {
        return ResponseEntity.ok(userService.getUserWithAvatarUriByUsername(username));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(HttpServletRequest req,
                                     @Valid @RequestBody(required = true) User user) {
        readHeaders(req);
        User createdUser = userService.addUser(user);
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "createdUser", createdUser
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(Map.of(
                "users", userService.getAllUsersWithAvatar()
        ));
    }

    @GetMapping("/checkOnline")
    public ResponseEntity<?> isUserOnline(@RequestParam(name = "userId") int userId) {
        return ResponseEntity.ok(userService.isUserOnline(userId));
    }

    @PostMapping("/change")
    public ResponseEntity<?> changeUserData(@RequestParam(name = "property") String property,
                                            @RequestParam(name = "value") String value,
                                            HttpServletResponse resp) {
        int rowsAffected = userService.changeUserDataOfPrincipal(property, value);
        if (rowsAffected >= 1) {
            resp.setHeader("Authorization", userService.createNewJwtTokenForPrincipal());
            if (property.equals("username")) {
                return ResponseEntity.ok(ControllersUtil.composeResponse(
                        "property", property,
                        "newValue", value,
                        "newAvatarUri", userService.getAvatarURI(PrincipalUtil.getPrincipalId())));
            } else {
                return ResponseEntity.ok(ControllersUtil.composeResponse(
                        "property", property,
                        "newValue", value));
            }
        } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/changeAvatar")
    public ResponseEntity<?> changeUserAvatar(@RequestParam(name = "file") MultipartFile file,
                                              HttpServletResponse resp) {
        UploadedFile uploadedFile = userService.changeAvatarUriOfPrincipal(file);
        resp.setHeader("Authorization", userService.createNewJwtTokenForPrincipal());
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "property", "avatarURI",
                "newValue", uploadedFile.getDownloadURI()
        ));
    }

    public void readHeaders(HttpServletRequest req) {
        Enumeration<String> h = req.getHeaderNames();
        for (String name = h.nextElement(); h.hasMoreElements(); name = h.nextElement())
            System.out.format("[%s]: %s\n", name, req.getHeader(name));
    }

}
