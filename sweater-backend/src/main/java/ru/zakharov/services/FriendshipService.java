package ru.zakharov.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.zakharov.dto.FriendshipDTO;
import ru.zakharov.exceptions.FriendshipException;
import ru.zakharov.exceptions.UserNotFoundException;
import ru.zakharov.models.FriendshipStatuses;
import ru.zakharov.models.User;
import ru.zakharov.repos.LikeRepo;
import ru.zakharov.repos.UserRepo;
import ru.zakharov.repos.WallMessageCommentsRepo;
import ru.zakharov.repos.WallMessageRepo;
import ru.zakharov.utils.PrincipalUtil;
import ru.zakharov.utils.TransactionHandler;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FriendshipService {

    private final UserRepo userRepo;
    private final UserService userService;
    private final WallMessageRepo wallMessageRepo;
    private final WallMessageCommentsRepo commentsRepo;
    private final LikeRepo likeRepo;
    private final TransactionHandler transactionHandler;
    private final EntityManager em;
    private final ChatService chatService;

    private void ifUserNotExistsThrowExc(int userId) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    public List<Integer> getIdOfAllFriendsOfUser(int userId) {
        ifUserNotExistsThrowExc(userId);
        if (userRepo.checkForFriends(userId) < 1) {
            throw new FriendshipException("You have no friends");
        }
        return userRepo.getIdOfAllFriendsOfUser(userId);
    }

    public List<Integer> getSentRequestedFriendshipUserIds(int principalId) {
        ifUserNotExistsThrowExc(principalId);
        if (!userRepo.checkForSentRequestsOfFriendship(principalId)) {
            throw new FriendshipException(String.format("User with id %d have no sent requests",
                    principalId));
        }
        return userRepo.getIdOfAllSentRequestsOfFriendship(principalId);
    }

    public List<Integer> getReceivedRequestedFriendshipUserIds(int userId) {
        ifUserNotExistsThrowExc(userId);
        if (!userRepo.checkForReceivedRequestsOfFriendship(userId)) {
            throw new FriendshipException(String.format("User with id %d have no received requests",
                    userId));
        }
        return userRepo.getIdOfAllReceivedRequestsOfFriendship(userId);
    }

    public boolean checkFriendship(int potentialFriendId) {
        return userRepo
                .checkForFriendship(PrincipalUtil.getPrincipalId(), potentialFriendId);
    }

    public String checkFriendshipStatus(int userId) {
        if (userRepo.checkForReceivedRequestsBetweenUsers(PrincipalUtil.getPrincipalId(), userId)) {
            return FriendshipStatuses.OUTGOING_REQUEST.getStatus();
        } else {
            try {
                String sql = "select f.status from friendship f where (user_id = :userId and friend_id = :principalId) or " +
                        "(user_id = :principalId and friend_id = :userId) limit 1";
                Query query = em.createNativeQuery(sql);
                query.setParameter("userId", userId);
                query.setParameter("principalId", PrincipalUtil.getPrincipalId());
                return  (String) query.getSingleResult();
            } catch (Exception e) {
                return FriendshipStatuses.NOT_EXISTS.getStatus();
            }
        }
    }

    public int acceptFriendshipRequestFrom(int userId) {
        final int principalId = PrincipalUtil.getPrincipalId();
        return transactionHandler.runInTransaction(() -> {
            userRepo.deleteRequestFriendship(userId, principalId);
            log.info("New friendship between {} and {}", principalId, userId);
            return userRepo.addNewFriend(principalId, userId);
        });
    }

    /**
     * @param userId - future friend
     * @return new friendship status
     */
    public String addFriend(int userId) {
        if (!userService.isUserExistsById(userId))
            throw new UserNotFoundException(userId);
        if (userRepo.checkForFriendship(PrincipalUtil.getPrincipalId(), userId))
            throw new FriendshipException("already friends");

        int rowsAffected = -1;
        FriendshipStatuses friendshipStatus = null;
        if (userRepo.checkForReceivedRequestsBetweenUsers(userId, PrincipalUtil.getPrincipalId())) {
            // accept request
            rowsAffected = acceptFriendshipRequestFrom(userId);
            friendshipStatus = FriendshipStatuses.ACCEPTED;

            // create new chat for users where userId send friendship request (owner)
            chatService.createChatRoom(2, userId, PrincipalUtil.getPrincipalId());
        } else {
            // send new request
            if (userRepo.checkOutgoingFriendshipRequest(PrincipalUtil.getPrincipalId(), userId))
                throw new FriendshipException("request already sent");
            else {
                rowsAffected = sendRequestOfFriendship(userId);
                friendshipStatus = FriendshipStatuses.OUTGOING_REQUEST;
            }
        }
        if (rowsAffected < 1) throw new FriendshipException("Error while adding a friend");
        return friendshipStatus.getStatus();
    }

    public int sendRequestOfFriendship(int userId) {
        return userRepo.requestFriendship(PrincipalUtil.getPrincipalId(), userId);
    }

    public void deleteFriend(int userId, String status) {
        userService.ifUserNotExistsByIdThrowUserNotFound(userId);

        if (status.equals(FriendshipStatuses.REQUESTED.getStatus())) {
            userRepo.deleteRequestFriendship(userId, PrincipalUtil.getPrincipalId());
        }
        else if (status.equals(FriendshipStatuses.ACCEPTED.getStatus())) {
            boolean friendship = checkFriendship(userId);
            int principalId = PrincipalUtil.getPrincipalId();
            log.debug("Delete friendship between principal (id {}) and other user (id {})", principalId, userId);
            if (friendship) {
                transactionHandler.runInTransaction(() -> {
                    userRepo.deleteFriend(principalId, userId);
                    userRepo.deleteFriend(userId, principalId);
                    log.info("Delete friendship between id {} and id {}", principalId, userRepo);
                    // delete chat between users
                    chatService.deleteChatBetweenUsers(principalId, userId);
                });
            } else {
                log.info("Friendship between id {} and id {} doesn't exist", principalId, userId);
                throw new FriendshipException(String.format(
                        "Friendship between users with id's of %d and %d doesn't exist",
                        principalId, userId
                ));
            }
        } else if (status.equals(FriendshipStatuses.OUTGOING_REQUEST.getStatus())) {
            userRepo.deleteRequestFriendship(PrincipalUtil.getPrincipalId(), userId);
        } else {
            throw new IllegalStateException("Wrong friendship status");
        }
    }

    protected List<User> getAllFriendsByUserId(int userId) {
        log.info("Get all friends of user with id={}", userId);
        return userRepo.getAllFriends(userId);
    }

    protected List<User> getAllRequestsOfFriendshipByUserId(int userId) {
        log.info("Get all friends of user with id={}", userId);
        return userRepo.getAllRequestsOfFriendship(userId);
    }

    protected List<User> getAllOutgoingRequestsOfFriendship(int userId) {
        log.info("Get all outgoing requests of friendship of user with id {}", userId);
        return userRepo.getAllOutgoingRequestsOfFriendship(userId);
    }

    private int amountOfFriendsByUserId(int userId) {
        return userRepo.amountOfFriendsByUserId(userId);
    }

    public int amountOfFriends(Integer userId, String username) {
        if (userId == null && username == null)
            throw new IllegalStateException("You must provide either userId or username ");
        return amountOfFriendsByUserId(Objects.requireNonNullElseGet(userId,
                () -> userService.getUserIdByUsername(username)));
    }

    public FriendshipDTO getAllFriends(Integer userId, String username) {
        if (userId == null && username == null)
            throw new IllegalStateException("You must provide either userId or username ");
        int id = Objects.requireNonNullElseGet(userId, () -> userService.getUserIdByUsername(username));
        List<User> accepted = getAllFriendsByUserId(id);
        List<User> requested = getAllRequestsOfFriendshipByUserId(id);
        List<User> outgoingRequests = getAllOutgoingRequestsOfFriendship(id);
        return FriendshipDTO.builder()
                .accepted(accepted.stream().map(userService::mapToDto).collect(Collectors.toList()))
                .requested(requested.stream().map(userService::mapToDto).collect(Collectors.toList()))
                .outgoingRequests(outgoingRequests.stream().map(userService::mapToDto).collect(Collectors.toList()))
                .build();
    }

}
