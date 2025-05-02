package org.example.dto.req;

import lombok.Data;

@Data
public class GroupSortReqDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 排序
     */
    private Integer sortOrder;
}
