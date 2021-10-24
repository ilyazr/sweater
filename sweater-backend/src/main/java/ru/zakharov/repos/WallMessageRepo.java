package ru.zakharov.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.zakharov.models.WallMessage;

import javax.persistence.Tuple;
import java.util.List;

public interface WallMessageRepo extends JpaRepository<WallMessage, Integer> {
    @Query(value = "select count(*) from likes l where l.post_id = :msgId",
            nativeQuery = true)
    int getAmountOfLikes(int msgId);

    @Query(value = "select * from wall_messages where author_id=:authorId order by created_at desc",
            nativeQuery = true)
    List<WallMessage> getWallMessagesByAuthorId(int authorId);

    // for principal
    @Query(value = "select wm.*, " +
            "       (select exists(select 1 from likes where owner_id=:authorId and post_id = wm.id)) as isLiked, " +
            "       u.first_name, " +
            "       u.last_name, " +
            "       u.username, " +
            "       (select count(*) from likes where post_id = wm.id) as amountOfLikes " +
            "from wall_messages wm " +
            "    left join users u on wm.author_id = u.id " +
            "where wm.author_id=:authorId order by created_at desc",
            nativeQuery = true)
    List<Tuple> getWallMessagesByAuthorIdWitAdditionalDataForPrincipal(int authorId);

    // for other users
    @Query(value = "select wm.*, " +
            "       (select exists(select 1 from likes where owner_id=:principalId and post_id = wm.id)) as isLiked, " +
            "       u.first_name, " +
            "       u.last_name, " +
            "       u.username, " +
            "       (select count(*) from likes where post_id = wm.id) as amountOfLikes " +
            "from wall_messages wm " +
            "    left join users u on wm.author_id = u.id " +
            "where wm.author_id=:authorId order by created_at desc",
            nativeQuery = true)
    List<Tuple> getWallMessagesByAuthorIdWitAdditionalDataForOther(int principalId, int authorId);

    @Query(value = "select exists(select 1 from likes where owner_id=:userId and post_id=:postId)", nativeQuery = true)
    boolean isUserLikedThePost(int userId, int postId);

    @Query(value = "select count(*) from wall_messages where author_id=:userId",
            nativeQuery = true)
    int amountOfWallMessagesByUserId(int userId);
}
