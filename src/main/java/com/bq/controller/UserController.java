package com.bq.controller;

import com.bq.common.Result;
import com.bq.dto.UserLoginDTO;
import com.bq.service.UserService;
import com.bq.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // POST /api/user/login
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO dto) {
        return Result.success(userService.login(dto));
    }
}