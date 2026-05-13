package com.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("merchant")
public class Merchant {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String phone;
    private String description;
    private String logo;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
