package org.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dao.entity.ShortLinkDO;
import org.example.dto.req.ShortLinkCreateReqDTO;
import org.example.dto.req.ShortLinkUpdateReqDTO;
import org.example.dto.resp.ShortLinkCreateRespDTO;
import org.example.dto.resp.ShortLinkGroupCountQueryRespDTO;
import org.example.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

/**
 * 短链接接口层
 */
public interface ShortLinkService extends IService<ShortLinkDO> {
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
    IPage<ShortLinkPageRespDTO> pageShortLink(String gid);
    List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam);
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

}
