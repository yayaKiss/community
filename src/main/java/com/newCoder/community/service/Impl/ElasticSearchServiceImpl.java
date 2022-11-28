package com.newCoder.community.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.newCoder.community.dao.elasticsearch.DiscussPostRepository;
import com.newCoder.community.entity.DiscussPost;
import com.newCoder.community.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lijie
 * @date 2022-11-20 22:17
 * @Desc
 */
@Service
@Slf4j
public class ElasticSearchServiceImpl implements ElasticSearchService {
    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void saveDiscussPost(DiscussPost post) {
        discussRepository.save(post);
    }

    @Override
    public void deleteDiscussPost(int id) {
        discussRepository.deleteById(id);
    }

    @Override
    public SearchResponse searchDiscussPost(String keyword, int offset, int limit) {
        SearchRequest searchRequest = new SearchRequest("discusspost");

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keyword,"title","content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(offset).size(limit).highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.info("elasticsearch检索出现异常 " + e.getMessage());
        }

        return searchResponse;
    }




}
