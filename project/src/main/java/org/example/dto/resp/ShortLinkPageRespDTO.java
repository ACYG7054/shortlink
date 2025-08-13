package org.example.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接分页返回参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkPageRespDTO {
    private Long id;
    private String domin;
    private String shortUri;
    private String fullShortUrl;
    private String originUrl;
    private String gid;
    private  Integer validDateType;
    private Date validDate;
    private String describe;
    private String favicon;
}
