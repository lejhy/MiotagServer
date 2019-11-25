package Miotag.service;

import Miotag.dto.MessageDto;

import java.util.List;

public interface IMessageService {

    List<MessageDto> getMessages(String name);

    MessageDto sendMessage(MessageDto messageDto, String name);
}
