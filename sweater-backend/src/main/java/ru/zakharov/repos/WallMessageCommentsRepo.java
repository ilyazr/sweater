package ru.zakharov.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.zakharov.models.WallMessageComment;

import java.util.List;

public interface WallMessageCommentsRepo extends JpaRepository<WallMessageComment, Integer> {

    @Query(value = "select * from wall_msg_comments cmt where cmt.wall_msg_id = :msgId",
            nativeQuery = true)
    List<WallMessageComment> getAllCommentsByWallMessageId(@Param("msgId") int id);

    @Query(value = "select * from wall_msg_comments cmt where cmt.author_id = :authorId",
            nativeQuery = true)
    List<WallMessageComment> getAllByAuthorId(int authorId);
}
