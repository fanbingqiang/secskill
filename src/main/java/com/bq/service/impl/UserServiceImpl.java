package com.bq.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bq.dto.UserLoginDTO;
import com.bq.entity.User;
import com.bq.exception.BizException;
import com.bq.mapper.UserMapper;
import com.bq.service.UserService;
import com.bq.util.JwtUtil;
import com.bq.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserLoginVO login(UserLoginDTO dto) {
        // 1. 按用户名查用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BizException(50010, "用户名或密码错误");
        }

        // 2. 校验密码（先明文对比，后面再改加密）
        if (!user.getPassword().equals(dto.getPassword())) {
            throw new BizException(50010, "用户名或密码错误");
        }

        // 3. 生成 token
        String token = jwtUtil.createToken(user.getId(), user.getUsername());

        // 4. 组装返回
        UserLoginVO vo = new UserLoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        return vo;
    }


}