package Miotag.repository;

import Miotag.model.Activity;
import Miotag.model.ActivityLog;
import Miotag.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findAllByUser(User user);
    List<ActivityLog> findAllByUserAndActivity(User user, Activity activity);
}
