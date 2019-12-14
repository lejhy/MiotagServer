package Miotag.repository;

import Miotag.model.Alert;
import Miotag.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByUser(User user);
}
