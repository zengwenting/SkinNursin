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
import com.tacomall.apiadmin.vo.mini.MiniCosmeticItemVo;
import com.tacomall.apiadmin.vo.mini.MiniDashboardVo;
import com.tacomall.apiadmin.vo.mini.MiniSkinTestItemVo;
import com.tacomall.apiadmin.vo.mini.MiniUserItemVo;
import com.tacomall.common.json.ResponseJson;
import com.tacomall.common.json.ResponsePageJson;

@RestController
@RequestMapping("/mini/")
public class MiniappController {

    @Autowired
    private MiniappAdminService miniappAdminService;

    @PostMapping("dashboard")
    public ResponseJson<MiniDashboardVo> dashboard() {
        return miniappAdminService.dashboard();
    }

    @PostMapping("userPage")
    public ResponsePageJson<List<MiniUserItemVo>> userPage(
            @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestBody JSONObject json) {
        return miniappAdminService.userPage(pageIndex, pageSize, json);
    }

    @PostMapping("skinTestPage")
    public ResponsePageJson<List<MiniSkinTestItemVo>> skinTestPage(
            @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestBody JSONObject json) {
        return miniappAdminService.skinTestPage(pageIndex, pageSize, json);
    }

    @PostMapping("checkinPage")
    public ResponsePageJson<List<MiniCheckinItemVo>> checkinPage(
            @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestBody JSONObject json) {
        return miniappAdminService.checkinPage(pageIndex, pageSize, json);
    }

    @PostMapping("cosmeticPage")
    public ResponsePageJson<List<MiniCosmeticItemVo>> cosmeticPage(
            @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestBody JSONObject json) {
        return miniappAdminService.cosmeticPage(pageIndex, pageSize, json);
    }
}
