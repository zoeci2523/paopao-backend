package com.yupi.usercenterbackend.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * Paopao平台用户信息
 * 从excel引入，完成excel和java类对象字段的映射
 */
@Data
public class PPUserInfo {

    /**
     * id
     */
    @ExcelProperty("成员编号")
    private String planetCode;

    /**
     * 用户名称
     */
    @ExcelProperty("成员昵称")
    private String username;

}
