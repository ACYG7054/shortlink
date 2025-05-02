package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dao.entity.GroupDO;
import org.example.dto.req.GroupSortReqDTO;
import org.example.dto.req.GroupUpdateReqDTO;
import org.example.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {
    void saveGroup(String name);
    List<ShortLinkGroupRespDTO> listGroup();
    void update(GroupUpdateReqDTO requestParam);
    void remove(String gid);
    void sort(List<GroupSortReqDTO> requestParam);
}
