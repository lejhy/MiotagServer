package Miotag.service;

import Miotag.dto.UserDto;
import Miotag.exception.EmailExistsException;
import Miotag.exception.UserNotFoundException;
import Miotag.mapper.UserMapper;
import Miotag.model.Alert;
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
    private final AlertService alertService;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, AlertService alertService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.alertService = alertService;
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
    public UserDto getUser(User user) {
        return userMapper.map(getAtachedUserEntity(user));
    }

    @Override
    public UserDto getUser(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (user.isPrivate()) {
            user = new User();
            user.setId(id);
            user.setPrivate(true);
        }
        return userMapper.map(user);
    }

    @Override
    public List<UserDto> getUsers(String query) {
        List<User> users = userRepository.findAllByEmailContainingOrFirstNameContainingOrLastNameContaining(query, query, query);
        users = users.stream().filter(user -> !user.isPrivate()).collect(Collectors.toList());
        return users.stream().map(userMapper::map).collect(Collectors.toList());
    }

    @Override
    public boolean userExists(long id) {return userRepository.existsById(id); }

    @Override
    @Transactional
    public UserDto updateUser(User user, UserDto userDto) {
        user = getAtachedUserEntity(user);
        if (!user.getEmail().equals(userDto.getEmail()) && emailExist(userDto.getEmail())) {
            throw new EmailExistsException(user.getEmail());
        }

        userDto.setId(user.getId());
        userMapper.map(userDto, user);
        return userMapper.map(user);
    }

    @Override
    public List<UserDto> getUsersFollowed(User user) {
        return getAtachedUserEntity(user).getUsersFollowed().stream().map(userMapper::map).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean followUser(User user, long targetId) {
        user = getAtachedUserEntity(user);
        if (user.getId() == targetId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot follow itself");
        }
        User target = userRepository.findById(targetId).orElseThrow(() ->
                new UserNotFoundException(targetId)
        );
        if(user.getUsersFollowed().add(target)) {
            Alert alert = new Alert();
            alert.setUser(target);
            alert.setMessage("User "+user.getUsername()+" started following you.");
            alertService.newAlert(alert);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean unfollowUser(User user, long targetId) {
        user = getAtachedUserEntity(user);
        if (user.getId() == targetId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot unfollow itself");
        }
        User target = userRepository.findById(targetId).orElseThrow(() ->
                new UserNotFoundException(targetId)
        );
        return user.getUsersFollowed().remove(target);
    }

    private User getAtachedUserEntity(User user) {
        long userId = user.getId();
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
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
