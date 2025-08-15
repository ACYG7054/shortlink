package org.example.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;


/**
 * 短链接访问设备监控响应参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsDeviceRespDTO {

    /**
     * 统计
     */
    private Integer cnt;

    /**
     * 设备类型
     */
    private String device;

    /**
     * 占比
     */
    private Double ratio;
}
