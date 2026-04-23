package com.tacomall.apiadmin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tacomall.common.entity.org.OrgStaff;
import com.tacomall.common.json.ResponseJson;
import com.tacomall.common.json.ResponsePageJson;
import com.tacomall.common.service.OrgStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/org/")
public class OrgStaffController {

    @Autowired
    private OrgStaffService orgStaffService;

    @PostMapping("staffPage")
    public ResponsePageJson<List<OrgStaff>> staffPage(
            @RequestParam("pageIndex") Integer pageIndex,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam("query") String query) {
        JSONObject queryJson = JSON.parseObject(query);
        return orgStaffService.page(pageIndex, pageSize, new JSONObject().fluentPut("query", queryJson));
    }

    @PostMapping("staffAdd")
    public ResponseJson<OrgStaff> staffAdd(@RequestParam("nickname") String nickname,
                                          @RequestParam("username") String username,
                                          @RequestParam("passwd") String passwd) {
        JSONObject json = new JSONObject();
        json.put("nickname", nickname);
        json.put("username", username);
        json.put("passwd", passwd);
        json.put("role", "operator"); // 默认角色为普通运营
        return orgStaffService.add(json);
    }

    @PostMapping("staffUpdate")
    public ResponseJson<String> staffUpdate(@RequestParam("id") Integer id,
                                           @RequestParam("nickname") String nickname,
                                           @RequestParam("username") String username,
                                           @RequestParam(value = "passwd", required = false) String passwd) {
        JSONObject json = new JSONObject();
        json.put("nickname", nickname);
        json.put("username", username);
        if (passwd != null && !passwd.isEmpty()) {
            json.put("passwd", passwd);
        }
        return orgStaffService.update(id, json);
    }

    @PostMapping("staffDelete")
    public ResponseJson<String> staffDelete(@RequestParam("id") Integer id) {
        return orgStaffService.delete(id);
    }
}
