package Miotag.controller;

import Miotag.dto.MessageDto;
import Miotag.exception.ValidationErrorException;
import Miotag.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
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
    public List<MessageDto> getMessages(Principal principal) {
        return messageService.getMessages(principal.getName());
    }

    @PostMapping
    public MessageDto sendMessage(@RequestBody @Valid MessageDto messageDto, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }
        return messageService.sendMessage(messageDto, principal.getName());
    }
}
