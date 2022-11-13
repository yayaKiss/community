package com.newCoder.community.controller;

import com.newCoder.community.entity.LoginTicket;
import com.newCoder.community.entity.User;
import com.newCoder.community.service.UserService;
import com.newCoder.community.util.CommunityUtils;
import com.newCoder.community.util.HostHolder;
import com.newCoder.community.vo.UpdateCodeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * @author lijie
 * @date 2022-11-12 20:59
 * @Desc
 */
@RequestMapping("/user")
@Controller
@Slf4j
public class UserController {

    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;


    @PostMapping("/upload")
    public String uploadImage(MultipartFile headImage, Model model){
        if(headImage.isEmpty()){
            model.addAttribute("error","您还没有上传图像");
            return "/site/setting";
        }
        //获取上传文件的后缀
        String fileName = headImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if(StringUtils.isEmpty(suffix)){
            throw new RuntimeException("文件类型错误");
        }
        //生成随机的存储文件的名字
        fileName = CommunityUtils.generateUUID() + "." + suffix;

        File dest = new File(uploadPath + "/" +  fileName);
        try { //上传文件
            headImage.transferTo(dest);
        } catch (IOException e) {
            log.info("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败" + e.getMessage());
        }
        //更新数据库的图像地址(web地址，客户端进行访问的)
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getValue();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeaderUrl(user.getId(),headerUrl);

        return "redirect:/index";
    }

    //获取服务器（本地）图片
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName,HttpServletResponse response){
        //服务器存放地址
        fileName = uploadPath + "/" + fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
        ) {
            OutputStream os =  response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = fis.read(buffer)) != -1){
                os.write(buffer,0,len);
            }
        } catch (IOException e) {
            log.info("读取头像失败" + e.getMessage());
            throw new RuntimeException("浏览器读取图片失败" + e.getMessage());
        }
    }

    @PostMapping("/updateCode")
    public String updateCode(UpdateCodeVo vo, Model model){
        User user = hostHolder.getValue();
        String oldPassword = vo.getOldPassword();
        oldPassword  = CommunityUtils.MD5(vo.getOldPassword() + user.getSalt());
        if(!oldPassword.equals(user.getPassword())){
            model.addAttribute("oldMsg","密码错误");
            return "/site/setting";
        }
        Map<String,Object> map = userService.updateCode(user.getId(),user.getSalt(),vo);
        if(!map.isEmpty()){
            model.addAttribute("newMsg",map.get("newMsg"));
            model.addAttribute("confirmMsg",map.get("confirmMsg"));
            return "/site/setting";
        }

        return "redirect:/logout";
    }
}
