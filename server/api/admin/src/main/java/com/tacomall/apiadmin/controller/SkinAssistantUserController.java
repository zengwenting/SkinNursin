package com.tacomall.apiadmin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.tacomall.apiadmin.service.MiniappAdminService;
import com.tacomall.apiadmin.vo.mini.MiniUserItemVo;
import com.tacomall.common.json.ResponseJson;
import com.tacomall.common.json.ResponsePageJson;

// 用户管理模块：负责用户分页、详情和编辑能力。
@RestController
@RequestMapping("/skin-assistant/users/")
public class SkinAssistantUserController {

    @Autowired
    private MiniappAdminService miniappAdminService;

    @PostMapping("page")
    public ResponsePageJson<List<MiniUserItemVo>> page(
            @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestBody JSONObject json) {
        return miniappAdminService.userPage(pageIndex, pageSize, json);
    }

    @PostMapping("info")
    public ResponseJson<MiniUserItemVo> info(@RequestParam("id") Integer id) {
        return miniappAdminService.userInfo(id);
    }

    @PostMapping("update")
    public ResponseJson<String> update(@RequestParam("id") Integer id, @RequestBody JSONObject json) {
        return miniappAdminService.userUpdate(id, json);
    }
}
