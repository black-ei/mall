package com.mr.test;

import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTokenTest {

    //公钥位置
    private static final String pubKeyPath = "E:\\rea.pub";
    //私钥位置
    private static final String priKeyPath = "E:\\rea.pri";
    //公钥对象
    private PublicKey publicKey;
    //私钥对象
    private PrivateKey privateKey;

    /**
     * 生成公钥私钥 根据密文
     * @throws Exception
     */
    @Test
    public void genRsaKey() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "mingrui");

    }
    /**
     * 从文件中读取公钥私钥
     * @throws Exception
     */
    @Before
    public void getKeyByRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 根据用户信息结合私钥生成token
     * @throws Exception
     */
    @Test
    public void genToken() throws Exception {
// 生成token
        String token = JwtUtils.generateToken(new UserInfo(1, "zhaojunhao"),
                privateKey, 2);
        System.out.println("user-token = " + token);
    }

    /**
     * 结合公钥解析token
     * @throws Exception
     */
    @Test
    public void parseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ6aGFvanVuaGFvIiwiZXhwIjoxNjAyMjUxMDA5fQ.VDCze7zIeYi2FKl7nY985vxHaRB5oARnjFiJM37Q26ohxmCgeqFvZvRaDy_6gx7HGI5CpIb19tfA0-mqTSZVVdjeqZrhbus3z72bIOY1re8j7CKvix5e6wYkm5qmuEkZ_tk9KFgKe4omzVKbnQS8k0eRAk123L _JfUo5gCgOqxQ";
        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }



}
