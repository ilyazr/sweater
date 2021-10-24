package ru.zakharov.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.zakharov.services.FriendshipService;
import ru.zakharov.utils.ControllersUtil;
import ru.zakharov.utils.PrincipalUtil;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/friends")
@AllArgsConstructor
@Slf4j
public class FriendshipController {

    private final FriendshipService friendshipService;

    @GetMapping
    public ResponseEntity<?> getAllFriendsByUsernameOrId(
            @RequestParam(name = "userId", required = false) Integer userId,
            @RequestParam(name = "username", required = false) String username
    ) {
        return ResponseEntity.ok(friendshipService.getAllFriends(userId, username));
    }

    @GetMapping("/checkStatus")
    public ResponseEntity<?> checkFriendshipStatus(@RequestParam(name = "userId") int userId) {
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "friendshipStatus", friendshipService.checkFriendshipStatus(userId)
        ));
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addFriend(@PathVariable(name = "userId") int userId) {
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "friendshipStatus", friendshipService.addFriend(userId)
        ));
    }

    @DeleteMapping("/del/{userId}")
    public ResponseEntity<?> deleteFriend(@PathVariable(name = "userId") int userId,
                                          @RequestParam(name = "status") String status) {
        friendshipService.deleteFriend(userId, status);
        return ResponseEntity.ok(Map.of(
                "idOfDeletedFriend", userId
        ));
    }

    @GetMapping("/accepted")
    public ResponseEntity<?> getIdsOfAllFriendsOfPrincipal() {
        List<Integer> friendsIds =
                friendshipService.getIdOfAllFriendsOfUser(PrincipalUtil.getPrincipalId());
        return ResponseEntity.ok(Map.of(
                "friendsIds", friendsIds
        ));
    }

    @GetMapping("/requested/sent")
    public ResponseEntity<?> getSentRequestedFriendshipUserIds() {
        int id = PrincipalUtil.getPrincipalId();
        List<Integer> friendsIds = friendshipService.getSentRequestedFriendshipUserIds(id);
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "quantity", friendsIds.size(),
                "userId", id,
                "requestedUsersIds", friendsIds
        ));
    }

    @GetMapping("/requested/received")
    public ResponseEntity<?> getReceivedRequestsOfFriendship() {
        List<Integer> receivedReqs = friendshipService
                .getReceivedRequestedFriendshipUserIds(PrincipalUtil.getPrincipalId());
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "quantity", receivedReqs.size(),
                "requesterIds", receivedReqs
        ));
    }

    @GetMapping("/accepted/{userId}")
    public ResponseEntity<?> getIdsOfAllFriendsOfUser(@PathVariable(name = "userId") int userId) {
        List<Integer> friendsIds = friendshipService.getIdOfAllFriendsOfUser(userId);
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "userId", userId,
                "friendsIds", friendsIds
        ));
    }

    @GetMapping("/requested/sent/{userId}")
    public ResponseEntity<?> getSentRequestedFriendshipUserIds(@PathVariable(name = "userId") int userId) {
        List<Integer> friendsIds = friendshipService.getSentRequestedFriendshipUserIds(userId);
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "quantity", friendsIds.size(),
                "userId", userId,
                "requestedUsersIds", friendsIds
        ));
    }

    @GetMapping("/requested/received/{userId}")
    public ResponseEntity<?> getReceivedRequestsOfFriendship(@PathVariable(name = "userId") int userId) {
        List<Integer> receivedReqs = friendshipService.getReceivedRequestedFriendshipUserIds(userId);
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "userId", userId,
                "quantity", receivedReqs.size(),
                "requesterIds", receivedReqs
        ));
    }

    @GetMapping("/count")
    public ResponseEntity<?> amountOfFriendsByUserId(
            @RequestParam(name = "userId", required = false) Integer userId,
            @RequestParam(name = "username", required = false) String username) {
        return ResponseEntity.ok(friendshipService.amountOfFriends(userId, username));
    }

}
