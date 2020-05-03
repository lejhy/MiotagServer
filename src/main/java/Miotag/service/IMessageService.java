package Miotag.service;

import Miotag.dto.MessageDto;
import Miotag.model.User;

import java.util.List;

public interface IMessageService {

    List<MessageDto> getMessages(User user);

    MessageDto sendMessage(MessageDto messageDto, User user);
}
