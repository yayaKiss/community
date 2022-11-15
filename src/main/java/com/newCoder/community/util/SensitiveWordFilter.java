package com.newCoder.community.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lijie
 * @date 2022-11-13 21:25
 * @Desc
 */
@Slf4j
@Component
public class SensitiveWordFilter {
    private static final String REPLACE_WORD = "***";

    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init(){
        try ( InputStream is = this.getClass().getClassLoader().getResourceAsStream("Sensitive_words.txt");
              BufferedReader reader = new BufferedReader(new InputStreamReader(is));)
        {
            String word;
            while((word = reader.readLine()) != null){
                //读到每一个单词，添加到树中
                insert(word);
            }
        } catch (IOException e) {
            log.info("加载敏感词失败" + e.getMessage());
        }
    }

    private void insert(String word){
        TrieNode cur = root;
        for(int i =0;i < word.length();i++){
            char c = word.charAt(i);
            TrieNode child = cur.getChild(c);
            if(child == null){
                cur.addChild(c, new TrieNode());
            }
            cur = cur.getChild(c);
        }
        cur.isEnd = true;
    }

    public String filter(String word){
        if(StringUtils.isEmpty(word)){
            return null;
        }
        TrieNode cur = root;
        int start = 0;
        int end = 0;
        StringBuilder sb = new StringBuilder();
        while(start < word.length()){
            if(end < word.length()){
                char c = word.charAt(end);
                //跳过符号
                if(isSymbol(c)){
                    if(cur == root){
                        sb.append(c);
                        start++;
                    }
                    end++;
                    continue;
                }
                cur = cur.getChild(c);
                if(cur == null){
                    //以start开头不存在敏感词，start跳下一个开头,end重新向后找,cur节点回到root节点
                    sb.append(c);
                    end = ++start;
                    cur = root;
                }else if(cur.isEnd){
                    //start到end这段字符串是敏感词，需要替换
                    sb.append(REPLACE_WORD);
                    start = ++end;
                    cur = root;
                }else{
                    //end找一个词
                    end++;
                }
            }else{
                //越界也没有找到
                sb.append(word.charAt(start));
                end = ++start;
                cur = root;
            }
        }
        return sb.toString();
    }

    private boolean isSymbol(Character c){
        //数字字母  或 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    private class TrieNode{
        private boolean isEnd = false;
        private Map<Character,TrieNode> child = new HashMap<>();

        //添加子节点
        public void addChild(Character key,TrieNode node){
            child.put(key,node);
        }
        //获取子节点
        public TrieNode getChild(Character key){
            return child.get(key);
        }

    }
}
