package dev.aniket.Instagram_api.dao;

import dev.aniket.Instagram_api.model.Story;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StoryDao extends JpaRepository<Story, String> {
    @Query("SELECT s FROM Story s WHERE s.user.id = :userId")
    List<Story> findAllStoryByUserId(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Story s WHERE s.timestamp < :cutoffDate")
    void deleteStoriesAutomaticallyAfter24Hours(@Param("cutoffDate") LocalDateTime cutoff);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM users_stories Where stories_id = :storyId", nativeQuery = true)
    void deleteUsers_storiesRow(@Param("storyId") String storyId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Story u where u.id = :storyId")
    void deleteStory(@Param("storyId") String storyId);

    @Query("SELECT s FROM Story s WHERE s.timestamp < :cutoffDate")
    List<Story> findAllStoryBeforeTime(@Param("cutoffDate") LocalDateTime cutoff);
}
