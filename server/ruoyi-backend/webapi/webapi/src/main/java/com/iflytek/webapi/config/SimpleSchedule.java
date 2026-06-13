package com.iflytek.webapi.config;

import com.iflytek.webapi.service.AudioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class SimpleSchedule {
    private static final Logger logger = LoggerFactory.getLogger(SimpleSchedule.class);
    @Value("${audio.filePath}")
    private String AUDIO_FILE_PATH;
    @Value("${images.filePath}")
    private String imagesFilePath;

    @Scheduled(cron = " 0 0 2 * * ?")   //每日凌晨2点执行
    private void process() {
        File folder = new File(AUDIO_FILE_PATH); // 当前文件夹
        File[] listOfFiles = folder.listFiles();
        logger.info("执行删除文件数量：",listOfFiles.length);
        for (File file : listOfFiles) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    @Scheduled(cron = " 0 0 3 * * ?")   //每日凌晨3点执行
    private void processImages() {
        File folder = new File(imagesFilePath); // 当前文件夹
        File[] listOfFiles = folder.listFiles();
        logger.info("执行删除文件数量：",listOfFiles.length);
        for (File file : listOfFiles) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }

}