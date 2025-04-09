package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dao.entity.UserDO;
import org.example.dto.req.UserRegisterReqDTO;
import org.example.dto.resp.UserRespDTO;

public interface UserService extends IService<UserDO> {
    UserRespDTO getUserByUsername(String userName);
    boolean findByUsername(String username);
    void register(UserRegisterReqDTO requestParam);
}
