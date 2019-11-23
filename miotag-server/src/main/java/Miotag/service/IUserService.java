package Miotag.service;

import Miotag.dto.UserDto;
import Miotag.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.transaction.Transactional;

public interface IUserService {
    UserDto registerUser(UserDto userDto);
    UserDto getUserByEmail(String email);
    UserDto updateUser(UserDto userDto, String email);
}
