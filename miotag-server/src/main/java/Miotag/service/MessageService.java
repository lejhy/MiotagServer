package Miotag.service;

import Miotag.dto.MessageDto;
import Miotag.exception.UserNotFoundException;
import Miotag.mapper.MessageMapper;
import Miotag.model.Message;
import Miotag.model.User;
import Miotag.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService implements IMessageService {

    private final MessageRepository messageRepository;
    private final IUserService userService;
    private final MessageMapper messageMapper;


    @Autowired
    public MessageService(MessageRepository messageRepository, IUserService userService, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.messageMapper = messageMapper;
    }

    @Override
    public List<MessageDto> getMessages(User user) {
        List<Message> messages = messageRepository.findAllByToOrFrom(user, user);
        return messages.stream().map(messageMapper::map).collect(Collectors.toList());
    }

    @Override
    public MessageDto sendMessage(MessageDto messageDto, User user) {
        long recipientId = messageDto.getTo().getId();
        if(!userService.userExists(recipientId)) {
            throw new UserNotFoundException(recipientId);
        }
        Message message = prepareNewMessage(messageDto, user);
        Message savedMessage = messageRepository.save(message);
        return messageMapper.map(savedMessage);
    }

    private Message prepareNewMessage(MessageDto messageDto, User user) {
        Message message = messageMapper.map(messageDto);
        message.setId(0);
        message.setFrom(user);
        message.setDate(new Date());
        return message;
    }
}
