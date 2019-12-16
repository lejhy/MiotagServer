package Miotag.service;

import Miotag.dto.UserDto;

import java.util.List;

public interface IUserService {
    UserDto registerUser(UserDto userDto);
    UserDto getUserByEmail(String email);

    boolean userExists(long id);

    UserDto updateUser(UserDto userDto, String email);
    List<UserDto> getUsersFollowed(String email);
    boolean followUser(String email, UserDto userDto);
    boolean unfollowUser(String email, UserDto userDto);
}
