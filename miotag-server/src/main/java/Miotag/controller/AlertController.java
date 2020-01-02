package Miotag.controller;

import Miotag.dto.AlertDto;
import Miotag.exception.ValidationErrorException;
import Miotag.model.User;
import Miotag.service.IAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final IAlertService alertService;

    @Autowired
    public AlertController(IAlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<AlertDto> getAlerts(@AuthenticationPrincipal User user) {
        return alertService.getAlerts(user);
    }

    @DeleteMapping
    public boolean deleteAlert(@RequestBody @Valid AlertDto alertDto, BindingResult bindingResult, @AuthenticationPrincipal User user) {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }
        return alertService.deleteAlert(user, alertDto);
    }
}
