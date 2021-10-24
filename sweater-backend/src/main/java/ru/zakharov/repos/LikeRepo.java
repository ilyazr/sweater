package ru.zakharov.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.zakharov.models.Like;

import javax.transaction.Transactional;

public interface LikeRepo extends JpaRepository<Like, Integer> {
    @Query(value = "select exists(select 1 from likes where owner_id=:userId and post_id=:msgId)",
            nativeQuery = true)
    boolean isLikeAlreadyExist(@Param("userId") int userId,@Param("msgId") int msgId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from likes where owner_id=:ownerId and post_id=:msgId",
            nativeQuery = true)
    void deleteByOwnerIdAndMsgId(@Param("ownerId") int ownerId, @Param("msgId") int msgId);
}
