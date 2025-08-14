package org.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dao.entity.ShortLinkDO;
import org.example.dto.req.RecycleBinRecoverReqDTO;
import org.example.dto.req.RecycleBinRemoveReqDTO;
import org.example.dto.req.RecycleBinSaveReqDTO;
import org.example.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.example.dto.resp.ShortLinkPageRespDTO;

/**
 * 回收站管理接口层
 */
public interface RecycleBinService extends IService<ShortLinkDO> {
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam);

    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    void removeRecycleBin(RecycleBinRemoveReqDTO requestParam);
}
