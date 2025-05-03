package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.dao.entity.ShortLinkDO;
import org.example.dao.mapper.ShortLinkMapper;
import org.example.service.ShortLinkService;
import org.springframework.stereotype.Service;

/**
 * 短链接接口实现层
 */
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
}
