package org.example.dto.req;

import lombok.Data;
import org.example.dao.entity.ShortLinkDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 短链接分页请求参数
 */
@Data
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 排序标识
     */
    private String orderTag;
}