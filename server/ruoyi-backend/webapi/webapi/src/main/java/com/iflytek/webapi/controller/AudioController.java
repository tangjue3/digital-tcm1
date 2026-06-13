package com.iflytek.webapi.controller;


import com.alibaba.fastjson2.JSONObject;
import com.iflytek.webapi.model.TongueVo;
import com.iflytek.webapi.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@Api("服务接口")
@CrossOrigin(origins = "*")
public class AudioController {
    private static final Logger logger = LoggerFactory.getLogger(AudioController.class);
    @Autowired
    private AudioService service;
    @Autowired
    private SpeedAudioService  speedAudioService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private TtsService ttsService;
    @Autowired
    private FaceService faceService;

    @PostMapping("/audioUpload")
    @ApiOperation("讯飞语音识别")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
           return service.getText(file);
        }else {
            return "";
        }
    }

    @PostMapping("/audioUploadSpeed")
    @ApiOperation("讯飞语音识别极速版")
    public String audioUploadSpeed(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                return speedAudioService.getText(file);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }else {
            return "";
        }

    }

    @PostMapping("/audioUploadSpeedBd")
    @ApiOperation("百度语音识别")
    public String audioUploadSpeedBd(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                return speedAudioService.getBdText(file);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }else {
            return "";
        }

    }

    @PostMapping("/orc")
    @ApiOperation("药材图片识别")
    public Map<String,String> Ocr(@RequestParam("file") MultipartFile file)
    {
        if (!file.isEmpty()) {
            try {
                return imageService.getName(file);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else {
            return null;
        }
    }

    @GetMapping("/hostUrl")
    public String hostUrl()
    {
        try {
            return service.getAuthUrl();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @PostMapping("/text2audio")
    @ApiOperation("语音合成")
    public String text2audio(String text)
    {
        if(StringUtils.isEmpty(text)){
            logger.info("语音合成接口传参为空{}",text);
            return "语音合成接口传参内容为空";
        }
        return ttsService.getUrl(text);
    }
    @PostMapping("/ocrtext")
    @ApiOperation("印刷文字图片识别")
    public Map<String, String> getOcrText(@RequestParam("file") MultipartFile file)
    {
        if (!file.isEmpty()) {
            try {
                return imageService.getOcrText(file);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else {
            return null;
        }
    }

    @PostMapping("/ise")
    @ApiOperation("语音评测")
    public Map<String, String> getIse(@RequestParam("file") MultipartFile file,@RequestParam("text") String text)
    {
        if (!file.isEmpty()&&!text.isEmpty()) {
            try {
                return imageService.getIse(file,text,"1");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else {
            return null;
        }
    }
    @PostMapping("/tongue")
    @ApiOperation("百度舌诊接口")
    public TongueVo tonGue(@RequestParam("file") MultipartFile file)
    {
        if (!file.isEmpty()) {
            try {
                return imageService.getTongue(file);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else {
            return null;
        }
    }

    @PostMapping("/getMp3")
    @ApiOperation("获取音频")
    public String text2audio(@RequestParam("file") MultipartFile file)
    {
        if(!file.isEmpty()){
            logger.info("语音合成接口传参为空{}",file);
            return "语音合成接口传参内容为空";
        }
        return ttsService.getMp3(file);
    }

    @PostMapping("/textCheck")
    @ApiOperation("文字纠错")
    public Map<String,String> textCheck(String text)
    {
        if(StringUtils.isEmpty(text)){
            logger.info("文字纠错接口传参为空{}",text);
            return null;
        }
        return ttsService.textCheck(text);
    }
    @PostMapping("/textRewrite")
    @ApiOperation("文本改写")
    public List<String> textRewrite(String text)
    {
        if(StringUtils.isEmpty(text)){
            logger.info("文本改写接口传参为空{}",text);
            return null;
        }
        return ttsService.textRewrite(text);
    }
    @PostMapping("/pictureToText")
    @ApiOperation("图片转文字")
    public String pictureToText(@RequestParam("file") MultipartFile file)
    {
        if (!file.isEmpty()) {
            try {
                return imageService.pictureToText(file);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else {
            return null;
        }
    }

    @GetMapping("/test")
    @ApiOperation("测试")
    public String uploadFile(String  text) {
        logger.info("打印输出日志{}",text);
        return "你好"+text;
    }
    @PostMapping("/faceAi")
    @ApiOperation("舌诊、面诊")
    public JSONObject faceAi(@RequestParam("file") MultipartFile file, @RequestParam("type") String type)
    {
        if (!file.isEmpty()&&!type.isEmpty()) {
            try {
                return faceService.getface(file,type);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else {
            return null;
        }
    }

    @PostMapping("/iseC")
    @ApiOperation("中文语音评测")
    public Map<String, String> getIseC(@RequestParam("file") MultipartFile file,@RequestParam("text") String text)
    {
        if (!file.isEmpty()&&!text.isEmpty()) {
            try {
                return imageService.getIse(file,text,"2");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else {
            return null;
        }
    }

    @GetMapping("/faceAiPlus")
    @ApiOperation("舌诊、面诊")
    public JSONObject faceAiPlus(String imageUrl, String type)
    {
        if (!imageUrl.isEmpty()&&!type.isEmpty()) {
            try {
                return faceService.getfacePlus(imageUrl,type);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else {
            return null;
        }
    }
}
