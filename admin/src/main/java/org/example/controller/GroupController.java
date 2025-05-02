package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.common.convention.result.Result;
import org.example.common.convention.result.Results;
import org.example.dto.req.GroupSortReqDTO;
import org.example.dto.req.GroupUpdateReqDTO;
import org.example.dto.resp.ShortLinkGroupRespDTO;
import org.example.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接分组控制层
 */

@RestController
@RequiredArgsConstructor
public class GroupController {
    public final GroupService groupService;

    /**
     * 新增分组
     */
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> save(@RequestParam String name){
        groupService.saveGroup(name);
        return Results.success();
    }

    /**
     * 查询分组集合
     */
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listGroup() {
        return Results.success(groupService.listGroup());
    }

    /**
     * 修改短链接分组
     */
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> update(@RequestBody GroupUpdateReqDTO requestParam){
        groupService.update(requestParam);
        return Results.success();
    }

    /**
     * 删除短链接分组
     */
    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> remove(@RequestParam String gid){
        groupService.remove(gid);
        return Results.success();
    }

    /**
     * 排序短链接分组
     */
    @PostMapping("/api/short-link/admin/v1/group/sort")
    public Result<Void> sort(@RequestBody List<GroupSortReqDTO> requestParam){
        groupService.sort(requestParam);
        return Results.success();
    }
}
