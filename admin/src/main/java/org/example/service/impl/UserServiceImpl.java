package org.example.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.common.convention.exception.ServiceException;
import org.example.common.enums.UserErrorCodeEnum;
import org.example.dao.entity.UserDO;
import org.example.dao.mapper.UserMapper;
import org.example.dto.resp.UserRespDTO;
import org.example.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    /**
     * 根据用户名查询用户信息
     */
    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> lambdaQueryWrapper = Wrappers.lambdaQuery(UserDO.class);
        lambdaQueryWrapper.eq(UserDO::getUsername, username);

        UserDO userDO = baseMapper.selectOne(lambdaQueryWrapper);
        if (userDO == null) {
            throw new ServiceException(UserErrorCodeEnum.USER_NULL);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    /**
     * 判断用户名是否存在
     */
    @Override
    public boolean findByUsername(String username) {
        LambdaQueryWrapper<UserDO> lq = Wrappers.lambdaQuery(UserDO.class);
        lq.eq(UserDO::getUsername,username);
        UserDO userDO = baseMapper.selectOne(lq);
        return userDO==null;
    }

}
