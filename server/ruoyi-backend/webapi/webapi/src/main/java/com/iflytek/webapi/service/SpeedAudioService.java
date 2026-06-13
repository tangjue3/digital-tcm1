package com.iflytek.webapi.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baidu.aip.speech.AipSpeech;
import com.google.gson.Gson;
import com.iflytek.webapi.model.*;
import com.iflytek.webapi.request.FileReq;
import com.iflytek.webapi.request.OpenReq;
import com.iflytek.webapi.util.FileCaller;
import com.iflytek.webapi.util.FileResp;
import com.iflytek.webapi.util.OpenCaller;
import com.iflytek.webapi.util.OpenResp;
import okhttp3.OkHttpClient;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class SpeedAudioService {
    private static final Logger logger = LoggerFactory.getLogger(SpeedAudioService.class);
    @Value("${audio.filePath}")
    private String AUDIO_FILE_PATH;
    // 控制台获取以下信息
    private static String APP_ID = System.getenv().getOrDefault("XFYUN_OST_APP_ID", "");
    private static String API_SECRET = System.getenv().getOrDefault("XFYUN_OST_API_SECRET", "");
    private static String API_KEY = System.getenv().getOrDefault("XFYUN_OST_API_KEY", "");

    private static String FILE_URL_PREFIX = "https://upload-ost-api.xfyun.cn/file"; // 上传文件的地址开头

    private static String OPEN_URL_PREFIX = "https://ost-api.xfyun.cn/v2"; // 创建、查询任务的地址开头
    private static OkHttpClient client = new OkHttpClient.Builder().build();
    private static final Gson gson = new Gson();
    private static final String AUDIO_PATH = "audioExample/audio_sample_little.wav"; // 在此指定音频文件路径
    private static final int SLICE_SIZE = 15728640;// 15M，每块范围 5M~32M
     //百度
    public static final String BD_APP_ID = System.getenv().getOrDefault("BAIDU_SPEECH_APP_ID", "");
    public static final String BD_API_KEY = System.getenv().getOrDefault("BAIDU_SPEECH_API_KEY", "");
    public static final String BD_SECRET_KEY = System.getenv().getOrDefault("BAIDU_SPEECH_SECRET_KEY", "");
    public String getText(MultipartFile file) throws Exception {
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
        File audio = new File(AUDIO_FILE_PATH + name); // 需极速转写的音频文件
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(AUDIO_FILE_PATH + name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileCaller fileCaller = FileCaller.builder().apiKey(API_KEY).apiSecret(API_SECRET).client(client).ulrPrefix(FILE_URL_PREFIX).build();
        JSONObject jsonObjectUploadRespOrCompleteResp;
        String id= IdUtil.randomUUID();
        /** 1. 上传音频文件 */
        if (audio.length() < 31457280) {
            /** 1.1 如果是小文件走单个上传接口，建议不大于30M */
            FileResp<FileResp.UploadData> uploadResp = fileCaller.fileUpload(FileReq.Upload.builder().
                    appId("f279524c").
                    fileName(name).
                    requestId(id).
                    data(IOUtils.toByteArray(fis))
                    .build());

            jsonObjectUploadRespOrCompleteResp = JSON.parseObject(JSON.toJSONString(uploadResp.getData()));
        } else { // 如果是大文件走分块上传接口
            /** 1.2 如果是大文件走分块上传接口*/
            /** 1.2.1 初始化分块信息 */
            FileResp<FileResp.InitData> initResp = fileCaller.fileInit(FileReq.Init.builder().
                    requestId("20211213155434").
                    appId("f279524c")
                    .build());
            JSONObject jsonObjectInitResp = JSON.parseObject(JSON.toJSONString(initResp.getData()));
            logger.info("初始化分块信息-返回的upload_id：" + jsonObjectInitResp.get("upload_id"));
            /** 1.2.2 分块上传 */
            // 分片上传文件
            int len = 0;
            byte[] slice = new byte[SLICE_SIZE];
            int sliceIdIndex = 1;
            while ((len = fis.read(slice)) > 0) {
                // 上传分片
                if (fis.available() == 0) {
                    slice = Arrays.copyOfRange(slice, 0, len);
                }
                FileResp<Void> partUploadResp = fileCaller.filePartUpload(FileReq.PartUpload.builder().
                        requestId("20211213152023").
                        appId("f279524c").
                        uploadId(jsonObjectInitResp.get("upload_id").toString()). // 使用初始化分块信息返回的upload_id
                        sliceId(sliceIdIndex).
                        data(slice).
                        build());
                logger.info("第" + sliceIdIndex + "块分块上传-返回的信息：" + partUploadResp);
                sliceIdIndex++;
            }
            /** 1.2.3 分块上传完成 */
            FileResp<Void> completeResp = fileCaller.fileUploadComplete(FileReq.Complete.builder().
                    appId("f279524c").requestId("2021164834").
                    uploadId(jsonObjectInitResp.get("upload_id").toString()). // 使用初始化分块信息返回的upload_id
                    build());
            logger.info("分块上传完成-返回的信息：" + completeResp);
            jsonObjectUploadRespOrCompleteResp = JSON.parseObject(JSON.toJSONString(completeResp.getData()));
        }

        /** 3. 创建任务 */
        OpenCaller openCaller = OpenCaller.builder().apiKey(API_KEY).apiSecret(API_SECRET).
                client(client).ulrPrefix(OPEN_URL_PREFIX).build();
        String requestId = IdUtil.randomUUID(); // 可以自定义
        OpenResp createResp = openCaller.create(OpenReq.Create.builder().
                common(OpenReq.Common.builder().appId("f279524c").build()).
                business(OpenReq.Business.builder().
                        requestId(requestId).
                        // callbackUrl("http://IP:端口号/xxx").
                                accent("mandarin").
                                language("zh_cn").
                                domain("pro_ost_ed").
                                build()).
                data(OpenReq.Data.builder().audioUrl(jsonObjectUploadRespOrCompleteResp.get("url").toString()). // 上传文件或分块上传完成返回的url
                        encoding("raw").format("audio/L16;rate=16000").audioSrc("http").build()).
                build());
        logger.info("创建任务-返回的信息：" + createResp);
        JSONObject jsonObjectCreateResp = JSON.parseObject(JSON.toJSONString(createResp.getData()));

        /** 4. 查询任务 */
        OpenResp queryResp = openCaller.query(OpenReq.Query.builder().
                common(OpenReq.Common.builder().appId("f279524c").build()).
                business(OpenReq.QueryBusiness.builder().taskId(jsonObjectCreateResp.get("task_id").toString()).build()). // 创建任务返回的task_id
                build());
        logger.info("查询任务-返回的信息：" + queryResp);
        JSONObject jsonObjectQueryResp = JSON.parseObject(JSON.toJSONString(queryResp.getData()));
        StringBuilder builder = new StringBuilder();
        while (true) {
            if (jsonObjectQueryResp.get("task_status").equals("5")) {
                logger.info("极速转写-最终结果==>：任务已取消..."); // 控制台打印取消信息
                break; // 跳出循环
            } else if (jsonObjectQueryResp.get("task_status").equals("3") || jsonObjectQueryResp.get("task_status").equals("4")) {

                logger.info("极速转写-最终结果（更多字段请参考queryResp进行解析）==>：");
                JsonParse jsonParse = gson.fromJson(jsonObjectQueryResp.toJSONString(), JsonParse.class);
                List<Lattice> latticeList = jsonParse.getResult().getLattice();
                for (int i = 0; i < latticeList.size(); i++) {
                    Lattice tempLattice = latticeList.get(i);
                    // String rl=tempLattice.json_1best.st.rl;
                    List<Rt> rtList = tempLattice.getJson_1best().getSt().getRt();
                    for (int j = 0; j < rtList.size(); j++) {
                        Rt tempRt = rtList.get(j);
                        List<Ws> wsList = tempRt.getWs();
                        for (int k = 0; k < wsList.size(); k++) {
                            Ws tempWs = wsList.get(k);
                            List<Cw> cwList = tempWs.getCw();
                            for (int l = 0; l < cwList.size(); l++) {
                                Cw tempCw = cwList.get(l);
                                builder.append(tempCw.getW());
                            }
                        }
                    }
                }
                break; // 跳出循环
            } else {
                Thread.sleep(1000); // 两秒查询一次
                // 再次查询任务
                queryResp = openCaller.query(OpenReq.Query.builder().
                        common(OpenReq.Common.builder().appId("f279524c").build()).
                        business(OpenReq.QueryBusiness.builder().taskId(jsonObjectCreateResp.get("task_id").toString()).build()). // 创建任务返回的task_id
                        build());
                jsonObjectQueryResp = JSON.parseObject(JSON.toJSONString(queryResp.getData()));
                logger.info("极速转写-转写中...");
            }
        }

        return builder.toString();
    }

    public String getBdText(MultipartFile file){
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
        AipSpeech client = new AipSpeech(BD_APP_ID, BD_API_KEY, BD_SECRET_KEY);
        // 调用接口
        org.json.JSONObject res = client.asr(AUDIO_FILE_PATH + name, "pcm", 16000, null);
       if("success.".equals(res.getString("err_msg"))){
           JSONArray jsonArray =  res.getJSONArray("result");
           return jsonArray.getString(0);
       }
        return "";
    }
}
