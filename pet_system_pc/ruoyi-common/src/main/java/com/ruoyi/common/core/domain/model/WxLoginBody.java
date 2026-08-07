package com.ruoyi.common.core.domain.model;

/**
 * 微信用户登录对象
 *
 * @author ruoyi
 */
public class WxLoginBody {
    /**
     * 临时登陆凭证 code 只能使用一次
     */
    private String code;

    /**
     * 偏移量
     */
    private String encryptedIv;

    /**
     * 加密数据
     */
    private String encryptedData;


    private String phoneCode;

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEncryptedIv() {
        return encryptedIv;
    }

    public void setEncryptedIv(String encryptedIv) {
        this.encryptedIv = encryptedIv;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }
}