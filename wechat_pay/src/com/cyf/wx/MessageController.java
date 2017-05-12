package com.cyf.wx;

import com.cyf.domain.AccessToken;
import com.cyf.domain.TemplateData;
import com.cyf.domain.WxTemplate;
import com.cyf.util.*;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;


@Controller
@RequestMapping("/message")
@SuppressWarnings("rawtypes")
public class MessageController {


    private static Logger logger = LoggerFactory.getLogger(MessageController.class);

    @RequestMapping(value = "/sendMessage", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void message(String appId, String appSecret, String openId) {
      /*  AccessToken token = AccessTokenUtilO.getWeiXinAccessToken(appId, appSecret);
        String access_token = token.getToken();*/

      String access_token=  "NU_mNzAudNT2nAY2hAa_poLDiAtml7yv4rtWPZdSZAoAjYHTD-9swfRLA-lxrVrgc6pcwWuPTue2tJyd6zbCN121tXhLEELge0AFuBSnqJ-ViF7gPinw1UaIyFTB4EamTXHeAIADHR";
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token;
        WxTemplate temp = new WxTemplate();
        temp.setUrl("http://weixin.qq.com/download");

        openId="oCn8ExDrI5um_rFwyl-ue2iKYiBk";
        temp.setTouser(openId);
        temp.setTopcolor("#000000");
//        temp.setTemplate_id("ngqIpbwh8bUfcSsECmogfXcV14J0tQlEpBO27izEYtY");
        temp.setTemplate_id("HNb_GttgvjDTtAeXiQkNGtXyJcqHNbAj_rhTATOSDPc");
        Map<String, TemplateData> m = new HashMap<String, TemplateData>();
        TemplateData first = new TemplateData();
        first.setColor("#000000");
        first.setValue("这里填写您要发送的模板信息");
        m.put("first", first);
        TemplateData name = new TemplateData();
        name.setColor("#000000");
        name.setValue("另一行内人");
        m.put("name", name);
        TemplateData wuliu = new TemplateData();
        wuliu.setColor("#000000");
        wuliu.setValue("N行");
        m.put("wuliu", wuliu);
        TemplateData orderNo = new TemplateData();
        orderNo.setColor("#000000");
        orderNo.setValue("**666666");
        m.put("orderNo", orderNo);
        TemplateData receiveAddr = new TemplateData();
        receiveAddr.setColor("#000000");
        receiveAddr.setValue("*测试模板");
        m.put("receiveAddr", receiveAddr);
        TemplateData remark = new TemplateData();
        remark.setColor("#000000");
        remark.setValue("***备注说明***");
        m.put("Remark", remark);
        temp.setData(m);
         String jsonString = JSONObject.fromObject(temp).toString();
        JSONObject jsonObject = SendMessageHelper.httpRequest(url, "POST", jsonString);
        System.out.println(jsonObject);
        int result = 0;
        if (null != jsonObject) {
            if (0 != jsonObject.getInt("errcode")) {
                result = jsonObject.getInt("errcode");
                logger.error("错误 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
            }
        }
        logger.info("模板消息发送结果：" + result);
    }

    @RequestMapping(value = "/getOpenid", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String getUserOpenId() {
        String appId = "wx99c2381b15bbf68a";
        String appSecret = "46f26a9b99d2c39a6a4ca383be9951ac";
        /*AccessToken token = AccessTokenUtilO.getWeiXinAccessToken(appId, appSecret);
		 accessToken = token.getToken();*/

        String accessToken = AccessTokenUtil.getAccessToken(appId, appSecret);

        String result = null;
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
        System.out.println(accessToken);
        JSONObject jsonObject = SendMessageHelper.httpRequest(requestUrl, "GET", null);
        if (null != jsonObject) {
            System.out.println(jsonObject);
            System.out.println(jsonObject.get("total"));
            System.out.println(jsonObject.get("data"));
            result = jsonObject.get("data") + "";
        }
        return result;
    }
}
