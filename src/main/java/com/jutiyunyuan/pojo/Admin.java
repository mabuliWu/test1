package com.jutiyunyuan.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Kong
 * @Date 2020/10/26 21:19
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin implements Serializable {
    private String username;

    private String password;

    private String salt;

}
