package cn.com.zhxj.common.dao.impl;

import cn.com.zhxj.common.dao.BaseTest;
import cn.com.zhxj.common.dao.core.*;
import cn.com.zhxj.common.dao.entity.TdCompany;
import cn.com.zhxj.common.dao.core.Traced;
import org.junit.Test;

import static cn.com.zhxj.common.dao.core.ExprBuilder.*;
import static org.junit.Assert.*;


public class DefaultUpdatableImplTest extends BaseTest {

    @Test
    public void updateTraceTest(){
        Traced<TdCompany> cp = companyDao.findFirstForUpdate(w -> w.setFd_id(companieIds[0]));
        cp.get().setFd_phone("xiaojun13.");
        cp.get().setFd_erpcode(null);
        assertEquals(cp.getModifies().size(),2);
        int row = companyDao.updateTraced(cp, w -> w.setFd_id(cp.get().getFd_id()));
        assertEquals(1,row);


        TdCompany first = companyDao.findFirst(w -> w.setFd_id(companieIds[0]));
        assertEquals(first.getFd_phone(), cp.get().getFd_phone());
        assertNull(first.getFd_erpcode());
    }


    @Test
    public void updateExprTest(){
        Traced<TdCompany> cp = companyDao.findFirstForUpdate(w -> w.setFd_id(companieIds[0]));
        int row = companyDao.update()
                .setExpr(w->w.getFd_phone(),w->concat(w.getFd_phone(),"1"))
                .whereIdEqual(cp.get().getFd_id())
                .execute();
        assertEquals(1,row);


        TdCompany first = companyDao.findFirst(w -> w.setFd_id(companieIds[0]));
        assertEquals(first.getFd_phone(), cp.get().getFd_phone()+"1");
    }
}
