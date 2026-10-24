package com.bq.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private LocalDateTime createTime;
}