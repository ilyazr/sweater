package ru.zakharov.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.zakharov.models.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
    boolean existsByUsernameAndEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query(value = "select id from users where username=:username", nativeQuery = true)
    int getUserIdByUsername(String username);

    @Query(value = "select exists(select 1 " +
            "from users " +
                "where username=:username " +
                    "and password=:password " +
                    "and email=:email " +
                    "and enabled=false)",
            nativeQuery = true)
    boolean isUserRegisteredButNotConfirmed(String username, String password, String email);

    @Query(value = "select u.*, a.avatar_uri from users u " +
            "left join users_avatars a on u.id = a.user_id where u.id=:userId",
            nativeQuery = true)
    Optional<User> getUserWithAvatarUri(int userId);

    @Query(value = "select u.*, a.avatar_uri from users u " +
            "left join users_avatars a on u.id = a.user_id;", nativeQuery = true)
    List<User> getAllUsersWithAvatar();

    @Query(value = "select u.*, a.avatar_uri from users u " +
            "left join users_avatars a on u.id = a.user_id where u.username=:username",
            nativeQuery = true)
    Optional<User> getUserWithAvatarUriByUsername(String username);

    @Query(value = "select u.*, ua.avatar_uri from users u " +
            "left join users_avatars ua on u.id = ua.user_id " +
            "right join chat_room_users cru on u.id = cru.user_id " +
            "where cru.chat_room_id=:chatId",
            nativeQuery = true)
    List<User> getAllMembersOfChatRoom(int chatId);


    @Query(value = "select u.*, a.avatar_uri from users u " +
            "left join users_avatars a on u.id = a.user_id " +
            "where id in (select friend_id from friendship where user_id=:userId and status = 'accepted')",
            nativeQuery = true)
    List<User> getAllFriends(@Param("userId") int userId);

    @Query(value = "select u.*, a.avatar_uri from users u " +
            "left join users_avatars a on u.id = a.user_id " +
            "where id in (select user_id from friendship where friend_id=:userId and status = 'requested')",
            nativeQuery = true)
    List<User> getAllRequestsOfFriendship(@Param("userId") int userId);

    @Query(value = "select u.*, a.avatar_uri from users u " +
            "left join users_avatars a on u.id = a.user_id " +
            "where id in (select friend_id from friendship where user_id=:userId and status = 'requested')",
            nativeQuery = true)
    List<User> getAllOutgoingRequestsOfFriendship(int userId);

    @Query(value = "select f1.friend_id from friendship f1 " +
            "inner join friendship f2 on f1.user_id = f2.friend_id and f1.friend_id = f2.user_id " +
            "where f1.user_id = :userId",
            nativeQuery = true)
    List<Integer> getIdOfAllFriendsOfUser(@Param("userId") int userId);

    @Query(value = "select friend_id from friendship where user_id=:userId and status = 'requested'",
            nativeQuery = true)
    List<Integer> getIdOfAllSentRequestsOfFriendship(int userId);

    @Query(value = "select user_id from friendship where friend_id=:userId and status = 'requested'",
            nativeQuery = true)
    List<Integer> getIdOfAllReceivedRequestsOfFriendship(@Param("userId") int userId);

    @Query(value = "select exists(select 1 from friendship where user_id=:principalId and status='requested')",
            nativeQuery = true)
    boolean checkForSentRequestsOfFriendship(int principalId);

    @Query(value = "select exists(select 1 from friendship " +
            "where friend_id=:user2 " +
            "and user_id=:user1 " +
            "and status='requested')",
            nativeQuery = true)
    boolean checkForReceivedRequestsBetweenUsers(int user1, int user2);

    @Query(value = "select exists(select 1 from friendship " +
            "where friend_id=:userId " +
            "and status='requested')",
            nativeQuery = true)
    boolean checkForReceivedRequestsOfFriendship(int userId);

    @Query(value = "select exists(select 1 from friendship where user_id=:sId and friend_id=:rId and status='accepted')",
            nativeQuery = true)
    boolean checkForFriendship(@Param("sId") int senderId,
                               @Param("rId") int receiverId);

    @Query(value = "select count(*) from friendship where user_id = :userId and status='accepted'",
            nativeQuery = true)
    int checkForFriends(@Param("userId") int userId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "insert into friendship (user_id, friend_id, status) values " +
            "(:userId, :friendId, 'requested')",
            nativeQuery = true)
    int requestFriendship(int userId, int friendId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from friendship where user_id=:user1Id and " +
            "friend_id=:user2Id and status='requested'",
            nativeQuery = true)
    int deleteRequestFriendship(int user1Id, int user2Id);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "insert into friendship (user_id, friend_id, status) values (:friendId, :principalId, 'accepted'), (:principalId, :friendId, 'accepted')",
            nativeQuery = true)
    int addNewFriend(int principalId, int friendId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from friendship where user_id=:u1 and friend_id=:u2",
            nativeQuery = true)
    void deleteFriend(@Param("u1") int u1, @Param("u2") int u2);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update users set enabled = true where email=:email",
            nativeQuery = true)
    int enableUser(String email);

    @Query(value = "select count(*) from friendship where user_id=:userId and status = 'accepted'",
            nativeQuery = true)
    int amountOfFriendsByUserId(int userId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update users_avatars set avatar_uri = :avatarURI where user_id = :userId",
            nativeQuery = true)
    int changeAvatarUri(int userId, String avatarURI);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "insert into users_avatars (avatar_uri, user_id) values (:avatarURI, :userId)",
            nativeQuery = true)
    int setAvatarUri(int userId, String avatarURI);

    @Query(value = "select exists(select 1 from users_avatars where user_id = :userId)",
            nativeQuery = true)
    boolean isAvatarSet(int userId);

    @Query(value = "select exists(select 1 from friendship " +
            "where user_id = :outUserId and friend_id = :inUserId and status = 'requested')",
            nativeQuery = true)
    boolean checkOutgoingFriendshipRequest(int outUserId, int inUserId);

}
