package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.common.convention.exception.ClientException;
import org.example.common.convention.exception.ServiceException;
import org.example.common.enums.VailDateTypeEnum;
import org.example.dao.entity.ShortLinkDO;
import org.example.dao.entity.ShortLinkGotoDO;
import org.example.dao.mapper.ShortLinkGotoMapper;
import org.example.dao.mapper.ShortLinkMapper;
import org.example.dto.req.ShortLinkCreateReqDTO;
import org.example.dto.req.ShortLinkUpdateReqDTO;
import org.example.dto.resp.ShortLinkCreateRespDTO;
import org.example.dto.resp.ShortLinkGroupCountQueryRespDTO;
import org.example.dto.resp.ShortLinkPageRespDTO;
import org.example.service.ShortLinkService;
import org.example.toolkit.HashUtil;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 短链接接口实现层
 */
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        //生成短链接
        String shortLinkSuffix = generateShortLink(requestParam);
        //为shortLinkDO赋值
        ShortLinkDO shortLinkDO= BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUri(shortLinkSuffix);
        shortLinkDO.setFullShortUrl(requestParam.getDomain()+"/"+shortLinkSuffix);

        ShortLinkGotoDO linkGotoDO = ShortLinkGotoDO.builder()
                .fullShortUrl(requestParam.getDomain()+"/"+shortLinkSuffix)
                .gid(requestParam.getGid())
                .build();
        try{
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(linkGotoDO);
        }catch(DuplicateKeyException ex){
            //判断是否存在于数据库，不存在则直接新增
            if(shortUriCreateCachePenetrationBloomFilter.contains(shortLinkDO.getFullShortUrl())){
                shortUriCreateCachePenetrationBloomFilter.add(shortLinkDO.getFullShortUrl());
            }
            throw new ServiceException("短链接生成失败，请稍后再试");
        }

        shortUriCreateCachePenetrationBloomFilter.add(shortLinkDO.getFullShortUrl());
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(String gid) {
        // 创建分页对象，假设当前页为1，每页大小为10，可根据实际需求调整
        IPage<ShortLinkDO> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);

        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);

        IPage<ShortLinkDO> result = baseMapper.selectPage(page, queryWrapper);

        // 可在此处将 DO 转换为 DTO，如果需要进一步处理可添加转换逻辑
        return result.convert(item -> ShortLinkPageRespDTO.builder()
                .fullShortUrl(item.getFullShortUrl())
                .originUrl(item.getOriginUrl())
                .gid(item.getGid())
                .build());
    }


    /**
     * 生成短链接
     */
    private String generateShortLink(ShortLinkCreateReqDTO requestParam) {

        int customGenerateCount = 0;
        String shorUri=null;
        while (true) {
            if (customGenerateCount > 10) {
                throw new ServiceException("短链接生成失败，请稍后再试");
            }
            String originUrl = requestParam.getOriginUrl();
            originUrl += UUID.randomUUID().toString();
            shorUri = HashUtil.hashToBase62(originUrl);
            if (!shortUriCreateCachePenetrationBloomFilter.contains(requestParam.getDomain()+"/"+ shorUri)) {
                break;
            }
            customGenerateCount++;
        }
        return shorUri;
    }

    /**
     * 批量查询短链接分组下的短链接数量
     */
    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid, count(*) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .eq("del_flag", 0)
                .eq("del_time", 0L)
                .groupBy("gid");
        List<Map<String, Object>> shortLinkDOList = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(shortLinkDOList, ShortLinkGroupCountQueryRespDTO.class);
    }

    /**
     * 修改短链接
     */
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getOriginGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
        if (hasShortLinkDO == null) {
            throw new ClientException("短链接记录不存在");
        }
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(hasShortLinkDO.getDomain())
                .shortUri(hasShortLinkDO.getShortUri())
                .createdType(hasShortLinkDO.getCreatedType())
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .describe(requestParam.getDescribe())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .build();
        if (Objects.equals(hasShortLinkDO.getGid(), requestParam.getGid())) {
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .set(Objects.equals(requestParam.getValidDateType(), VailDateTypeEnum.PERMANENT.getType()), ShortLinkDO::getValidDate, null);

            baseMapper.update(shortLinkDO, updateWrapper);
        } else{
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, hasShortLinkDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            baseMapper.delete(queryWrapper);
            baseMapper.insert(shortLinkDO);
        }
    }

    @Override
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) {
        //TODO 跳转
    }
}
