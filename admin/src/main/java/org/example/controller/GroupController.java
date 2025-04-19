package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.service.GroupService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接分组控制层
 */

@RestController
@RequiredArgsConstructor
public class GroupController {
    public final GroupService groupService;
}
