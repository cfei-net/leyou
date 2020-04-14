package com.leyou.common.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 这个实体类专门用来封装当前用户的信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Long id; // 当前登录用户的id
    private String username;// 当前用户的名称
    private String role;//当前用户有什么角色
}
