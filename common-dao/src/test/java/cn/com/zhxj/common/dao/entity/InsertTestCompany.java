package cn.com.zhxj.common.dao.entity;

import cn.com.zhxj.common.dao.annotation.IdUtil;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "TD_COMPANY")
public class InsertTestCompany extends InsertTestCompanyParent {

    @Id
    @Column(name = "FD_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "SEQ_TD_COMPANY")
    @SequenceGenerator(name = "SEQ_TD_COMPANY",sequenceName = "SEQ_TEST")
    private Long   fd_id;

    @IdUtil
    @Column(name = "FD_NAME")
    private String fd_name;

    @Column(name = "FD_PHONE")
    private String fd_phone;

    @Column(name = "FD_ERPCODE")
    private String fd_erpcode;

}
