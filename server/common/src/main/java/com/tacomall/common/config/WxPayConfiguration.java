package com.tacomall.common.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.tacomall.common.properties.WxPayProperties;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 👈 確保引入了這個
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Binary Wang
 */
@Slf4j // 👈 【關鍵】在這裡加上這行註解
@Configuration
@ConditionalOnClass(WxPayService.class)
@EnableConfigurationProperties(WxPayProperties.class)
@AllArgsConstructor
public class WxPayConfiguration {

  private WxPayProperties properties;

  @Bean
  @ConditionalOnMissingBean
  public WxPayService wxService() {
    log.info("⚠️ 檢測到微信支付配置初始化，正在使用開發者硬編碼模式...");

    WxPayConfig payConfig = new WxPayConfig();

    // --- 【關鍵修改】直接寫死假數據，不再依賴 properties ---
    payConfig.setAppId("wx0038a1d24bb9eb99");
    payConfig.setMchId("1234567890");
    payConfig.setMchKey("test1234567890123456789012345678"); // 必須32位

    // 其他可選配置也隨便填一個，防止內部校驗報錯
    payConfig.setSubAppId(null);
    payConfig.setSubMchId(null);
    payConfig.setKeyPath(null);
    payConfig.setPrivateKeyPath(null);
    payConfig.setPrivateCertPath(null);
    payConfig.setApiV3Key("test1234567890123456789012345678");

    payConfig.setPayBaseUrl("https://api.mch.weixin.qq.com");
    // --- 修改結束 ---

    payConfig.setUseSandboxEnv(false);

    WxPayService wxPayService = new WxPayServiceImpl();
    try {
      wxPayService.setConfig(payConfig);
      log.info("✅ 微信支付服務 (WxPayService) 初始化成功！(硬編碼模式)");
    } catch (Exception e) {
      log.error("❌ 微信支付服務初始化失敗！", e);
      throw new RuntimeException("微信支付配置錯誤：" + e.getMessage(), e);
    }

    return wxPayService;
  }

}