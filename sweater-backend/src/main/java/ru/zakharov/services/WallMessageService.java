package ru.zakharov.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.zakharov.dto.WallMessageDTO;
import ru.zakharov.exceptions.LikeException;
import ru.zakharov.exceptions.UserNotFoundException;
import ru.zakharov.exceptions.WallMessageException;
import ru.zakharov.exceptions.WallMessageException.ExceptionType;
import ru.zakharov.models.Like;
import ru.zakharov.models.User;
import ru.zakharov.models.WallMessage;
import ru.zakharov.models.WallMessageComment;
import ru.zakharov.repos.LikeRepo;
import ru.zakharov.repos.UserRepo;
import ru.zakharov.repos.WallMessageCommentsRepo;
import ru.zakharov.repos.WallMessageRepo;
import ru.zakharov.utils.PrincipalUtil;

import javax.persistence.Tuple;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class WallMessageService {

    private final UserRepo userRepo;
    private final WallMessageRepo wallMessageRepo;
    private final WallMessageCommentsRepo commentsRepo;
    private final LikeRepo likeRepo;
    private final UserService userService;

    private int getPrincipalId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public WallMessage addNewWallMessage(WallMessage wallMessage) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findUserByUsername(name)
                .orElseThrow(() -> new UserNotFoundException(name));
        System.out.println(user.getId());
        wallMessage.setCreatedAt(Calendar.getInstance());
        wallMessage.setUpdatedAt(Calendar.getInstance());
        wallMessage.setAuthor(user);
        wallMessage.setAuthorUsername(user.getUsername());
        wallMessage.setAuthorId(user.getId());
        wallMessage.setAuthorFirstName(user.getFirstName());
        wallMessage.setAuthorLastName(user.getLastName());
        return wallMessageRepo.save(wallMessage);
    }

    public WallMessage deleteWallMessage(int id) {
        WallMessage message = wallMessageRepo
                .findById(id)
                .orElseThrow(() ->
                        new WallMessageException(String.format("Message with id %d not found!", id)));
        wallMessageRepo.delete(message);
        return message;
    }

    public WallMessage editMessage(int id, WallMessage newMsg) {
        WallMessage message = wallMessageRepo
                .findById(id)
                .orElseThrow(() ->
                        new WallMessageException(id, ExceptionType.NOT_FOUND));
        newMsg.setUpdatedAt(Calendar.getInstance());
        BeanUtils.copyProperties(newMsg, message,
                "id", "imgURI", "createdAt", "author");
        WallMessage saved = wallMessageRepo.save(message);
        User author = message.getAuthor();
        saved.setAuthorId(author.getId());
        saved.setAuthorUsername(author.getUsername());
        return saved;
    }

    private List<WallMessageDTO> mapWallMessagesToDtoWithLikes(Stream<Tuple> messages) {
        return messages.map(t -> new WallMessageDTO(
                        t.get(0, Integer.class),
                        t.get(1, Timestamp.class),
                        t.get(2, String.class),
                        t.get(3, String.class),
                        t.get(4, Timestamp.class),
                        t.get(5, Integer.class),
                        t.get(6, Boolean.class),
                        t.get(7, String.class),
                        t.get(8, String.class),
                        t.get(9, String.class),
                        t.get(10, BigInteger.class).intValue()
                )).collect(Collectors.toList());
    }

    public List<WallMessageDTO> getAllWallMessagesByUsernameWithAdditionalData(String username) {
        List<Tuple> messages;
        User principal = PrincipalUtil.getPrincipalAsUser();
        if (username.equals(principal.getUsername())) {
            messages = wallMessageRepo.getWallMessagesByAuthorIdWitAdditionalDataForPrincipal(principal.getId());
        } else {
            messages = wallMessageRepo.getWallMessagesByAuthorIdWitAdditionalDataForOther(
                    principal.getId(),
                    userService.getUserIdByUsername(username));
        }
        return mapWallMessagesToDtoWithLikes(messages.stream());
    }

    public List<WallMessageComment> getAllCommentsOfWallMessage(int id) {
        boolean exist = wallMessageRepo.existsById(id);
        if (exist) {
            return commentsRepo.getAllCommentsByWallMessageId(id);
        }
        else {
            throw new WallMessageException(id, ExceptionType.NOT_FOUND);
        }
    }

    public List<WallMessageComment> getAllCommentsOfUser(int id) {
        boolean userExist = userRepo.existsById(id);
        if (!userExist) {
            throw new UserNotFoundException(id);
        } else {
            return commentsRepo.getAllByAuthorId(id);
        }
    }

    public WallMessageComment addCommentToWallMessage(int id, WallMessageComment comment) {
        boolean exist = wallMessageRepo.existsById(id);
        if (!exist) {
            throw new WallMessageException(id, ExceptionType.NOT_FOUND);
        } else {
            int principalId = PrincipalUtil.getPrincipalId();
            User principal = userRepo.findById(principalId)
                    .orElseThrow(() -> new UserNotFoundException(principalId));
            WallMessage message = wallMessageRepo.getOne(id);
            comment.setAuthor(principal);
            comment.setWallMessage(message);
            comment.setCreatedAt(Calendar.getInstance());
            return commentsRepo.save(comment);
        }
    }

    public WallMessageComment delCommentOfWallMessage(int cmtId) {
        WallMessageComment comment = commentsRepo
                .findById(cmtId)
                .orElseThrow(() -> new WallMessageException(
                        cmtId, ExceptionType.NOT_FOUND
                ));
        commentsRepo.delete(comment);
        return comment;
    }

    public boolean isUserLikedThePost(int userId, int postId) {
        return wallMessageRepo.isUserLikedThePost(userId, postId);
    }

    public Like addLike(int msgId) {
        int idOfPrincipal = PrincipalUtil.getPrincipalId();
        if (likeRepo.isLikeAlreadyExist(idOfPrincipal, msgId)) {
            throw new LikeException("Like already exist");
        } else {
            WallMessage msg = wallMessageRepo
                    .findById(msgId)
                    .orElseThrow(() -> new WallMessageException(msgId, ExceptionType.NOT_FOUND));
            User principal = userRepo
                    .findById(idOfPrincipal)
                    .orElseThrow(() -> new UserNotFoundException(idOfPrincipal));
            Like like = new Like(principal, msg);
            return likeRepo.save(like);
        }
    }

    public void removeLike(int msgId) {
        boolean msgExists = wallMessageRepo.existsById(msgId);
        if (msgExists) {
            int principalId = getPrincipalId();
            likeRepo.deleteByOwnerIdAndMsgId(principalId, msgId);
        } else {
            throw new WallMessageException(msgId, ExceptionType.NOT_FOUND);
        }
    }

    public Set<Like> getAllLikesOfWallMessage(int msgId) {
        WallMessage message = wallMessageRepo
                .findById(msgId)
                .orElseThrow(() -> new WallMessageException(msgId, ExceptionType.NOT_FOUND));
        return message.getLikes();
    }

    public int amountOfWallMessagesByUserId(int userId) {
        return wallMessageRepo.amountOfWallMessagesByUserId(userId);
    }

}
