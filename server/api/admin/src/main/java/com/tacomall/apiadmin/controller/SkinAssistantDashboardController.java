package com.tacomall.apiadmin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tacomall.apiadmin.service.MiniappAdminService;
import com.tacomall.apiadmin.vo.mini.MiniDashboardVo;
import com.tacomall.common.json.ResponseJson;

// 后台首页模块：负责返回总览统计和图表数据
@RestController
@RequestMapping("/skin-assistant/dashboard/")
public class SkinAssistantDashboardController {

    @Autowired
    private MiniappAdminService miniappAdminService;

    @PostMapping("summary")
    public ResponseJson<MiniDashboardVo> summary() {
        return miniappAdminService.dashboard();
    }
}
