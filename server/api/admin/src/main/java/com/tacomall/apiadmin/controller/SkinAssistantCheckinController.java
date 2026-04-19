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
import com.tacomall.apiadmin.vo.mini.MiniCheckinItemVo;
import com.tacomall.common.json.ResponseJson;
import com.tacomall.common.json.ResponsePageJson;

// 护理打卡模块：负责打卡记录分页、详情和后台编辑
@RestController
@RequestMapping("/skin-assistant/checkins/")
public class SkinAssistantCheckinController {

    @Autowired
    private MiniappAdminService miniappAdminService;

    @PostMapping("page")
    public ResponsePageJson<List<MiniCheckinItemVo>> page(
            @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestBody JSONObject json) {
        return miniappAdminService.checkinPage(pageIndex, pageSize, json);
    }

    @PostMapping("info")
    public ResponseJson<MiniCheckinItemVo> info(@RequestParam("id") Integer id) {
        return miniappAdminService.checkinInfo(id);
    }

    @PostMapping("update")
    public ResponseJson<String> update(@RequestParam("id") Integer id, @RequestBody JSONObject json) {
        return miniappAdminService.checkinUpdate(id, json);
    }
}
