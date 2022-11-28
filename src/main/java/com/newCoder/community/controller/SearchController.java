package com.newCoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.newCoder.community.constant.EntityConstant;
import com.newCoder.community.entity.DiscussPost;
import com.newCoder.community.entity.Page;
import com.newCoder.community.service.ElasticSearchService;
import com.newCoder.community.service.LikeService;
import com.newCoder.community.service.UserService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lijie
 * @date 2022-11-20 23:23
 * @Desc
 */
@Controller
public class SearchController {
    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, Page page, Model model){
        SearchResponse response = elasticSearchService.searchDiscussPost(keyword, page.getOffset(), page.getLimit());

        page.setPath("/search?keyword=" + keyword);
        page.setRows((int) response.getHits().getTotalHits().value);

        List<Map<String,Object>> searchVos = new ArrayList<>();
        SearchHit[] hits = response.getHits().getHits();
        if(hits.length > 0){
            for(SearchHit hit : hits){
                Map<String,Object> map = new HashMap<>();
                DiscussPost post = JSONObject.parseObject(hit.getSourceAsString(),DiscussPost.class);
                // 处理高亮显示的结果
                HighlightField titleField = hit.getHighlightFields().get("title");
                if (titleField != null) {
                    post.setTitle(titleField.getFragments()[0].toString());
                }
                HighlightField contentField = hit.getHighlightFields().get("content");
                if (contentField != null) {
                    post.setContent(contentField.getFragments()[0].toString());
                }

                map.put("user",userService.findUserById(post.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(EntityConstant.ENTITY_TYPE_POST,post.getId()));
                map.put("post",post);

                searchVos.add(map);
            }
        }
        model.addAttribute("keyword",keyword);
        model.addAttribute("searchVos",searchVos);
        return "/site/search";
    }

}
