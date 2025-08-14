package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dao.entity.ShortLinkDO;
import org.example.dto.req.RecycleBinSaveReqDTO;

/**
 * 回收站管理接口层
 */
public interface RecycleBinService extends IService<ShortLinkDO> {
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);
}
