package com.ruoyi.web.controller.system;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.ruoyi.system.service.ISysUserService;
import okhttp3.*;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.net.MediaType;
import com.ruoyi.common.config.wxAppConfig;
import com.ruoyi.common.core.domain.model.WxLoginBody;
import com.ruoyi.common.utils.sign.Base64;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysMenuService;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 登录验证
 *
 * @author ruoyi
 */
@Slf4j
@RestController
public class SysLoginController {
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private wxAppConfig wxAppConfig;

    @Autowired
    private ISysUserService userService;

    /**
     * 微信的登录
     *
     * @param wxLoginBody
     * @return
     */
    @PostMapping("/wxLogin")
    public AjaxResult wxLogin(@RequestBody WxLoginBody wxLoginBody) {
        String code = wxLoginBody.getCode();

        //秘钥
        String encryptedIv = wxLoginBody.getEncryptedIv();
        //加密数据
        String encryptedData = wxLoginBody.getEncryptedData();
        //向微信服务器发送请求获取用户信息
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + wxAppConfig.getAppId() + "&secret=" + wxAppConfig.getAppSecret() + "&js_code=" + code + "&grant_type=authorization_code";

        String res = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSONObject.parseObject(res);

        //获取session_key和openid
        assert jsonObject != null;
        String sessionKey = jsonObject.getString("session_key");
        String openid = jsonObject.getString("openid");

        //解密
        String decryptResult = "";
        try {
            //如果没有绑定微信开放平台，解析结果是没有unionid的。
            decryptResult = decrypt(sessionKey, encryptedIv, encryptedData);

        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("微信登录失败！");
        }

        if (StringUtils.hasText(decryptResult)) {
            // 3. 解析用户信息，包括性别等
            JSONObject userInfo = JSONObject.parseObject(decryptResult);

            //如果解析成功,获取token
            String token = loginService.wxLogin(decryptResult);
            AjaxResult ajax = AjaxResult.success();
            ajax.put(Constants.TOKEN, token);
            return ajax;
        } else {
            return AjaxResult.error("微信登录失败！");
        }
    }

    /**
     * AES解密
     */
    private String decrypt(String sessionKey, String encryptedIv, String encryptedData) throws Exception {
        // 转化为字节数组
        byte[] key = Base64.decode(sessionKey);
        byte[] iv = Base64.decode(encryptedIv);
        byte[] encData = Base64.decode(encryptedData);
        // 如果密钥不足16位，那么就补足
        int base = 16;
        if (key.length % base != 0) {
            int groups = key.length / base + (key.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(key, 0, temp, 0, key.length);
            key = temp;
        }
        // 如果初始向量不足16位，也补足
        if (iv.length % base != 0) {
            int groups = iv.length / base + (iv.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(iv, 0, temp, 0, iv.length);
            iv = temp;
        }

        AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
        String resultStr = null;

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            resultStr = new String(cipher.doFinal(encData), "UTF-8");
        } catch (Exception e) {
            //            logger.info("解析错误");
            e.printStackTrace();
        }

        // 解析加密后的字符串
        return resultStr;
    }

    /**
     * 获取用户手机号
     */
    private String getPhoneNumber(String sessionKey, String phoneCode) {
        try {
            // 调用微信获取手机号接口
            String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + getAccessToken();

            JSONObject requestBody = new JSONObject();
            requestBody.put("code", phoneCode);

            String response = restTemplate.postForObject(url, requestBody.toJSONString(), String.class);
            JSONObject result = JSONObject.parseObject(response);

            if (result != null && result.getIntValue("errcode") == 0) {
                JSONObject phoneInfo = result.getJSONObject("phone_info");
                return phoneInfo.getString("phoneNumber");
            }
        } catch (Exception e) {
            log.error("获取手机号失败", e);
        }
        return null;
    }

    /**
     * 获取微信access_token
     */
    private String getAccessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" +
                wxAppConfig.getAppId() + "&secret=" + wxAppConfig.getAppSecret();

        String response = restTemplate.getForObject(url, String.class);
        JSONObject result = JSONObject.parseObject(response);

        return result != null ? result.getString("access_token") : null;
    }

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);

        if (!loginUser.getPermissions().equals(permissions)) {
            loginUser.setPermissions(permissions);
            tokenService.refreshToken(loginUser);
        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        ajax.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
        ajax.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }

    // 检查初始密码是否提醒修改
    public boolean initPasswordIsModify(Date pwdUpdateDate) {
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    // 检查密码是否过期
    public boolean passwordIsExpiration(Date pwdUpdateDate) {
        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0) {
            if (StringUtils.isNull(pwdUpdateDate)) {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
