package cn.com.zhxj.common.dao.test;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "TD_COMPANY")
public class TdCompany  extends TdCompanyParent {

    @Id
    @Column(name = "FD_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "SEQ_TD_COMPANY")
    @SequenceGenerator(name = "SEQ_TD_COMPANY",sequenceName = "SEQ_TEST")
    private Long   fd_id;


    @Column(name = "FD_PHONE")
    private String fd_phone;

    @Column(name = "FD_ERPCODE")
    private String fd_erpcode;
    @Column(name = "FD_DEL")
    private Integer fd_del;





}
