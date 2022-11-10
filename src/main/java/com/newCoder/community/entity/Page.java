package com.newCoder.community.entity;

/**
 * @author lijie
 * @date 2022-11-10 00:05
 * @Desc
 */
public class Page {
    //当前页码
    private int current = 1;
    //页码大小
    private int limit = 10;
    //总评论数
    private int rows;
    //总页码
    private int total;
    //路径
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    //获取总的页码
    public int getTotal(){
        total = rows % limit == 0 ? rows / limit : rows / limit + 1;
        return total;
    }

    //获取当前页的起始行
    public int getOffSet(){
        return (current - 1) * limit;
    }
    //获取一系列页码
    public int getFrom(){
        int from = current - 2;
        return Math.max(from, 1);
    }
    public int getTo(){
        int to = current + 2;
        return Math.min(to,getTotal());
    }


}
