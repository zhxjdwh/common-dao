package cn.com.zhxj.common.dao.entity;

import cn.com.zhxj.common.dao.annotation.InsertTime;
import cn.com.zhxj.common.dao.annotation.UpdateTime;
import cn.com.zhxj.common.dao.core.Entity;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "TD_COMPANY")
public class InsertTestCompanyParent implements Entity {

    @InsertTime
    @Column(name = "FD_CREATETIME")
    private Date   fd_createtime;
    @UpdateTime
    @Column(name = "FD_UPDATETIME")
    private Date   fd_updatetime;
    @Column(name = "FD_DEL")
    private Integer fd_del;

}
