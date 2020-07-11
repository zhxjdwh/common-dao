package cn.com.zhxj.common.dao;

import cn.com.zhxj.common.dao.core.Entity;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "ORD_CAR_SEND_RECORD")
public class OrdCarSendRecord implements Entity {
    @Id
    @Column(name = "FD_ID")
    private String   fd_id;
    @Column(name = "FD_ORDERCODE")
    private String fd_ordercode;
}
