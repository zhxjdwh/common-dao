package cn.com.zhxj.common.dao.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分页查询
 */
@Getter
@AllArgsConstructor
public class Pagination {
    private final int pageIndex;
    private final int pageSize;

    public int getStartInclude(){
        return (pageIndex-1)*pageSize;
    }
    public int getEndExclude(){
        return pageIndex*pageSize;
    }
}
