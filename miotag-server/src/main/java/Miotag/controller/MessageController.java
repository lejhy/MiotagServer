package Miotag.controller;

import Miotag.dto.MessageDto;
import Miotag.exception.ValidationErrorException;
import Miotag.model.User;
import Miotag.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final IMessageService messageService;

    @Autowired
    public MessageController(IMessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<MessageDto> getMessages(@AuthenticationPrincipal User user) {
        return messageService.getMessages(user);
    }

    @PostMapping
    public MessageDto sendMessage(@RequestBody @Valid MessageDto messageDto, BindingResult bindingResult, @AuthenticationPrincipal User user) {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }
        return messageService.sendMessage(messageDto, user);
    }
}
