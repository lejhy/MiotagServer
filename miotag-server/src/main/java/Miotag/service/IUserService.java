package Miotag.service;

import Miotag.dto.UserDto;
import Miotag.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.transaction.Transactional;

public interface IUserService extends UserDetailsService {
    @Transactional
    UserDto registerUser(UserDto userDto);
}
