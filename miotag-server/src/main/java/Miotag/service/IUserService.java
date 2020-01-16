package Miotag.service;

import Miotag.dto.UserDto;
import Miotag.model.User;

import java.util.List;

public interface IUserService {
    UserDto registerUser(UserDto userDto);
    UserDto getUser(User user);

    UserDto getUser(long id);

    List<UserDto> getUsers(String query);

    boolean userExists(long id);

    UserDto updateUser(User user, UserDto userDto);
    List<UserDto> getUsersFollowed(User user);
    boolean followUser(User user, UserDto userDto);
    boolean unfollowUser(User user, UserDto userDto);
}
