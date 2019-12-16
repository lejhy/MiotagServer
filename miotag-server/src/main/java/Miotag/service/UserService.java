package Miotag.service;

import Miotag.dto.UserDto;
import Miotag.exception.EmailExistsException;
import Miotag.exception.UserNotFoundException;
import Miotag.mapper.UserMapper;
import Miotag.model.User;
import Miotag.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ISecurityService securityService;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, ISecurityService securityService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.securityService = securityService;
    }

    @Override
    public UserDto registerUser(UserDto userDto) throws EmailExistsException {
        if (emailExist(userDto.getEmail())) {
            throw new EmailExistsException(userDto.getEmail());
        }
        User user = prepareNewUser(userDto);
        return userMapper.map(userRepository.save(user));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return userMapper.map(securityService.findUser(email));
    }

    @Override
    public boolean userExists(long id) {return userRepository.existsById(id); }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, String email) {
        User user = securityService.findUser(email);
        if (!email.equals(user.getEmail()) && emailExist(userDto.getEmail())) {
            throw new EmailExistsException(email);
        }

        userDto.setId(user.getId());
        userMapper.map(userDto, user);
        return userMapper.map(user);
    }

    @Override
    public List<UserDto> getUsersFollowed(String email) {
        return securityService.findUser(email).getUsersFollowed().stream().map(userMapper::map).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean followUser(String email, UserDto userDto) {
        User user = securityService.findUser(email);
        if (user.getId() == userDto.getId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot follow itself");
        }
        User target = userRepository.findById(userDto.getId()).orElseThrow(() ->
                new UserNotFoundException(userDto.getId())
        );
        return user.getUsersFollowed().add(target);
    }

    @Override
    @Transactional
    public boolean unfollowUser(String email, UserDto userDto) {
        User user = securityService.findUser(email);
        if (user.getId() == userDto.getId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot unfollow itself");
        }
        User target = userRepository.findById(userDto.getId()).orElseThrow(() ->
                new UserNotFoundException(userDto.getId())
        );
        return user.getUsersFollowed().remove(target);
    }

    private boolean emailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private User prepareNewUser(UserDto userDto) {
        User user = userMapper.map(userDto);
        user.setId(0);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        return user;
    }
}