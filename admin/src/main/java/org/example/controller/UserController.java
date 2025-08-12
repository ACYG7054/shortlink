package org.example.controller;


import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.convention.result.Result;
import org.example.common.convention.result.Results;
import org.example.dto.req.UserLoginDTO;
import org.example.dto.req.UserRegisterReqDTO;
import org.example.dto.resp.UserActualRespDTO;
import org.example.dto.resp.UserLoginRespDTO;
import org.example.dto.resp.UserRespDTO;
import org.example.service.UserService;
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

    /**
     * 修改用户
     */
    @PutMapping("/api/short-link/admin/v1/user")
    public Result<Void> update(@RequestBody UserRegisterReqDTO requestParam){
        userService.update(requestParam);
        return Results.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginDTO requestParam){
        return Results.success(userService.login(requestParam));
    }

    /**
     * 检查用户是否登录
     */
    @GetMapping("/api/short-link/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam String username, @RequestParam String uuid){
        return Results.success(userService.checkLogin(username,uuid));
    }

    /**
     * 退出登录
     */
    @DeleteMapping("/api/short-link/admin/v1/user/logout")
    public Result<Void> logout(@RequestParam String username,@RequestParam String uuid){
        log.info("username : {} {}, uuid : {} {}",username,username.getClass(),uuid,uuid.getClass());
        userService.logout(username,uuid);
        return Results.success();
    }
}
