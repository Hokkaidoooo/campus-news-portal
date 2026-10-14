package com.citywy.util;

public class PageUtil {
    private int x_pageNow;//第几页
    private int x_pageSize;//每页多少条
    private int x_pageCount;//一共多少页
    private int x_rowCount;//一共多少条
    private String x_order;//排序列 id menu...
    private String x_sort;//升序 降序 asc  desc
    private String x_name;//关键词搜索
    private String x_name1;//关键词搜索
    private String x_name2;//关键词搜索
    private String x_name3;//关键词搜索

    public PageUtil(int x_pageNow, int x_pageSize, int x_pageCount, int x_rowCount, String x_order,
                    String x_sort, String x_name,String x_name1,String x_name2,String x_name3) {
        this.x_pageNow = x_pageNow;
        this.x_pageSize = x_pageSize;
        this.x_pageCount = x_pageCount;
        this.x_rowCount = x_rowCount;
        this.x_order = x_order;
        this.x_sort = x_sort;
        this.x_name = x_name;
        this.x_name1 = x_name1;
        this.x_name2 = x_name2;
        this.x_name3 = x_name3;
    }

    /**
     * 获取前一页
     * @return
     */
    public int getPreviousPage(){
        return this.x_pageNow>1?this.x_pageNow-1:this.x_pageNow;
    }

    /**
     * 获取后一页
     * @return
     */
    public int getNextPage(){
        return this.x_pageNow<this.x_pageCount?this.x_pageNow+1:this.x_pageNow;
    }

    /**
     * 获取尾页页码
     *
     * @return
     */
    public int getEndPage() {
        return getX_pageCount();
    }

    /***********************************************************************
     * 判断是否有上一页
     *
     * @return
     */
    public boolean hasPreviousPage() {
        if (getX_pageNow() > 1) {
            return true;
        } else {
            return false;
        }
    }

    /***********************************************************************
     * 判断是否有下一页
     *
     * @return
     */
    public boolean hasNextPage() {
        if (getX_pageNow() < getX_pageCount()) {
            return true;
        } else {
            return false;
        }
    }

    public int getX_pageNow() {
        return x_pageNow;
    }

    public void setX_pageNow(int x_pageNow) {
        this.x_pageNow = x_pageNow;
    }

    public int getX_pageSize() {
        return x_pageSize;
    }

    public void setX_pageSize(int x_pageSize) {
        this.x_pageSize = x_pageSize;
    }

    /**
     * 获取总页数
     * (总条数-1)/每页多少条 + 1
     * @return
     */
    public int getX_pageCount() {
        //pagecount=rowcount%pagesize==0?rowcount/pagesize:rowcount/pagesize+1;第一种写法，比较长
        //计算一共多少页 根据总条数和每页多少条
        return (x_rowCount-1)/x_pageSize+1;
    }

    public void setX_pageCount(int x_pageCount) {
        this.x_pageCount = x_pageCount;
    }

    public int getX_rowCount() {
        return x_rowCount;
    }

    public void setX_rowCount(int x_rowCount) {
        this.x_rowCount = x_rowCount;
    }

    public String getX_order() {
        return x_order;
    }

    public void setX_order(String x_order) {
        this.x_order = x_order;
    }

    public String getX_sort() {
        return x_sort;
    }

    public void setX_sort(String x_sort) {
        this.x_sort = x_sort;
    }

    public String getX_name() {
        return x_name;
    }

    public void setX_name(String x_name) {
        this.x_name = x_name;
    }

    public String getX_name1() {
        return x_name1;
    }

    public void setX_name1(String x_name1) {
        this.x_name1 = x_name1;
    }

    public String getX_name2() {
        return x_name2;
    }

    public void setX_name2(String x_name2) {
        this.x_name2 = x_name2;
    }

    public String getX_name3() {
        return x_name3;
    }

    public void setX_name3(String x_name3) {
        this.x_name3 = x_name3;
    }
}
