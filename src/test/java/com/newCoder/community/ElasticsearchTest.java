package com.newCoder.community;

import com.alibaba.fastjson.JSONObject;
import com.newCoder.community.dao.DiscussPostMapper;
import com.newCoder.community.dao.elasticsearch.DiscussPostRepository;
import com.newCoder.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lijie
 * @date 2022-11-20 16:33
 * @Desc
 */
@SpringBootTest
public class ElasticsearchTest {
    @Autowired
    DiscussPostMapper discussMapper;

    @Autowired
    DiscussPostRepository discussRepository;

    @Autowired
    RestHighLevelClient restHighLevelClient;


    @Test
    public void InsertTest(){
        discussRepository.save(discussMapper.selectDiscussPostDetail(241));
        discussRepository.save(discussMapper.selectDiscussPostDetail(242));
        discussRepository.save(discussMapper.selectDiscussPostDetail(243));
    }

    @Test
    public void InsertListTest(){
        discussRepository.saveAll(discussMapper.selectDiscussPosts(11,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(101,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(102,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(103,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(111,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(112,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(131,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(132,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(133,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(134,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(138,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(145,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(146,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(149,0,100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(164,0,100, 0));
    }

    @Test
    public void updateTest(){
        DiscussPost post = discussMapper.selectDiscussPostDetail(231);
        post.setTitle("新人灌水");
        post.setContent("我是新人，使劲灌水");
        discussRepository.save(post);
    }

    @Test
    public void deleteTest(){
        DiscussPost post = discussMapper.selectDiscussPostDetail(231);
        discussRepository.delete(post);
//        discussRepository.deleteById(231);
//        discussRepository.deleteAll();
    }

    @Test
    public void queryTest() throws IOException {
        SearchRequest searchRequest = new SearchRequest("discusspost");

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(1).size(10).highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<DiscussPost> lists = new ArrayList<>();
        for(SearchHit hit : searchResponse.getHits().getHits()){
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);
            // 处理高亮显示的结果
            HighlightField titleField = hit.getHighlightFields().get("title");
            if (titleField != null) {
                discussPost.setTitle(titleField.getFragments()[0].toString());
            }
            HighlightField contentField = hit.getHighlightFields().get("content");
            if (contentField != null) {
                discussPost.setContent(contentField.getFragments()[0].toString());
            }
            System.out.println(discussPost);
            lists.add(discussPost);
        }

        System.out.println(searchResponse.getHits().getTotalHits().value);
    }

}
