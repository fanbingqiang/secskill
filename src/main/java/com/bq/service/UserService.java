package com.bq.service;

import com.bq.dto.UserLoginDTO;
import com.bq.vo.UserLoginVO;

public interface UserService {
    UserLoginVO login(UserLoginDTO dto);
}