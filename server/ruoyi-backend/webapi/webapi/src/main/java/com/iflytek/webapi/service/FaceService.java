package com.iflytek.webapi.service;




import com.alibaba.fastjson2.JSONObject;
import com.iflytek.webapi.util.FaceHttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class FaceService {
    @Autowired
    private ResourceLoader resourceLoader;

    private static final Logger logger = LoggerFactory.getLogger(FaceService.class);
    @Value("${images.open}")
    private String imagesOpen;
    @Value("${images.filePath}")
    private String imagesFilePath;
    @Value("${images.appcode}")
    private String imagesAppCode;
    @Value("${images.url}")
    private String imagesUrl;

    public JSONObject getface(MultipartFile file,String type) throws IOException {
        HashMap<String,String> map=new HashMap();
        map.put("0","tf_image");
        map.put("1","ff_image");
        map.put("2","tb_image");
        //判断是否走模拟数据
        if("0".equals(imagesOpen)){
            logger.info("调用模拟数据-----type：{}",type);
            switch (type){
                case "0":
                    //舌诊
                    return loadJsonFromResource("shenzhen1.txt");
                case "1":
                    //面诊
                    return loadJsonFromResource("mianzhen1.txt");
                case "2":
                    //舌下诊
                    return loadJsonFromResource("shexia1.txt");
                default:
                    return loadJsonFromResource("shenzhen1.txt");

            }
        }
        String host = "https://ali-market-tongue-detect-v2.macrocura.com";
        String path = "/diagnose/face-tongue/result/";
        String method = "POST";
        //判断文件夹是否存在，不存在时，创建文件夹
        File directoryFile = new File(imagesFilePath);
        if (!directoryFile.exists()) {
            //创建多个文件夹
            directoryFile.mkdirs();
        }
        //上传到服务器
        String name = file.getOriginalFilename();
        File file1 = new File(imagesFilePath + name);
        try {
            if (!file1.exists()) {
                file.transferTo(new File(imagesFilePath + name));
            }
        } catch (IOException e) {
            logger.error("图片上传tomcat失败",e);
        }

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + imagesAppCode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/json; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("scene",2);
        jsonObject.put(map.get(type),imagesUrl+name);
        String bodys= jsonObject.toJSONString();
         logger.info("景诊传递参数:{}",bodys);

        try {
           HttpResponse response = FaceHttpUtils.doPost(host, path, method, headers, querys, bodys);
            logger.info("景诊接口返回:{}",EntityUtils.toString(response.getEntity(),"utf-8"));
             return JSONObject.parseObject(EntityUtils.toString(response.getEntity(),"utf-8"));

        } catch (Exception e) {
            logger.info("景诊接口异常:{}",e);
        }
     return null;
    }
    public JSONObject loadJsonFromResource(String filePath) throws IOException {
        // 加载 resource 目录下的文件
        Resource resource = resourceLoader.getResource("classpath:" + filePath);
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        // 将文件内容转换为 JSONObject
        return JSONObject.parseObject(content.toString());
    }

    public JSONObject getfacePlus(String  image,String type) throws IOException {
        HashMap<String,String> map=new HashMap();
        map.put("01","tf_image");
        map.put("02","ff_image");
        map.put("03","tb_image");
        //判断是否走模拟数据
        if("0".equals(imagesOpen)){
            logger.info("调用模拟数据-----type：{}",type);
            switch (type){
                case "01":
                    //舌诊
                    return loadJsonFromResource("shenzhen1.txt");
                case "02":
                    //面诊
                    return loadJsonFromResource("mianzhen1.txt");
                case "03":
                    //舌下诊
                    return loadJsonFromResource("shexia1.txt");
                default:
                    return loadJsonFromResource("shenzhen1.txt");

            }
        }
        String host = "https://ali-market-tongue-detect-v2.macrocura.com";
        String path = "/diagnose/face-tongue/result/";
        String method = "POST";

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + imagesAppCode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/json; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("scene",2);
        jsonObject.put(map.get(type),image);
        String bodys= jsonObject.toJSONString();
        logger.info("景诊传递参数:{}",bodys);

        try {
            HttpResponse response = FaceHttpUtils.doPost(host, path, method, headers, querys, bodys);
            logger.info("景诊接口返回:{}",EntityUtils.toString(response.getEntity(),"utf-8"));
            return JSONObject.parseObject(EntityUtils.toString(response.getEntity(),"utf-8"));

        } catch (Exception e) {
            logger.info("景诊接口异常:{}",e);
        }
        return null;
    }

}
