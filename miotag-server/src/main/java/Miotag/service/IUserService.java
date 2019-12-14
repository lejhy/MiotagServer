package Miotag.service;

import Miotag.dto.UserDto;
import Miotag.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.transaction.Transactional;
import java.util.List;

public interface IUserService {
    UserDto registerUser(UserDto userDto);
    UserDto getUserByEmail(String email);
    UserDto updateUser(UserDto userDto, String email);
    List<UserDto> getUsersFollowed(String email);
    boolean followUser(String email, UserDto userDto);
    boolean unfollowUser(String email, UserDto userDto);
}
