package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.common.convention.exception.ServiceException;
import org.example.dao.entity.ShortLinkDO;
import org.example.dao.mapper.ShortLinkMapper;
import org.example.dto.req.ShortLinkCreateReqDTO;
import org.example.dto.resp.ShortLinkCreateRespDTO;
import org.example.dto.resp.ShortLinkPageRespDTO;
import org.example.service.ShortLinkService;
import org.example.toolkit.HashUtil;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.util.UUID;

/**
 * 短链接接口实现层
 */
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        //生成短链接
        String shortLinkSuffix = generateShortLink(requestParam);
        //为shortLinkDO赋值
        ShortLinkDO shortLinkDO= BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUri(shortLinkSuffix);
        shortLinkDO.setFullShortUrl(requestParam.getDomain()+"/"+shortLinkSuffix);
        try{
            baseMapper.insert(shortLinkDO);
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
                .eq(ShortLinkDO::getDelFlag, 0);

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
}
