package com.iflytek.webapi.service;

import cn.hutool.json.XML;
import cn.xfyun.api.GeneralWordsClient;
import cn.xfyun.api.IseClient;
import cn.xfyun.config.OcrWordsEnum;
import cn.xfyun.model.response.ise.IseResponseData;
import cn.xfyun.service.ise.AbstractIseWebSocketListener;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baidubce.http.ApiExplorerClient;
import com.baidubce.http.AppSigner;
import com.baidubce.http.HttpMethodName;
import com.baidubce.model.ApiExplorerRequest;
import com.baidubce.model.ApiExplorerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iflytek.webapi.model.JsonParse;
import com.iflytek.webapi.model.TongueVo;
import com.iflytek.webapi.util.HttpUtils;
import okhttp3.*;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

import static cn.xfyun.util.StringUtils.gson;

@Service
public class ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    private static final String appId = System.getenv().getOrDefault("XFYUN_OCR_APP_ID", "");
    private static final String apiKey = System.getenv().getOrDefault("XFYUN_OCR_API_KEY", "");
    @Value("${audio.filePath}")
    private String AUDIO_FILE_PATH;
    @Value("${note.content}")
    private String content;

    private static final String iseAppId = System.getenv().getOrDefault("XFYUN_ISE_APP_ID", "");
    private static final String iseApiKey = System.getenv().getOrDefault("XFYUN_ISE_API_KEY", "");
    private static final String iseApiSecret = System.getenv().getOrDefault("XFYUN_ISE_API_SECRET", "");
    //private final static Base64.Decoder decoder = Base64.getDecoder();
    private final static Base64.Decoder decoder = Base64.getDecoder();
public Map<String,String> getName(MultipartFile file){

    byte[] fileBytes = new byte[0];
    try {
        fileBytes = file.getBytes();
    } catch (IOException e) {
        e.printStackTrace();
    }
    String base64String = Base64.getEncoder().encodeToString(fileBytes);
    String  img="";
    try {
        img= URLEncoder.encode(base64String, "utf-8");
    } catch (UnsupportedEncodingException e) {

        e.printStackTrace();
        return null;
    }
    return   HttpUtils.getName(img);
}

public Map<String,String> getOcrText(MultipartFile file){
    //判断文件夹是否存在，不存在时，创建文件夹
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
    GeneralWordsClient client = new GeneralWordsClient
            // 印刷文字识别  OcrWordsEnum.PRINT
            // 手写文字识别  OcrWordsEnum.HANDWRITING
            .Builder(appId, apiKey, OcrWordsEnum.PRINT)
            .build();
    InputStream inputStream = null;
    String result=null;
    try {
        inputStream = new FileInputStream(new File(AUDIO_FILE_PATH + name));
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String imageBase64 = Base64.getEncoder().encodeToString(bytes);
         result= client.generalWords(imageBase64);
    } catch (Exception e) {
        e.printStackTrace();
        logger.info("ocr图片转文字识别结果异常：{}",e.getMessage());
    }
    logger.info("ocr图片转文字识别结果：{}",result);
    List<String> resultList=new ArrayList<>();
    com.alibaba.fastjson2.JSONObject jsonObj = JSON.parseObject(result);
    if("0".equals(jsonObj.getString("code"))){
        com.alibaba.fastjson2.JSONObject data = jsonObj.getJSONObject("data");
        JSONArray block = data.getJSONArray("block");
        for (int i = 0; i < block.size(); i++) {
            com.alibaba.fastjson2.JSONObject blockObj = block.getJSONObject(i);
            JSONArray line = blockObj.getJSONArray("line");
            for (int j = 0; j < line.size(); j++) {
                com.alibaba.fastjson2.JSONObject lineObj = line.getJSONObject(j);
                JSONArray word = lineObj.getJSONArray("word");
                for (int k = 0; k < word.size(); k++) {
                    JSONObject wordObj = word.getJSONObject(k);
                    String content = wordObj.getString("content");
                    resultList.add(content);
                }
            }
        }
    }else {
        return null;
    }
    //获取检测内容
    List<String> contentList= Arrays.asList(content.split(","));
    HashMap<String,String> map = new HashMap<>();
    int k=0;
    for (int i = 0; i < contentList.size(); i++) {
        String content=contentList.get(i);
        //获取信息
        List<String> information= Arrays.asList(content.split("_"));
        String informationContent=information.get(0);
        String key=information.get(1);
        int informationScorce=Integer.valueOf(information.get(2));
        //遍历循环
          if(resultList.contains(informationContent)){
              map.put(key,"该项存在，建议优化");
              k=informationScorce+k;
          }else {
              map.put(key,"该项不完整，建议增加");
          }
    }
   // double rateStr = (new BigDecimal((float) k / contentList.size()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue()) * 100;
    map.put("score",String.valueOf(k));
    return map;
    }
public Map<String,String> getIse(MultipartFile file,String text,String type){
        //判断文件夹是否存在，不存在时，创建文件夹
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
        String ent= type.equals("1")?"en_vip":"cn_vip";
        IseClient client = new IseClient.Builder()
                .signature(iseAppId, iseApiKey, iseApiSecret)
                .addSub("ise")
                .addEnt(ent)
                .addCategory("read_sentence")
                .addTte("utf-8")
                .addText('\uFEFF' + text)
                .addRstcd("utf8")
                .build();
        File fileNew = new File(AUDIO_FILE_PATH + name);
         Map<String,String> map =new HashMap<>();
        logger.info("语音评测字符串长度：{}"+text.length()); ;
        Long s=System.currentTimeMillis();
        try {
            client.send(fileNew, new AbstractIseWebSocketListener() {

                @Override
                public void onSuccess(WebSocket webSocket, IseResponseData iseResponseData) {
                    try {
                        if(0==iseResponseData.getCode()) {
                            String result = new String(decoder.decode(iseResponseData.getData().getData()), "UTF-8");
                            logger.info("消耗时间：{}", System.currentTimeMillis() - s);
                            cn.hutool.json.JSONObject jsonObject = XML.toJSONObject(result);
                            if(type.equals("1")) {
                                cn.hutool.json.JSONObject readSentence = jsonObject.getJSONObject("xml_result").getJSONObject("read_sentence")
                                        .getJSONObject("rec_paper").getJSONObject("read_chapter");
                                String totalScore = readSentence.getStr("total_score");
                                String fluencyScore = readSentence.getStr("fluency_score");
                                String accuracyScore = readSentence.getStr("accuracy_score");
                                String standardScore = readSentence.getStr("standard_score");
                                map.put("totalScore", totalScore);
                                map.put("fluencyScore", fluencyScore);
                                map.put("accuracyScore", accuracyScore);
                                map.put("standardScore", standardScore);
                            }else{
                                cn.hutool.json.JSONObject readSentence1 = jsonObject.getJSONObject("xml_result");
                                logger.info(readSentence1.toString());
                                cn.hutool.json.JSONObject readSentence = jsonObject.getJSONObject("xml_result").getJSONObject("read_sentence")
                                        .getJSONObject("rec_paper").getJSONObject("read_sentence");
                                String totalScore = readSentence.getStr("total_score");
                                String fluencyScore = readSentence.getStr("fluency_score");
                                String accuracyScore = readSentence.getStr("phone_score");
                                String standardScore = readSentence.getStr("integrity_score");
                                map.put("totalScore", totalScore);
                                map.put("fluencyScore", fluencyScore);
                                map.put("phoneScore", accuracyScore);
                                map.put("integrityScore", standardScore);
                            }
                        }else {
                            map.put("fail","评测结果异常");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(WebSocket webSocket, Throwable throwable, Response response) {
                    map.put("fail","评测结果异常");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        //处理文件时间
        long timeSleep=400*(text.length());
        logger.info("等待时间：{} ms",timeSleep);
        try {
            Thread.sleep(timeSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return map;
    }
public TongueVo getTongue(MultipartFile file){
    TongueVo tongueVo=null;
    String  img="data:image/jpeg;base64,";
        try {
            byte[] fileBytes = file.getBytes();
        String base64String = Base64.getEncoder().encodeToString(fileBytes);
        img=img+URLEncoder.encode(base64String, "utf-8");
        } catch (Exception e) {
            logger.info("舌诊图片转换异常{}",e);
            return tongueVo;
        }

    String path = "https://macrocura-tongue.api.bdymkt.com/diagnose/tongue/match/question/";
    ApiExplorerRequest request = new ApiExplorerRequest(HttpMethodName.POST, path);
    request.setCredentials(
            System.getenv().getOrDefault("BAIDU_TONGUE_ACCESS_KEY", ""),
            System.getenv().getOrDefault("BAIDU_TONGUE_SECRET_KEY", "")
    );
    // 设置header参数
    request.addHeaderParameter("Content-Type", "application/json;charset=UTF-8");

    // 设置jsonBody参数
    String jsonBody = "{\r\n    \"source_code\": 0,\r\n    \"image\": \"" + img + "\"\r\n}";
    request.setJsonBody(jsonBody);
   ApiExplorerClient client = new ApiExplorerClient(new AppSigner());

    try {
        ApiExplorerResponse response = client.sendRequest(request);
        // 返回结果格式为Json字符串
       JSONObject result=JSONObject.parseObject(response.getResult());
       logger.info("获取结果：{}",response.getResult());
       //判断是否成功
        if("20000".equals(result.getString("code"))){
            tongueVo = gson.fromJson(result.getJSONObject("data").toJSONString(), TongueVo.class);
        }
    } catch (Exception e) {
        logger.info("舌诊获取结果失败：{}",e);
    }
       return tongueVo;
    }

    public String pictureToText(MultipartFile file){
        //判断文件夹是否存在，不存在时，创建文件夹
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
        GeneralWordsClient client = new GeneralWordsClient
                // 印刷文字识别  OcrWordsEnum.PRINT
                // 手写文字识别  OcrWordsEnum.HANDWRITING
                .Builder(appId, apiKey, OcrWordsEnum.PRINT)
                .build();
        InputStream inputStream = null;
        String result=null;
        try {
            inputStream = new FileInputStream(new File(AUDIO_FILE_PATH + name));
            byte[] bytes = IOUtils.toByteArray(inputStream);
            String imageBase64 = Base64.getEncoder().encodeToString(bytes);
            result= client.generalWords(imageBase64);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ocr图片转文字识别结果异常：{}",e.getMessage());
        }
        logger.info("ocr图片转文字识别结果：{}",result);
      StringBuilder textResult=new StringBuilder();
        com.alibaba.fastjson2.JSONObject jsonObj = JSON.parseObject(result);
        if("0".equals(jsonObj.getString("code"))){
            com.alibaba.fastjson2.JSONObject data = jsonObj.getJSONObject("data");
            JSONArray block = data.getJSONArray("block");
            for (int i = 0; i < block.size(); i++) {
                com.alibaba.fastjson2.JSONObject blockObj = block.getJSONObject(i);
                JSONArray line = blockObj.getJSONArray("line");
                for (int j = 0; j < line.size(); j++) {
                    com.alibaba.fastjson2.JSONObject lineObj = line.getJSONObject(j);
                    JSONArray word = lineObj.getJSONArray("word");
                    for (int k = 0; k < word.size(); k++) {
                        JSONObject wordObj = word.getJSONObject(k);
                        String content = wordObj.getString("content");
                        textResult.append(content);
                    }
                }
            }
        }else {
            return null;
        }
        return textResult.toString();

    }
}
