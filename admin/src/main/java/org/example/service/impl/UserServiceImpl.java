package org.example.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.common.convention.exception.ClientException;
import org.example.common.convention.exception.ServiceException;
import org.example.common.enums.UserErrorCodeEnum;
import org.example.config.RBloomFilterConfiguration;
import org.example.dao.entity.UserDO;
import org.example.dao.mapper.UserMapper;
import org.example.dto.req.UserRegisterReqDTO;
import org.example.dto.resp.UserRespDTO;
import org.example.service.UserService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.example.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static org.example.common.enums.UserErrorCodeEnum.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    private final RedissonClient redissonClient;
    private final RBloomFilter<String> userBloomFilterConfiguration;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(UserRegisterReqDTO requestParam) {
        //先判断用户名是否存在
        if(!findByUsername(requestParam.getUsername())){
            throw new ClientException(USER_NAME_EXIST);
        }
        //获取分布式锁
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        try{
            //上锁失败则直接返回
            if(!lock.tryLock()){
                throw new ClientException(USER_NAME_EXIST);
            }
            //
            if(baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class))<1){
                throw new ClientException(USER_SAVE_ERROR);
            }
            //将数据加入到布隆过滤器当中
            userBloomFilterConfiguration.add(requestParam.getUsername());
        }catch(DuplicateKeyException ex){
            //向数据库插入数据时，违反了唯一索引约束
            throw new ClientException(USER_EXIST);
        }finally{
            lock.unlock();
        }

    }

}
