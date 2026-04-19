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
import com.tacomall.apiadmin.vo.mini.MiniCosmeticItemVo;
import com.tacomall.common.json.ResponseJson;
import com.tacomall.common.json.ResponsePageJson;

// 化妆台产品模块：负责产品分页、详情和编辑能力
@RestController
@RequestMapping("/skin-assistant/cosmetics/")
public class SkinAssistantCosmeticController {

    @Autowired
    private MiniappAdminService miniappAdminService;

    @PostMapping("page")
    public ResponsePageJson<List<MiniCosmeticItemVo>> page(
            @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestBody JSONObject json) {
        return miniappAdminService.cosmeticPage(pageIndex, pageSize, json);
    }

    @PostMapping("info")
    public ResponseJson<MiniCosmeticItemVo> info(@RequestParam("id") Integer id) {
        return miniappAdminService.cosmeticInfo(id);
    }

    @PostMapping("update")
    public ResponseJson<String> update(@RequestParam("id") Integer id, @RequestBody JSONObject json) {
        return miniappAdminService.cosmeticUpdate(id, json);
    }
}
