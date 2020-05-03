package Miotag.service;

import Miotag.model.User;

public interface ISecurityService {
    User findUser(String email);
}
