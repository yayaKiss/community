package com.newCoder.community.controller;

import com.alibaba.fastjson.JSON;
import com.newCoder.community.annotation.LoginRequired;
import com.newCoder.community.constant.EntityConstant;
import com.newCoder.community.entity.*;
import com.newCoder.community.service.*;
import com.newCoder.community.util.CommunityUtils;
import com.newCoder.community.util.HostHolder;
import com.newCoder.community.util.JsonResult;
import com.newCoder.community.vo.PostVo;
import com.newCoder.community.vo.ReplyVo;
import com.newCoder.community.vo.UpdateCodeVo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

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

    @Value("${qiniu.key.access}")
    private String accessKey;
    @Value("${qiniu.key.secret}")
    private String secretKey;
    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;
    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private CommentService commentService;


    @GetMapping("/setting.html")
//    @LoginRequired
    public String setting(Model model){
        //??????????????????
        String fileName = CommunityUtils.generateUUID();
        //??????????????????
        StringMap policy = new StringMap();
        policy.put("returnBody", JSON.toJSONString(JsonResult.ok()));
        //??????????????????
        Auth auth = Auth.create(accessKey,secretKey);
        String uploadToken = auth.uploadToken(headerBucketName,fileName,3600,policy);

        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("fileName",fileName);
        return "/site/setting";
    }

    //??????????????????
    @PostMapping("/header/url")
    @ResponseBody
    public JsonResult updateHeaderUrl(String fileName){
        if(StringUtils.isEmpty(fileName)){
            return JsonResult.error("?????????????????????");
        }
        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeaderUrl(hostHolder.getValue().getId(),url);
        return JsonResult.ok();
    }

    @GetMapping("/forget.html")
    public String forget(){
        return "/site/forget";
    }

    //??????
//    @LoginRequired
    @PostMapping("/upload")
    public String uploadImage(MultipartFile headImage, Model model){
        if(headImage.isEmpty()){
            model.addAttribute("error","????????????????????????");
            return "/site/setting";
        }
        //???????????????????????????
        String fileName = headImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if(StringUtils.isEmpty(suffix)){
            throw new RuntimeException("??????????????????");
        }
        //????????????????????????????????????
        fileName = CommunityUtils.generateUUID() + "." + suffix;

        File dest = new File(uploadPath + "/" +  fileName);
        try { //????????????
            headImage.transferTo(dest);
        } catch (IOException e) {
            log.info("??????????????????" + e.getMessage());
            throw new RuntimeException("??????????????????" + e.getMessage());
        }
        //??????????????????????????????(web?????????????????????????????????)
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getValue();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeaderUrl(user.getId(),headerUrl);

        return "redirect:/index";
    }

    //??????
    //?????????????????????????????????
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName,HttpServletResponse response){
        //?????????????????????
        fileName = uploadPath + "/" + fileName;
        //????????????
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
            log.info("??????????????????" + e.getMessage());
            throw new RuntimeException("???????????????????????????" + e.getMessage());
        }
    }

//    @LoginRequired
    @PostMapping("/updateCode")
    public String updateCode(UpdateCodeVo vo, Model model){
        User user = hostHolder.getValue();
        String oldPassword = vo.getOldPassword();
        oldPassword  = CommunityUtils.MD5(vo.getOldPassword() + user.getSalt());
        if(!oldPassword.equals(user.getPassword())){
            model.addAttribute("oldMsg","????????????");
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

    //????????????
    @GetMapping("/profile/{userId}")
    public String profile(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("?????????????????????");
        }
        model.addAttribute("user",user);
        //????????????
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        //????????????
        long followerCount = followService.findEntityFollowerCount(EntityConstant.ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);
        //????????????
        long followeeCount = followService.findEntityFolloweeCount(userId,EntityConstant.ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //????????????
        boolean followStatus = false;
        if(hostHolder.getValue() != null){
            followStatus = followService.findUserFollowStatus(hostHolder.getValue().getId(), EntityConstant.ENTITY_TYPE_USER ,userId);
        }
        model.addAttribute("followStatus",followStatus);
        return "/site/profile";
    }

    @GetMapping("/myPost/{userId}")
    public String myPost(@PathVariable("userId")int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("???????????????");
        }
        //??????id
        model.addAttribute("userId",user.getId());
        //????????????
        int postCount = discussPostService.findDiscussPostRows(user.getId());
        model.addAttribute("postCount",postCount);
        //????????????
        page.setRows(postCount);
        page.setLimit(5);
        page.setPath("/user/myPost/"+ userId);

        List<DiscussPost> posts = discussPostService.findDiscussPosts(user.getId(), page.getOffset(), page.getLimit(),0);
        List<PostVo> postVos = new ArrayList<>();
        if(posts != null){
            for(DiscussPost post : posts){
                PostVo vo = new PostVo();
                vo.setId(post.getId());
                vo.setTitle(post.getTitle());
                vo.setContent(post.getContent());
                vo.setPublishTime(post.getCreateTime());
                long likeCount = likeService.findEntityLikeCount(EntityConstant.ENTITY_TYPE_POST, post.getId());
                vo.setLikeCount(likeCount);

                postVos.add(vo);
            }
        }
        model.addAttribute("postVos",postVos);
        return "/site/my-post";
    }

    @GetMapping("/myReply/{userId}")
    public String myReply(@PathVariable("userId")int userId,Page page,Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("??????????????????");
        }
        model.addAttribute("userId",user.getId());
        int commentPostCount = commentService.findCommentPostCount(user.getId());
        model.addAttribute("commentPostCount",commentPostCount);

        page.setRows(commentPostCount);
        page.setPath("/user/myReply/" + userId);
        page.setLimit(5);

        List<Comment> commentPosts = commentService.findCommentPosts(user.getId(),page.getOffset(),page.getLimit());
        List<ReplyVo> replyVos = new ArrayList<>();
        if(commentPosts != null){
            for (Comment comment : commentPosts){
                ReplyVo vo  = new ReplyVo();
                vo.setPostId(comment.getEntityId());
                DiscussPost post = discussPostService.findDiscussPostDetail(comment.getEntityId());
                vo.setTitle(post.getTitle());
                vo.setContent(comment.getContent());
                vo.setPublishTime(comment.getCreateTime());

                replyVos.add(vo);
            }
        }
        model.addAttribute("replyVos",replyVos);

        return "/site/my-reply";
    }
}
