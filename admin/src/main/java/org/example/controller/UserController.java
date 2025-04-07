package org.example.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.convention.result.Result;
import org.example.common.convention.result.Results;
import org.example.dto.resp.UserRespDTO;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
     * @param username
     * @return
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

}
