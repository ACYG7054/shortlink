package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dao.entity.ShortLinkDO;
import org.example.dto.req.ShortLinkCreateReqDTO;
import org.example.dto.resp.ShortLinkCreateRespDTO;

/**
 * 短链接接口层
 */
public interface ShortLinkService extends IService<ShortLinkDO> {
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}
