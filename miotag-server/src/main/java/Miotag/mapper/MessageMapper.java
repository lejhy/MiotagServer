package Miotag.mapper;

import Miotag.dto.MessageDto;
import Miotag.model.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    private ModelMapper modelMapper;

    @Autowired
    public MessageMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.createTypeMap(MessageDto.class, Message.class)
                .addMappings(mapper -> mapper.map(MessageDto::getFrom, Message::setFrom))
                .addMappings(mapper -> mapper.map(MessageDto::getTo, Message::setTo));
    }

    public Message map(MessageDto messageDto) {
        return modelMapper.map(messageDto, Message.class);
    }

    public MessageDto map(Message message) {
        return modelMapper.map(message, MessageDto.class);
    }
}
