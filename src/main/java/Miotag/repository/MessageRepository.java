package Miotag.repository;

import Miotag.model.Message;
import Miotag.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    //TODO pagination
    List<Message> findAllByToOrFrom(User to, User from);
}
