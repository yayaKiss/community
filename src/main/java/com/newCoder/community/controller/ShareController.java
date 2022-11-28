package com.newCoder.community.controller;

import com.newCoder.community.constant.TopicConstant;
import com.newCoder.community.entity.Event;
import com.newCoder.community.event.EventProducer;
import com.newCoder.community.util.CommunityUtils;
import com.newCoder.community.util.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author lijie
 * @date 2022-11-23 12:54
 * @Desc
 */
@Controller
@Slf4j
public class ShareController {

    @Autowired
    private EventProducer producer;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${wk.image.storage}")
    private String imageStorage;

    @Value("${qiniu.bucket.share.url}")
    private String shareBucketUrl;


    @GetMapping("/share")
    @ResponseBody
    public JsonResult share(String htmlUrl){
        String fileName = CommunityUtils.generateUUID();

        //异步处理 ---》 完成分享图片（将图片下载到本地服务器或之后的云服务器）
        Event event = new Event()
                .setTopic(TopicConstant.HTML_SHARE)
                .setData("htmlUrl",htmlUrl)
                .setData("fileName",fileName)
                .setData("suffix",".png");
        producer.sendEvent(event);

        //返回用户可以访问图片的路径
        return JsonResult.ok().put("imageUrl",shareBucketUrl + "/" + fileName);
    }

    //废弃
    @GetMapping("/image/share/{fileName}")
    public void getImage(@PathVariable("fileName")String fileName, HttpServletResponse response){
        if(StringUtils.isEmpty(fileName)){
            throw new IllegalArgumentException("文件名不能为空!");
        }
        //找到本地服务器（云服务器）上的图片，返回给客户
        response.setContentType("image/png");
        File file = new File(imageStorage + "/" + fileName + ".png");
        try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = fis.read(buffer)) != -1){
                os.write(buffer,0,len);
            }
        } catch (IOException e) {
            log.error("获取长图失败!"+ e.getMessage());
        }
    }
}
