package com.tacomall.apiadmin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tacomall.common.annotation.OrgStaffLoginLog;
import com.tacomall.common.entity.org.OrgAccessRule;
import com.tacomall.common.entity.org.OrgStaff;
import com.tacomall.common.json.ResponseJson;
import com.tacomall.common.service.OrgStaffService;

// 管理员认证模块：负责后台登录、登出、用户信息和权限菜单初始化
@RestController
@RequestMapping("/org/")
public class OrgAuthController {

    @Autowired
    private OrgStaffService orgStaffService;

    @OrgStaffLoginLog
    @PostMapping("staffLogin")
    public ResponseJson<String> staffLogin(
            @RequestParam("username") String username,
            @RequestParam("passwd") String passwd) {
        return orgStaffService.login(username, passwd);
    }

    @PostMapping("staffLogout")
    public ResponseJson<String> staffLogout() {
        return orgStaffService.logout();
    }

    @PostMapping("staffInfo")
    public ResponseJson<OrgStaff> staffInfo(@RequestParam(value = "id", defaultValue = "0") Integer id) {
        return orgStaffService.info(id);
    }

    @PostMapping("staffAccessRuleList")
    public ResponseJson<List<OrgAccessRule>> staffAccessRuleList() {
        return orgStaffService.accessRuleList();
    }
}
