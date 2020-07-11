package cn.com.zhxj.common.dao.service;

import cn.com.zhxj.common.dao.Dao;
import cn.com.zhxj.common.dao.test.TdCompany;
import cn.com.zhxj.common.dao.core.ExprBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static cn.com.zhxj.common.dao.core.ExprBuilder.$;

@Service
public class TestService {

    @Autowired
    private Dao<TdCompany> companyDao;



    public List<TdCompany> getFirst(){
        List<TdCompany> tdCompanies = companyDao.query()
                .whereExpr(w -> ExprBuilder.$(w.getFd_id(), ExprBuilder.isNotNull()))
                .toList(1);
        return tdCompanies;
    }

    @Transactional
    public int insert(){
        int row=0;
        for (int i = 0; i < 10; i++) {
            TdCompany entity = new TdCompany();
            entity.setFd_name("test_compan"+i);
            entity.setFd_phone("13656-"+i);
            entity.setFd_createtime(new Date());
            entity.setFd_erpcode("test_erpcode"+i);
            entity.setFd_del(i%2);
            row+= companyDao.insert(entity);
            System.out.println(row);
        }
        return row;
    }


}
