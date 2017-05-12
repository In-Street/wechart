package com.cyf.util;

import com.cyf.domain.MyX509TrustManager;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.net.ssl.*;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;

/**
 * Created by Administrator on 2017-2-6.
 */
public class SendMessageHelper {


        /**
         * 获取token接口
         */
        private String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";
        /**
         * 拉微信用户信息接口
         */
        private String getUserInfoUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token={0}&openid={1}";
        /**
         * 主动推送信息接口
         */
        private String sendMsgUrl = "https://api.weixin.qq.com/cgi-bin/message/send?access_token={0}";

        private HttpClient webClient;

        private static Logger log = LoggerFactory.getLogger(SendMessageHelper.class);



       /* *//**
         * @desc 初始化创建 WebClient
         *//*
        public void initWebClient() {

            log.info("initWebClient start....");
            try {
                PoolingClientConnectionManager tcm = new PoolingClientConnectionManager();
                tcm.setMaxTotal(10);
                SSLContext ctx = SSLContext.getInstance("TLS");
                X509TrustManager tm = new X509TrustManager() {

                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

                    }

                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };
                ctx.init(null, new X509TrustManager[] { tm }, null);
                SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                Scheme sch = new Scheme("https", 443, ssf);
                tcm.getSchemeRegistry().register(sch);
                webClient = new DefaultHttpClient(tcm);
            } catch (Exception ex) {
                log.error("initWebClient exception", ex);
            } finally {
                log.info("initWebClient end....");
            }
        }


        *//**
         * @desc 推送信息
         * @param token
         * @param msg
         * @return
         *//*
        public String sendMessage(String token,String msg){
            try{
                log.info("sendMessage start.token:"+token+",msg:"+msg);
                String url = MessageFormat.format(this.sendMsgUrl, token);
                HttpPost post = new HttpPost(url);
                ResponseHandler<?> responseHandler = new BasicResponseHandler();
                StringEntity entity = new StringEntity(msg);
                post.setEntity(entity);
                String response = (String) this.webClient.execute(post, responseHandler);
                log.info("return response=====start======");
                log.info(response);
                log.info("return response=====end======");
                return response;

            }catch (Exception e) {
                log.error("get user info exception", e);
                return null;
            }
        }

        *
         * @desc 发起HTTP GET请求返回数据
         * @param url
         * @return
         * @throws IOException
         * @throws ClientProtocolException
        private String executeHttpGet(String url) throws IOException, ClientProtocolException {
            ResponseHandler<?> responseHandler = new BasicResponseHandler();
            String response = (String) this.webClient.execute(new HttpGet(url), responseHandler);
            log.info("return response=====start======");
            log.info(response);
            log.info("return response=====end======");
            return response;
        }

    */

    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {

        JSONObject jsonObject = null;

        StringBuffer buffer = new StringBuffer();

        try {

// 创建SSLContext对象，并使用我们指定的信任管理器初始化

            TrustManager[] tm = { new MyX509TrustManager() };

            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");

            sslContext.init(null, tm, new java.security.SecureRandom());

// 从上述SSLContext对象中得到SSLSocketFactory对象

            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);

            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();

            httpUrlConn.setSSLSocketFactory(ssf);

            httpUrlConn.setDoOutput(true);

            httpUrlConn.setDoInput(true);

            httpUrlConn.setUseCaches(false);

// 设置请求方式（GET/POST）

            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))

                httpUrlConn.connect();

// 当有数据需要提交时

            if (null != outputStr) {

                OutputStream outputStream = httpUrlConn.getOutputStream();

// 注意编码格式，防止中文乱码

                outputStream.write(outputStr.getBytes("UTF-8"));

                outputStream.close();

            }

// 将返回的输入流转换成字符串

            InputStream inputStream = httpUrlConn.getInputStream();

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;

            while ((str = bufferedReader.readLine()) != null) {

                buffer.append(str);

            }

            bufferedReader.close();

            inputStreamReader.close();

// 释放资源

            inputStream.close();

            inputStream = null;

            httpUrlConn.disconnect();

            jsonObject = JSONObject.fromObject(buffer.toString());

        } catch (ConnectException ce) {

            log.error("Weixin server connection timed out.");

        } catch (Exception e) {

            log.error("https request error:{}", e);

        }

        return jsonObject;

    }




}
