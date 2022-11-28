package com.newCoder.community.dao.elasticsearch;

import com.newCoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * @author lijie
 * @date 2022-11-20 16:31
 * @Desc
 */

//继承接口就行，ElasticsearchRepository底层已经实现了增删改查的方法，<参数1，参数2>
    //参数1 ：存储的实体
    //参数2 ： 实体主键的类型
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {
}
