package com.ohgiraffers.r_pakabe.domains.user.command.application.service;

import com.ohgiraffers.r_pakabe.common.error.ApplicationException;
import com.ohgiraffers.r_pakabe.common.error.ErrorCode;
import com.ohgiraffers.r_pakabe.domains.user.command.application.dto.UserRequestDTO;
import com.ohgiraffers.r_pakabe.domains.user.command.application.dto.UserResponseDTO;
import com.ohgiraffers.r_pakabe.domains.user.command.domain.model.User;
import com.ohgiraffers.r_pakabe.domains.user.command.domain.service.UserDomainService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserAppService {

    private final UserDomainService userDomainService;

    public UserAppService(UserDomainService userDomainService) {
        this.userDomainService = userDomainService;
    }


    @Transactional
    public UserResponseDTO.authDTO userLogin(UserRequestDTO.LoginDTO loginDTO) {
        User user = userDomainService.findByUserId(loginDTO.userId());

        if (user == null) {
            throw new ApplicationException(ErrorCode.NO_SUCH_USER);
        }

        if (user.getPassword().equals(loginDTO.password())) {
            return new UserResponseDTO.authDTO(user.getUserCode());
        }else {
            throw new ApplicationException(ErrorCode.INVALID_PASSWORD);
        }
    }

    @Transactional
    public void userRegister(UserRequestDTO.RegisterDTO registerDTO) {

        if (userDomainService.findByUserId(registerDTO.userId()) != null) {
            throw new ApplicationException(ErrorCode.ID_ALREADY_EXIT);
        }

        User user = User.builder()
                .userId(registerDTO.userId())
                .password(registerDTO.password())
                .nickname(registerDTO.nickName())
                .build();

        userDomainService.registerUser(user);
    }

    @Transactional
    public UserResponseDTO.UserDetailDTO findUser(Long userCode) {
        User user = userDomainService.findByUserCode(userCode);
        if (user == null) {
            throw new ApplicationException(ErrorCode.NO_SUCH_USER);
        }
        return new UserResponseDTO.UserDetailDTO(
                user.getUserId(),
                user.getNickname()
        );
    }







    /** 나중에 */

    @Transactional
    public void changeUserPW(UserRequestDTO.UserUpdateDTO updateDTO) {
        User existUser = userDomainService.findByUserCode(updateDTO.userCode());
        if (existUser == null) {
            throw new ApplicationException(ErrorCode.NO_SUCH_USER);
        }

        User user = new User(
                existUser.getUserCode(),
                existUser.getUserId(),
                updateDTO.password(),
                existUser.getNickname()
        );
        userDomainService.updateUser(user);
    }

    @Transactional
    public void changeUserNickName(String userId, String nickname) {
        User existUser = userDomainService.findByUserId(userId);
        if (existUser == null) {
            throw new ApplicationException(ErrorCode.NO_SUCH_USER);
        }
        if (existUser.getNickname().equals(nickname)) {
            throw new ApplicationException(ErrorCode.BAD_USER_DATA);
        }

        User user = new User(
                existUser.getUserCode(),
                existUser.getUserId(),
                existUser.getPassword(),
                nickname
        );
        userDomainService.updateUser(user);
    }


    @Transactional
    public void unregisterUser(String userId) {
        User existUser = userDomainService.findByUserId(userId);
        if (existUser == null) {
            throw new ApplicationException(ErrorCode.NO_SUCH_USER);
        }
        userDomainService.deleteUser(existUser);
    }










}
