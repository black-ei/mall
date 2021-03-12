package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "tb_user")
@Data
public class UserEntity {
    @Id
    private Integer id;
    @NotEmpty(message = "用户名不能为空")
    private String username;
    @NotEmpty(message = "密码不能为空")
    private String password;
    private String phone;
    private Date created;
    private String salt;
}
