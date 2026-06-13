package com.iflytek.webapi.service;

import cn.hutool.core.lang.UUID;
import cn.xfyun.api.TextCheckClient;
import cn.xfyun.api.TtsClient;
import cn.xfyun.model.response.TtsResponse;
import cn.xfyun.service.tts.AbstractTtsWebSocketListener;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.Response;
import okhttp3.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;



@Service
public class TtsService {
    private static final Logger logger = LoggerFactory.getLogger(TtsService.class);
    private static final String appId = System.getenv().getOrDefault("XFYUN_TTS_APP_ID", "");
    private static final String apiKey = System.getenv().getOrDefault("XFYUN_TTS_API_KEY", "");
    private static final String apiSecret = System.getenv().getOrDefault("XFYUN_TTS_API_SECRET", "");
    private static final String requestUrl = "https://api.xf-yun.com/v1/private/se3acbe7f";
    // 改写等级 <L1>  ~  <L6>  等级越高，改写程度越深
    private static final String LEVEL="<L6>";
    @Value("${audio.filePath}")
    private String AUDIO_FILE_PATH;
    @Value("${text.check}")
    private String textString;
    public String getUrl(String text) {
        String name = "";
        String uuid = UUID.randomUUID().toString();
        try {
            TtsClient ttsClient = new TtsClient.Builder()
                    .signature(appId, apiKey, apiSecret)
                    .build();
            String path = AUDIO_FILE_PATH + uuid + ".mp3";
            long time = System.currentTimeMillis();
            File file = new File(path);
            logger.info("字符串长度：{}", text.length());
            ttsClient.send(text, new AbstractTtsWebSocketListener(file) {
                @Override
                public void onSuccess(byte[] bytes) {
                    logger.info("消耗时间：{} ms", System.currentTimeMillis() - time);

                }

                @Override
                public void onFail(WebSocket webSocket, Throwable throwable, Response response) {
                    logger.error(throwable.getMessage());
                }

                @Override
                public void onBusinessFail(WebSocket webSocket, TtsResponse ttsResponse) {
                    logger.error(ttsResponse.toString());
                }
            });
            //文件写入时间等待
            long timeSleep = 10 * (text.length());
            logger.info("等待时间：{} ms", timeSleep);
            Thread.sleep(timeSleep);
            name = uuid + ".mp3";
            return "/files/" + name;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "";
        }

    }

    public String getMp3(MultipartFile file) {

        File directoryFile = new File(AUDIO_FILE_PATH);
        if (!directoryFile.exists()) {
            //创建多个文件夹
            directoryFile.mkdirs();
        }
        String name = file.getOriginalFilename();
        File file1 = new File(AUDIO_FILE_PATH + name);
        try {
            if (!file1.exists()) {
                file.transferTo(new File(AUDIO_FILE_PATH + name));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        name = name + ".mp3";
        return "/files/" + name;
    }


    public Map<String, String> textCheck(String text) {
        List<String> textList= Arrays.asList(textString.split(","));
        Map<String, String> map = new HashMap<>();
        TextCheckClient client = new TextCheckClient
                .Builder(appId, apiKey, apiSecret)
                .build();

        try {
            String result = client.send(text);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if ("0".equals(jsonObject.getJSONObject("header").getString("code"))) {
                String resultText=  jsonObject.getJSONObject("payload").getJSONObject("result").getString("text");
                String base64Decode = new String(Base64.getDecoder().decode(resultText), StandardCharsets.UTF_8);
                logger.info("文本检差返回解码的结果：{}",base64Decode);
                JSONObject textResult=JSONObject.parseObject(base64Decode);
                //遍历循环
                for (int i = 0; i < textList.size(); i++) {
                    JSONArray jsonArray=textResult.getJSONArray(textList.get(i));
                    for (int j = 0; j < jsonArray.size(); j++) {
                        List<String> strings=jsonArray.getJSONArray(j).toJavaList(String.class);
                        map.put(strings.get(1),strings.get(2));
                    }
                }
            }
        } catch (Exception e) {
            logger.info("调用接口失败", e);
            return map;
        }

        return map;
    }
    public List<String> textRewrite(String text) {
        List<String> textResultList=new ArrayList<>();
        try {
            String resp = doRequest(text);
            JSONObject tempJSONObject= JSON.parseObject(resp);
            logger.info("文本改写返回的结果：{}",tempJSONObject);
            if ("0".equals(tempJSONObject.getJSONObject("header").getString("code"))) {
                String resultText=  tempJSONObject.getJSONObject("payload").getJSONObject("result").getString("text");
                String base64Decode = new String(Base64.getDecoder().decode(resultText), StandardCharsets.UTF_8);
                JSONArray jsonArray=JSONArray.parseArray(base64Decode);
                logger.info("文本改写返回解码的结果：{}",base64Decode);
                //遍历循环
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONArray jsonArrayItem=jsonArray.getJSONArray(i);
                    textResultList.add(jsonArrayItem.getString(0));
                }
            }

          return   textResultList;
        } catch (Exception e) {
           logger.error("文本改写返回的结果异常",e);

        }
        return textResultList;
    }
    /**
     * 请求主方法
     * @return 返回服务结果
     * @throws Exception 异常
     */
    public String doRequest(String text) throws Exception {
        URL realUrl = new URL(buildRequetUrl());
        URLConnection connection = realUrl.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-type","application/json");

        OutputStream out = httpURLConnection.getOutputStream();
        String params = buildParam(text);
        // System.out.println("params=>"+params);
        out.write(params.getBytes(StandardCharsets.UTF_8));
        out.flush();
        InputStream is;
        try{
            is = httpURLConnection.getInputStream();
        }catch (Exception e){
            is = httpURLConnection.getErrorStream();
            throw new Exception("make request error:"+"code is "+httpURLConnection.getResponseMessage()+readAllBytes(is));
        }
        return readAllBytes(is);
    }

    /**
     * 处理请求URL
     * 封装鉴权参数等
     * @return 处理后的URL
     */
    public String buildRequetUrl(){
        URL url;
        // 替换调schema前缀 ，原因是URL库不支持解析包含ws,wss schema的url
        String  httpRequestUrl = requestUrl.replace("ws://", "http://").replace("wss://","https://" );
        try {
            url = new URL(httpRequestUrl);
            //获取当前日期并格式化
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = format.format(new Date());
            String host = url.getHost();
            if (url.getPort()!= 80 && url.getPort() != 443){
                host = host +":"+ url.getPort();
            }
            String builder = "host: " + host + "\n" +
                    "date: " + date + "\n" +
                    "POST " + url.getPath() + " HTTP/1.1";
            Charset charset = StandardCharsets.UTF_8;
            Mac mac = Mac.getInstance("hmacsha256");
            String apiSecretNew = System.getenv().getOrDefault("XFYUN_REWRITE_API_SECRET", "");
            SecretKeySpec spec = new SecretKeySpec(apiSecretNew.getBytes(charset), "hmacsha256");
            mac.init(spec);
            byte[] hexDigits = mac.doFinal(builder.getBytes(charset));
            String sha = Base64.getEncoder().encodeToString(hexDigits);
            String apiKeyNew = System.getenv().getOrDefault("XFYUN_REWRITE_API_KEY", "");
            String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKeyNew, "hmac-sha256", "host date request-line", sha);
            String authBase = Base64.getEncoder().encodeToString(authorization.getBytes(charset));
            return String.format("%s?authorization=%s&host=%s&date=%s", requestUrl, URLEncoder.encode(authBase), URLEncoder.encode(host), URLEncoder.encode(date));

        } catch (Exception e) {
            throw new RuntimeException("assemble requestUrl error:"+e.getMessage());
        }
    }    /**
     * 组装请求参数
     * 直接使用示例参数，
     * 替换部分值
     * @return 参数字符串
     */
    private String  buildParam(String text) throws UnsupportedEncodingException {
        String appIdNew = System.getenv().getOrDefault("XFYUN_REWRITE_APP_ID", "");
        String param = "{"+
                "    \"header\": {"+
                "        \"app_id\": \""+appIdNew+"\","+
                "        \"status\": 3"+
                "    },"+
                "    \"parameter\": {"+
                "        \"se3acbe7f\": {"+
                "            \"level\": \""+LEVEL+"\","+
                "            \"result\": {"+
                "                \"encoding\": \"utf8\","+
                "                \"compress\": \"raw\","+
                "                \"format\": \"json\""+
                "            }"+
                "        }"+
                "    },"+
                "    \"payload\": {"+
                "        \"input1\": {"+
                "            \"encoding\": \"utf8\","+
                "            \"compress\": \"raw\","+
                "            \"format\": \"plain\","+
                "            \"status\": 3,"+
                "            \"text\": \""+Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8))+"\""+
                "        }"+
                "    }"+
                "}";
        return param;
    }

    /**
     * 读取流数据
     *
     * @param is 流
     * @return 字符串
     * @throws IOException 异常
     */
    private String readAllBytes(InputStream is) throws IOException {
        byte[] b = new byte[1024];
        StringBuilder sb = new StringBuilder();
        int len;
        while ((len = is.read(b)) != -1){
            sb.append(new String(b, 0, len, StandardCharsets.UTF_8));
        }
        return sb.toString();
    }
}
