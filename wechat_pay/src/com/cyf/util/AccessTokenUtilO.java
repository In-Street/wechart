package com.cyf.util;

import com.cyf.domain.AccessToken;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Administrator on 2017-2-6.
 */
public class AccessTokenUtilO {

    private static Logger logger = LoggerFactory.getLogger(AccessTokenUtilO.class);

    public static AccessToken getWeiXinAccessToken(String appid, String secret)
    {
        if(StringUtils.isEmpty(appid)|| StringUtils.isEmpty(secret))
        {
            logger.error("appid or secret is null");
            return null;
        }
        AccessToken accessToken = new AccessToken();
        try
        {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+secret;
            HttpClient httpClient = new HttpClient();
            GetMethod getMethod = new GetMethod(url);
            int execute = httpClient.executeMethod(getMethod);
            System.out.println("execute:"+execute);
            String getResponse = getMethod.getResponseBodyAsString();
            System.out.println(getResponse);
            accessToken.setToken(getResponse);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println(accessToken);
        return accessToken;
    }
}
