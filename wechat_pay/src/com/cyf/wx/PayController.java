package com.cyf.wx;

import com.cyf.util.HttpUtil;
import com.cyf.util.PayCommonUtil;
import com.cyf.util.PayConfigUtil;
import com.cyf.util.XMLUtil;
import com.swetake.util.Qrcode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;


@Controller
@RequestMapping("/cyf")
@SuppressWarnings("rawtypes")
public class PayController {


	private Logger logger = LoggerFactory.getLogger(PayController.class);

	@RequestMapping(value="/getURL" , method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String weixin_pay() throws Exception {
		// 账号信息
		String appid = PayConfigUtil.APP_ID;  // appid
		//String appsecret = PayConfigUtil.APP_SECRET; // appsecret
		String mch_id = PayConfigUtil.MCH_ID; // 商业号
		String key = PayConfigUtil.API_KEY; // key

		String currTime = PayCommonUtil.getCurrTime();
		String strTime = currTime.substring(8, currTime.length());
		String strRandom = PayCommonUtil.buildRandom(4) + "";
		String nonce_str = strTime + strRandom;

		String order_price = "100"; // 价格   注意：价格的单位是分
		String body = "测试商品";   // 商品名称
		String out_trade_no = "10001"; // 订单号

		// 获取发起电脑 ip
		String spbill_create_ip = PayCommonUtil.localIp();
		// 回调接口
		String notify_url = PayConfigUtil.NOTIFY_URL;
		String trade_type = "NATIVE";

		SortedMap<Object,Object> packageParams = new TreeMap<Object,Object>();
		packageParams.put("appid", appid);
		packageParams.put("mch_id", mch_id);
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("body", body);
		packageParams.put("out_trade_no", out_trade_no);
		packageParams.put("total_fee", order_price);
		packageParams.put("spbill_create_ip", spbill_create_ip);
		packageParams.put("notify_url", notify_url);
		packageParams.put("trade_type", trade_type);

		String sign = PayCommonUtil.createSign("UTF-8", packageParams,key);
		packageParams.put("sign", sign);

		String requestXML = PayCommonUtil.getRequestXml(packageParams);
		//System.out.println(requestXML);

		String resXml = HttpUtil.postData(PayConfigUtil.UFDODER_URL, requestXML);


		Map map = XMLUtil.doXMLParse(resXml);
		//String return_code = (String) map.get("return_code");
		//String prepay_id = (String) map.get("prepay_id");
		String urlCode = (String) map.get("code_url");
		System.out.println(urlCode);
		return urlCode;
	}


	public String getIMG() throws Exception {
		String img=QRfromGoogle("weixin://wxpay/bizpayurl?pr=btkPIwD");
		return img;
	}



	public static String QRfromGoogle(String chl) throws Exception {
		int widhtHeight = 300;
		String EC_level = "L";
		int margin = 0;
		chl = UrlEncode(chl);
		String QRfromGoogle = "http://chart.apis.google.com/chart?chs=" + widhtHeight + "x" + widhtHeight
				+ "&cht=qr&chld=" + EC_level + "|" + margin + "&chl=" + chl;

		return QRfromGoogle;
	}

	public static String UrlEncode(String src)  throws UnsupportedEncodingException {
		return URLEncoder.encode(src, "UTF-8").replace("+", "%20");
	}


	@RequestMapping(value="/getIMG" , method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public void drawQRCODE(/*String content,String filepath*/HttpServletResponse response){
		BufferedImage image=null;
		try {

			String content = "weixin://wxpay/bizpayurl?pr=mKRURAm";
			String filepath = "D://A.png";

			Qrcode qrcode=new Qrcode();
			qrcode.setQrcodeErrorCorrect('M');
			qrcode.setQrcodeEncodeMode('B');
			qrcode.setQrcodeVersion(15);
			int width= 235;
			int height=235;
			 image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			Graphics2D g2=image.createGraphics();
			g2.setBackground(Color.WHITE);
			g2.clearRect(0,0,235,235);
			g2.setColor(Color.BLACK);

			byte[] contentbytes=content.getBytes("utf-8");
			boolean[][] codeout= qrcode.calQrcode(contentbytes);
			for (int i = 0; i <codeout.length; i++) {
				for (int j = 0; j < codeout.length; j++) {
					if (codeout[j][i]) g2.fillRect(j*3+2,i*3+2,3,3);
				}
			}
			g2.dispose();
			image.flush();
			File imgFile = new File(filepath);
			ImageIO.write(image, "png", imgFile);

			OutputStream stream = response.getOutputStream();
			ImageIO.write(image, "png", stream);

		}catch (Exception e){
			e.printStackTrace();
		}

	}

	public void weixin_notify(HttpServletRequest request, HttpServletResponse response) throws Exception{

		//读取参数
		InputStream inputStream ;
		StringBuffer sb = new StringBuffer();
		inputStream = request.getInputStream();
		String s ;
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		while ((s = in.readLine()) != null){
			sb.append(s);
		}
		in.close();
		inputStream.close();

		//解析xml成map
		Map<String, String> m = new HashMap<String, String>();
		m = XMLUtil.doXMLParse(sb.toString());

		//过滤空 设置 TreeMap
		SortedMap<Object,Object> packageParams = new TreeMap<Object,Object>();
		Iterator it = m.keySet().iterator();
		while (it.hasNext()) {
			String parameter = (String) it.next();
			String parameterValue = m.get(parameter);

			String v = "";
			if(null != parameterValue) {
				v = parameterValue.trim();
			}
			packageParams.put(parameter, v);
		}

		// 账号信息
		String key = PayConfigUtil.API_KEY; // key

		//logger.info(packageParams);
		//判断签名是否正确
		if(PayCommonUtil.isTenpaySign("UTF-8", packageParams,key)) {
			//------------------------------
			//处理业务开始
			//------------------------------
			String resXml = "";
			if("SUCCESS".equals((String)packageParams.get("result_code"))){
				// 这里是支付成功
				//////////执行自己的业务逻辑////////////////
				String mch_id = (String)packageParams.get("mch_id");
				String openid = (String)packageParams.get("openid");
				String is_subscribe = (String)packageParams.get("is_subscribe");
				String out_trade_no = (String)packageParams.get("out_trade_no");

				String total_fee = (String)packageParams.get("total_fee");

				logger.info("mch_id:"+mch_id);
				logger.info("openid:"+openid);
				logger.info("is_subscribe:"+is_subscribe);
				logger.info("out_trade_no:"+out_trade_no);
				logger.info("total_fee:"+total_fee);

				//////////执行自己的业务逻辑////////////////

				logger.info("支付成功");
				//通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
				resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
						+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

			} else {
				logger.info("支付失败,错误信息：" + packageParams.get("err_code"));
				resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
						+ "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
			}
			//------------------------------
			//处理业务完毕
			//------------------------------
			BufferedOutputStream out = new BufferedOutputStream(
					response.getOutputStream());
			out.write(resXml.getBytes());
			out.flush();
			out.close();
		} else{
			logger.info("通知签名验证失败");
		}

	}

}
