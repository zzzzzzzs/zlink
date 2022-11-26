package com.zlink.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author zs
 * @date 2022/11/26
 */
@Data
public class BaseEntity {
    @TableField(exist = false)
    private int pageNo = 1;
    @TableField(exist = false)
    private int pageSize = 20;
}