package com.tacomall.apiadmin.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.tacomall.apiadmin.vo.mini.MiniCheckinItemVo;
import com.tacomall.apiadmin.vo.mini.MiniCosmeticItemVo;
import com.tacomall.apiadmin.vo.mini.MiniDashboardVo;
import com.tacomall.apiadmin.vo.mini.MiniSkinTestItemVo;
import com.tacomall.apiadmin.vo.mini.MiniUserItemVo;
import com.tacomall.common.json.ResponseJson;
import com.tacomall.common.json.ResponsePageJson;

public interface MiniappAdminService {
    ResponseJson<MiniDashboardVo> dashboard();

    ResponsePageJson<List<MiniUserItemVo>> userPage(Integer pageIndex, Integer pageSize, JSONObject json);

    ResponsePageJson<List<MiniSkinTestItemVo>> skinTestPage(Integer pageIndex, Integer pageSize, JSONObject json);

    ResponsePageJson<List<MiniCheckinItemVo>> checkinPage(Integer pageIndex, Integer pageSize, JSONObject json);

    ResponsePageJson<List<MiniCosmeticItemVo>> cosmeticPage(Integer pageIndex, Integer pageSize, JSONObject json);
}
