package com.newCoder.community.service.Impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.newCoder.community.dao.DiscussPostMapper;
import com.newCoder.community.entity.DiscussPost;
import com.newCoder.community.service.DiscussPostService;
import com.newCoder.community.util.SensitiveWordFilter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author lijie
 * @date 2022-11-09 16:16
 * @Desc
 */
@Service
@Slf4j
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Value("${caffeine.post.max-size}")
    private int maxSize;
    @Value("${caffeine.post.expires-seconds}")
    private int expireSeconds;

    private LoadingCache<String,List<DiscussPost>> postListCache;
    private LoadingCache<Integer,Integer> postRowsCache;

    @PostConstruct
    public void init(){
        //初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterAccess(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(String key) throws Exception {
                        if(key == null || key.length() == 0){
                            throw new IllegalArgumentException("参数错误");
                        }
                        String[] split = key.split(":");
                        if(split.length != 2){
                            throw new IllegalArgumentException("参数错误");
                        }

                        int offset = Integer.parseInt(split[0]);
                        int limit = Integer.parseInt(split[1]);
                        log.info("load post from DB");
                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });
        //初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterAccess(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(Integer key) throws Exception {
                        if(key == null){
                            throw new IllegalArgumentException("参数错误");
                        }
                        log.info("load postRows from DB");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int orderMode){
//        if(userId == 0 && orderMode == 1){
//            return postListCache.get(offset + ":" + limit);
//        }
        log.info("load post from DB");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit,orderMode);
    }

    public int findDiscussPostRows(int userId){
//        if(userId == 0){
//            return postRowsCache.get(userId);
//        }
        log.info("load post from DB");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int publish(DiscussPost post) {
        //字符转义
        String title = HtmlUtils.htmlEscape(post.getTitle());
        String content = HtmlUtils.htmlEscape(post.getContent());

        //敏感词过滤
        post.setTitle(sensitiveWordFilter.filter(title));
        post.setContent(sensitiveWordFilter.filter(content));
        return discussPostMapper.insertDiscussPost(post);
    }

    @Override
    public DiscussPost findDiscussPostDetail(int postId) {
        return discussPostMapper.selectDiscussPostDetail(postId);
    }

    @Override
    public int topPost(int id,int type) {
        return discussPostMapper.updateType(id,type);
    }

    @Override
    public int wonderfulPost(int id,int status) {
        return discussPostMapper.updateStatus(id,status);
    }

    @Override
    public int deletePost(int id) {
        return discussPostMapper.updateStatus(id,2);
    }

    @Override
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id,score);
    }


}
