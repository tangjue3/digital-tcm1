package com.iflytek.webapi.service;


import cn.xfyun.api.LfasrClient;
import cn.xfyun.config.LfasrTaskStatusEnum;
import cn.xfyun.model.response.lfasr.LfasrMessage;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AudioService {
    private static final Logger logger = LoggerFactory.getLogger(AudioService.class);

    private static final String APP_ID = System.getenv().getOrDefault("XFYUN_LFASR_APP_ID", "");
    private static final String SECRET_KEY = System.getenv().getOrDefault("XFYUN_LFASR_SECRET_KEY", "");
    @Value("${audio.filePath}")
    private String AUDIO_FILE_PATH;



   public String getText(MultipartFile file){
       try {
           //判断文件夹是否存在，不存在时，创建文件夹
           File directoryFile = new File(AUDIO_FILE_PATH);
           if (!directoryFile.exists()) {
               //创建多个文件夹
               directoryFile.mkdirs();
           }
           String name = file.getOriginalFilename();
           file.transferTo(new File(AUDIO_FILE_PATH + name));
           //1、创建客户端实例
           LfasrClient lfasrClient = new LfasrClient.Builder(APP_ID, SECRET_KEY).slice(102400).build();

           //2、上传
           LfasrMessage task = lfasrClient.upload(AUDIO_FILE_PATH + name);
           String taskId = task.getData();
           logger.info("转写任务 taskId：" + taskId);

           //3、查看转写进度
           int status = 0;
           while (LfasrTaskStatusEnum.STATUS_9.getKey() != status) {
               LfasrMessage message = lfasrClient.getProgress(taskId);

               logger.info(message.toString());
               Gson gson = new Gson();
               Map<String, String> map = gson.fromJson(message.getData(), new TypeToken<Map<String, String>>() {
               }.getType());
               status = Integer.parseInt(map.get("status"));
               TimeUnit.SECONDS.sleep(2);
           }
           //4、获取结果
           LfasrMessage result = lfasrClient.getResult(taskId);
           logger.info("获取结果内容：" + JSONObject.toJSONString(result));
          //处理结果
           if(result.getOk()==LfasrTaskStatusEnum.STATUS_0.getKey()){
               JSONArray jsonArray =  JSONArray.parseArray(result.getData());
               StringBuilder StringBuilder =new StringBuilder();
               //遍历获取
               for (int i = 0,j=jsonArray.size(); i <j ; i++) {
                   JSONObject jsonObject =jsonArray.getJSONObject(i);
                   StringBuilder.append(jsonObject.getString("onebest"));
               }
               return StringBuilder.toString();
           }
           return "";
       } catch (Exception e) {
           logger.error("语音转换异常：{}",e);
           return "";
       }
   }
    public  String getAuthUrl() throws Exception {
       String apiSecret = System.getenv().getOrDefault("XFYUN_AUDIO_API_SECRET", "");
        String apiKey = System.getenv().getOrDefault("XFYUN_AUDIO_API_KEY", "");
        String hostUrl="https://iat-api.xfyun.cn/v2/iat";
        URL url = new URL(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        StringBuilder builder = new StringBuilder("host: ").append(url.getHost()).append("\n").//
                append("date: ").append(date).append("\n").//
                append("GET ").append(url.getPath()).append(" HTTP/1.1");
        //System.out.println(builder);
        Charset charset = Charset.forName("UTF-8");
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
        String sha = Base64.getEncoder().encodeToString(hexDigits);

        //System.out.println(sha);
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        //System.out.println(authorization);
        HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(charset))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();
        String urlResult = httpUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        return urlResult;
    }

}
