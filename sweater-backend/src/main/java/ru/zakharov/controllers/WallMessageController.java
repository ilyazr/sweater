package ru.zakharov.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.zakharov.dto.WallMessageDTO;
import ru.zakharov.models.Like;
import ru.zakharov.models.User;
import ru.zakharov.models.WallMessage;
import ru.zakharov.models.WallMessageComment;
import ru.zakharov.services.WallMessageService;
import ru.zakharov.utils.ControllersUtil;
import ru.zakharov.utils.PrincipalUtil;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/wall")
@AllArgsConstructor
public class WallMessageController {

    private final WallMessageService wallMessageService;

    @PostMapping("/message")
    public ResponseEntity<?> addNewWallMessage(@Valid @RequestBody WallMessage wallMessage) {
        WallMessage saved = wallMessageService.addNewWallMessage(wallMessage);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/message/{id}")
    public ResponseEntity<?> deleteWallMessage(@PathVariable(name = "id") int id) {
        WallMessage deletedMsg = wallMessageService.deleteWallMessage(id);
        return ResponseEntity.ok(Map.of("Deleted message", deletedMsg));
    }

    @PutMapping("/message/{id}")
    public ResponseEntity<?> editMessage(@PathVariable(name = "id") int id,
                                         @RequestBody WallMessage newMsg) {
        WallMessage editedMsg = wallMessageService.editMessage(id, newMsg);
        return ResponseEntity.ok(editedMsg);
    }

    @GetMapping("/messages/{username}")
    public ResponseEntity<?> getAllWallMessagesByUsernameWithAdditionalDataForPrincipal(@PathVariable(name = "username")
                                                                                        String username) {
        List<WallMessageDTO> wallMessages =
                wallMessageService.getAllWallMessagesByUsernameWithAdditionalData(username);
        return ResponseEntity.ok(ControllersUtil.composeResponse("amount", wallMessages.size(),
                "wallMessages", wallMessages));
    }

    @GetMapping("/messages/count")
    public ResponseEntity<?> amountOfWallMessagesByUserId(@RequestParam(name = "userId") int userId) {
        return ResponseEntity.ok(wallMessageService.amountOfWallMessagesByUserId(userId));
    }

    @GetMapping("/comments/{msgId}")
    public ResponseEntity<?> getAllCommentsOfWallMessage(@PathVariable(name = "msgId") int id) {
        List<WallMessageComment> allCommentsOfWallMessage =
                wallMessageService.getAllCommentsOfWallMessage(id);
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "wallMsgId", id,
                "comments", allCommentsOfWallMessage
        ));
    }

    @GetMapping("/comments/user/{userId}")
    public ResponseEntity<?> getAllCommentsOfUser(@PathVariable(name = "userId") int id) {
        List<WallMessageComment> comments = wallMessageService.getAllCommentsOfUser(id);
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "authorId", id,
                "amount", comments.size(),
                "comments", comments
        ));
    }

    @PostMapping("/comments/{msgId}")
    public ResponseEntity<?> addCommentToWallMessage(@PathVariable(name = "msgId") int msgId,
                                                     @Valid @RequestBody WallMessageComment comment) {
        WallMessageComment addedComment =
                wallMessageService.addCommentToWallMessage(msgId, comment);
        int principalId = PrincipalUtil.getPrincipalId();
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                    "authorId", principalId,
                    "wallMsgId", msgId,
                    "comment", addedComment
            ));
    }

    @DeleteMapping("/comments/{cmtId}")
    public ResponseEntity<?> delCommentOfWallMessage(@PathVariable(name = "cmtId") int cmtId) {
        WallMessageComment comment = wallMessageService.delCommentOfWallMessage(cmtId);
        return ResponseEntity.ok(Map.of(
                "deletedComment", comment
        ));
    }

    @PostMapping("/message/{id}/like")
    public ResponseEntity<?> addLike(@PathVariable(name = "id") int id) {
        Like like = wallMessageService.addLike(id);
        Set<WallMessageComment> comments = like.getPost().getComments();
        return ResponseEntity.ok(like);
    }

    @DeleteMapping("/message/{id}/like")
    public ResponseEntity<?> removeLike(@PathVariable(name = "id") int msgId) {
        wallMessageService.removeLike(msgId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/message/{postId}/like")
    public ResponseEntity<?> processLikeOfPrincipal(@PathVariable(name = "postId") int postId) {
        boolean isLiked = wallMessageService.isUserLikedThePost(PrincipalUtil.getPrincipalId(), postId);
        if (isLiked) {
            wallMessageService.removeLike(postId);
            return ResponseEntity.ok(ControllersUtil.composeResponse("operation", "remove"));
        } else return ResponseEntity.ok(ControllersUtil.composeResponse(
                "operation", "add",
                "like", wallMessageService.addLike(postId)
        ));
    }

    @GetMapping("/message/{id}/likes")
    public ResponseEntity<?> getAllLikesOfWallMessage(@PathVariable(name = "id") int msgId) {
        Set<Like> likes = wallMessageService.getAllLikesOfWallMessage(msgId);
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "msgId", msgId,
                "quantity", likes.size(),
                "likes", likes
        ));
    }

}
