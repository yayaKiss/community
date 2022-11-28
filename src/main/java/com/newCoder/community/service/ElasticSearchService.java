package com.newCoder.community.service;

import com.newCoder.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-20 22:08
 * @Desc
 */
public interface ElasticSearchService {

    //向Elasticsearch中添加数据
    void saveDiscussPost(DiscussPost post);

    //向Elasticsearch删除数据
    void deleteDiscussPost(int id);

    //搜索帖子
    SearchResponse searchDiscussPost(String keyword, int offset, int limit);

}
