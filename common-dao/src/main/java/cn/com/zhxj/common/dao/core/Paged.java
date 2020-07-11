package cn.com.zhxj.common.dao.core;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 分页查询结果
 */
@Getter
@Setter
public class Paged<T> {
    private  int     pageIndex;
    private  int     pageSize;
    private  int     rowCount;
    private  List<T> data;
    public static <T> Paged<T> create(Pagination pagination,int totalRow,List<T> data){
        return create(pagination.getPageIndex(),pagination.getPageSize(),totalRow,data);
    }
    public static <T> Paged<T> create(int pageIndex,int pageSize,int rowCount,List<T> data){
        Paged paged = new Paged();
        paged.pageIndex=pageIndex;
        paged.pageSize=pageSize;
        paged.rowCount=rowCount;
        paged.data=data;
        return paged;
    }
}
