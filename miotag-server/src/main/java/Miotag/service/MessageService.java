package Miotag.service;

import Miotag.dto.MessageDto;
import Miotag.dto.UserDto;
import Miotag.exception.UserNotFoundException;
import Miotag.mapper.MessageMapper;
import Miotag.model.Message;
import Miotag.model.User;
import Miotag.repository.MessageRepository;
import Miotag.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService implements IMessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final ISecurityService securityService;


    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository userRepository, MessageMapper messageMapper, ISecurityService securityService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageMapper = messageMapper;
        this.securityService = securityService;
    }

    @Override
    public List<MessageDto> getMessages(String email) {
        User user = securityService.findUser(email);
        List<Message> messages = messageRepository.findAllByToOrFrom(user, user);
        return messages.stream().map(messageMapper::map).collect(Collectors.toList());
    }

    @Override
    public MessageDto sendMessage(MessageDto messageDto, String email) {
        long recipientId = messageDto.getTo().getId();
        if(!userRepository.existsById(recipientId)) {
            throw new UserNotFoundException(recipientId);
        }
        Message message = prepareNewMessage(messageDto, email);
        Message savedMessage = messageRepository.save(message);
        return messageMapper.map(savedMessage);
    }

    private Message prepareNewMessage(MessageDto messageDto, String email) {
        Message message = messageMapper.map(messageDto);
        message.setId(0);
        message.setFrom(securityService.findUser(email));
        message.setDate(new Date());
        return message;
    }
}
