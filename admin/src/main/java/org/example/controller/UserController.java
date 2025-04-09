package org.example.controller;


import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.convention.result.Result;
import org.example.common.convention.result.Results;
import org.example.dto.req.UserRegisterReqDTO;
import org.example.dto.resp.UserActualRespDTO;
import org.example.dto.resp.UserRespDTO;
import org.example.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    /**
     * 根据用户名查询信息
     */
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username){
    /*不再采用，使用全局异常拦截器
        UserRespDTO userRespDTO = userService.getByUsername(username);
        if(userRespDTO==null){
            return Results.failure(userRespDTO);
        }*/
        log.info("查询用户信息，用户名：{}",username);
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 根据用户名查询无脱敏信息
     */
    @GetMapping("/api/short-link/admin/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable("username") String username){
        log.info("查询用户信息，用户名：{}",username);
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username),UserActualRespDTO.class));
    }

    /**
     * 查找用户名是否存在
     */
    @GetMapping("/api/short-link/admin/v1/user/has-username")
    public Result<Boolean> findByUsername(@RequestParam("username")String username){
        return Results.success(userService.findByUsername(username));
    }

    /**
     * 用户注册
     */
    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam){
        userService.register(requestParam);
        return Results.success();
    }
}
