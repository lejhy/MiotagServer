package Miotag.controller;

import Miotag.dto.AlertDto;
import Miotag.exception.ValidationErrorException;
import Miotag.service.IAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
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
    public List<AlertDto> getAlerts(Principal principal) {
        return alertService.getAlerts(principal.getName());
    }

    @DeleteMapping
    public boolean deleteAlert(@RequestBody @Valid AlertDto alertDto, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorException(bindingResult);
        }
        return alertService.deleteAlert(principal.getName(), alertDto);
    }
}
