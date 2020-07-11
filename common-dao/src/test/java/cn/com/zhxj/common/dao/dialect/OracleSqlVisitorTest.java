package cn.com.zhxj.common.dao.dialect;


import cn.com.zhxj.common.dao.BaseTest;
import cn.com.zhxj.common.dao.entity.TdCompany;
import cn.com.zhxj.common.dao.expr.BinaryExpr;
import cn.com.zhxj.common.dao.expr.MethodCallExpr;
import cn.com.zhxj.common.dao.core.ExprBuilder;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static cn.com.zhxj.common.dao.core.ExprBuilder.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OracleSqlVisitorTest extends BaseTest {


    @Test
    public void whereLikeTest() {
        List<TdCompany> expr = companyDao.findExpr(w -> $(w.getFd_name(), ExprBuilder.like(), "%" + COMPANY_NAME_PREFIX + "%"));
        assertNotNull(expr);
        assertEquals(companieIds.length, expr.size());

        List<TdCompany> expr2 = companyDao.findExpr(w -> {
            BinaryExpr like = $(w.getFd_name(), ExprBuilder.notLike(), "%" + COMPANY_NAME_PREFIX + "%");
            BinaryExpr in = $(w.getFd_id(), ExprBuilder.eq(), companieIds[0]);
            return $(like, ExprBuilder.and(), in);
        });
        assertNotNull(expr2);
        assertEquals(0, expr2.size());
    }


    @Test
    public void aliasTest() {

        List<TdCompany> tdCompanies1 = companyDao.query()
                .where(w -> w.setFd_id(companieIds[0]))
                .toList(1);


        assertEquals(1, tdCompanies1.size());

        int i = companyDao.updateDynamic(w -> {
            w.setFd_createtime(ExprBuilder.currentDateTime().aliasTo(new Date()));
        }, w -> {
            w.setFd_id(companieIds[0]);
        });

        assertEquals(1, i);

        List<TdCompany> tdCompanies2 = companyDao.query()
                .where(w -> w.setFd_id(companieIds[0]))
                .toList(1);

        assertEquals(1, tdCompanies2.size());
//        assertTrue(tdCompanies2.get(0).getFd_createtime().after(tdCompanies1.get(0).getFd_createtime()));

    }


    @Test
    public void startWithTest() {
        Long count = companyDao.query()
                .whereExpr(w -> $(w.getFd_name(), ExprBuilder.startWith(), COMPANY_NAME_PREFIX))
                .count();
        assertEquals(count.longValue(), companieIds.length);
    }


    @Test
    public void containTest() {
        Long count = companyDao.query()
                .whereExpr(w -> $(w.getFd_name(), ExprBuilder.contain(), COMPANY_NAME_PREFIX))
                .count();
        assertEquals(count.longValue(), companieIds.length);
    }

    @Test
    public void endWithTest() {
        Long count = companyDao.query()
                .whereExpr(w -> $(w.getFd_name(), ExprBuilder.endWith(), COMPANY_NAME_PREFIX.substring(1) + "1"))
                .count();
        assertEquals(count.longValue(), 1L);
    }

    @Test
    public void replaceTest() {
        Long count = companyDao.query()
                .whereExpr(w -> $(ExprBuilder.replace(w.getFd_name(), COMPANY_NAME_PREFIX, "xiaojun"), ExprBuilder.eq(), "xiaojun1"))
                .count();
        assertEquals(count.longValue(), 1L);
    }

    @Test
    public void replaceUpperTest() {
        Long count = companyDao.query()
                .whereExpr(w -> {
                    MethodCallExpr xiaojun = upper(ExprBuilder.replace(w.getFd_name(), COMPANY_NAME_PREFIX, "xiaojun"));
                    return $(xiaojun, ExprBuilder.eq(), "XIAOJUN1");
                })
                .count();
        assertEquals(count.longValue(), 1L);
    }

}
