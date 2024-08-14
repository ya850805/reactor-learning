package com.learning.security.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Table(name = "t_user_role")
public class TUserRole {
    @Id
    private Long id;

    private Long userId;

    private Long roleId;

    private Instant createTime;

    private Instant updateTime;
}
