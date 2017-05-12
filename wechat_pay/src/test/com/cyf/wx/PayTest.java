package com.cyf.wx;


import com.xinfu.weixin.dto.WeixinInfoDTO;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017-2-4.
 */
public class PayTest {


    @Test
    public void testWeixin_pay() throws Exception {
        HttpClient client = new DefaultHttpClient();
        //客户中心
      /*  String path = "http://localhost:8080/api/customer/proxyInfo/list";
       String requestInfo="{\"userId\": \"1\"}";*/

       // String path = "http://localhost:8080/pay/cyf/getURL";
        String path = "http://localhost:8080/pay/message/getOpenid";

        HttpPost post = new HttpPost(path);
        WeixinInfoDTO v = new WeixinInfoDTO();
        JSONObject contentObject = JSONObject.fromObject(v);
        String content = contentObject.toString();
        // String content = requestInfo;
        //String content = JSONBinder.binder(People.class).toJSON(v);
        StringEntity entity = null;
        try {
            entity = new StringEntity(content, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        entity.setContentEncoding("utf-8");
        entity.setContentType("application/json");
        post.setEntity(entity);
        HttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("响应状态码：" + response.getStatusLine().getStatusCode());
        InputStream is = null;
        try {
            is = response.getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
       /* String text = StreamUtil.readInputStream(is);
        System.out.println("服务器端响应的数据：" + text);*/
    }

    @Test
    public void timeTest(){
        System.out.println(System.currentTimeMillis());
    }
}