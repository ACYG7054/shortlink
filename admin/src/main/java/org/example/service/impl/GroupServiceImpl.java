package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.example.common.biz.user.UserContext;
import org.example.dao.entity.GroupDO;
import org.example.dao.mapper.GroupMapper;
import org.example.dto.req.GroupSortReqDTO;
import org.example.dto.req.GroupUpdateReqDTO;
import org.example.dto.resp.ShortLinkGroupRespDTO;
import org.example.service.GroupService;
import org.example.toolkit.RandomGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    public final GroupMapper groupMapper;

    /**
     * 新增分组
     */
    @Override
    public void saveGroup(String name) {
        //随机生成6位gid
        String gid;
        String username= UserContext.getUsername();
        while(true){
            gid=RandomGenerator.generateRandom(6);
            LambdaQueryWrapper<GroupDO> qw = Wrappers.lambdaQuery(GroupDO.class);
            qw.eq(GroupDO::getGid,gid);
            qw.eq(GroupDO::getUsername,username);
            if(baseMapper.selectOne(qw)==null){
                break;
            }
        }
        GroupDO groupDO = new GroupDO();
        groupDO.setUsername(username);
        groupDO.setGid(gid);
        groupDO.setSortOrder(0);
        groupDO.setName(name);
        baseMapper.insert(groupDO);
    }

    /**
     * 查询用户分组集合
     */
    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> qw = Wrappers.lambdaQuery(GroupDO.class);
        qw.eq(GroupDO::getUsername,UserContext.getUsername());
        log.info("username:{}",UserContext.getUsername());
        qw.eq(GroupDO::getDelFlag,0);
        qw.orderByDesc(GroupDO::getSortOrder,GroupDO::getUpdateTime);

        List<GroupDO> groupDOS = baseMapper.selectList(qw);

        return BeanUtil.copyToList(groupDOS, ShortLinkGroupRespDTO.class);
    }

    /**
     * 修改短链接分组
     */
    @Override
    public void update(GroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> uw = Wrappers.lambdaUpdate(GroupDO.class);
        uw.eq(GroupDO::getGid,requestParam.getGid());
        uw.eq(GroupDO::getUsername, UserContext.getUsername());
        uw.eq(GroupDO::getDelFlag,0);
        baseMapper.update(BeanUtil.toBean(requestParam,GroupDO.class),uw);
    }

    /**
     * 删除短链接分组
     */
    @Override
    public void remove(String gid) {
        LambdaUpdateWrapper<GroupDO> uw = Wrappers.lambdaUpdate(GroupDO.class)
                                                .eq(GroupDO::getGid, gid)
                                                .eq(GroupDO::getUsername, UserContext.getUsername())
                                                .eq(GroupDO::getDelFlag,0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO,uw);
    }

    /**
     * 排序短链接分组
     */
    @Override
    public void sort(List<GroupSortReqDTO> requestParam) {
        requestParam.forEach(each->{
            GroupDO groupDO = new GroupDO();
            groupDO.setSortOrder(each.getSortOrder());
            LambdaUpdateWrapper<GroupDO> uw =Wrappers.lambdaUpdate(GroupDO.class);
            uw.eq(GroupDO::getGid,each.getGid());
            uw.eq(GroupDO::getUsername,UserContext.getUsername());
            uw.eq(GroupDO::getDelFlag,0);
            baseMapper.update(groupDO,uw);
        });
    }


}
