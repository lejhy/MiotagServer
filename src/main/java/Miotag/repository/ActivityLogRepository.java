package Miotag.repository;

import Miotag.model.ActivityLog;
import Miotag.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findAllByUser(User user);
    List<ActivityLog> findAllByUserAndActivityId(User user, long activityId);
    List<ActivityLog> findAllByUserId(long id);
    List<ActivityLog> findAllByUserIdAndActivityId(long id, long activityId);
}
