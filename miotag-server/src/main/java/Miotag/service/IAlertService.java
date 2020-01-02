package Miotag.service;

import Miotag.dto.AlertDto;
import Miotag.model.Alert;
import Miotag.model.User;

import java.util.List;

public interface IAlertService {
    Alert newAlert(Alert alert);
    List<AlertDto> getAlerts(User user);
    boolean deleteAlert(User user, AlertDto alertDto);
}
