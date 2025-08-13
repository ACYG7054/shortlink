package org.example.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.convention.result.Result;
import org.example.common.convention.result.Results;
import org.example.dto.req.ShortLinkCreateReqDTO;
import org.example.service.ShortLinkService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
