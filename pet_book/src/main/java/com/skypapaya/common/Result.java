package com.skypapaya.common;

import lombok.Data;

/**
 * 统一API响应结果封装
 * @param <T> 数据载体的类型
 */
@Data
public class Result<T> {

    private Integer code; // 状态码：200成功，其他失败
    private String msg;   // 提示信息："操作成功" 或 错误原因
    private T data;       // 返回的数据：可以是对象、List、Map等

    // 私有化构造方法，强制使用静态方法创建
    private Result() {}

    // 1. 成功 - 带数据
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 2. 成功 - 不带数据 (例如删除成功)
    public static <T> Result<T> success() {
        return success(null);
    }

    // 3. 失败 - 自定义错误信息
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500); // 默认错误码，也可以定义枚举来管理
        result.setMsg(msg);
        return result;
    }

    // 4. 失败 - 自定义错误码和信息
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}