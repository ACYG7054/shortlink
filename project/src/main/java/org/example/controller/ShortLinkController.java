package org.example.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.convention.result.Result;
import org.example.common.convention.result.Results;
import org.example.dto.req.ShortLinkCreateReqDTO;
import org.example.dto.resp.ShortLinkPageRespDTO;
import org.example.service.ShortLinkService;
import org.springframework.web.bind.annotation.*;

/**
 * 短链接管理控制层
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ShortLinkController {

     private final ShortLinkService shortLinkService;
    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/v1/create")
    public Result createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(@RequestParam String gid) {
        return Results.success(shortLinkService.pageShortLink(gid));
    }
}
