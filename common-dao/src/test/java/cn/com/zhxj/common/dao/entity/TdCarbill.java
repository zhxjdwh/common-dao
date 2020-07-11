package cn.com.zhxj.common.dao.entity;

import cn.com.zhxj.common.dao.core.Entity;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "TD_CARBILL",schema = "ecom2g")
public class TdCarbill implements Entity {
    @Column(name = "FD_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TD_CARBILL")
    @SequenceGenerator(name = "SEQ_TD_CARBILL", sequenceName = "SEQ_TD_CARBILL")
    private Long   fd_id;
    @Column(name = "FD_CODE")
    private String fd_code;
    @Column(name = "FD_DATE")
    private Date   fd_date;
}
