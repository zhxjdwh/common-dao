package cn.com.zhxj.common.dao.impl;

import cn.com.zhxj.common.dao.BaseTest;
import cn.com.zhxj.common.dao.entity.TdCompany;
import cn.com.zhxj.common.dao.expr.BinaryExpr;
import cn.com.zhxj.common.dao.core.ExprBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static cn.com.zhxj.common.dao.core.ExprBuilder.*;
import static org.junit.Assert.*;

public class DefaultDaoImplTest extends BaseTest {


    @org.junit.Test
    public void insert() {
        TdCompany entity = new TdCompany();
        entity.setFd_name("test33");
        entity.setFd_phone("13656");
        entity.setFd_createtime(new Date());
        entity.setFd_erpcode("erpcodetest");
        entity.setFd_del(0);
        int row = companyDao.insert(entity);
        Assert.assertEquals(row,1);
        assertTrue(entity.getFd_id() > 1L);
    }


    @Test
    public void delete() {
//        TdCompany company1 = companyDao.findFirstNullable(w -> {
//            w.setFd_phone("13656-1");
//        });
//
//        int row = companyDao.delete(w -> {
//            w.setFd_phone("13656-1");
//        });
//
//        TdCompany company2 = companyDao.findFirstNullable(w -> {
//            w.setFd_phone("13656-1");
//        });
//        Assert.assertEquals(row,1);
//        Assert.assertNotNull(company1);
//        Assert.assertNull(company2);
    }

    @Test
    public void deleteWhere() {

//        TdCompany company1 = companyDao.findFirstNullable(w -> {
//            w.setFd_id(companieIds[0]);
//        });
//
//        int row = companyDao.deleteWhere(w -> $(w.getFd_id(), eq(), companieIds[0]));
//
//        TdCompany company2 = companyDao.findFirstNullable(w -> {
//            w.setFd_id(companieIds[0]);
//        });
//        Assert.assertEquals(row,1);
//        Assert.assertNotNull(company1);
//        Assert.assertNull(company2);
    }

    @Test
    public void updateTraced() {
    }

    @Test
    public void updateWhere() {
    }

    @Test
    public void updateDynamicWhere() {
    }

    @Test
    public void updateAllFieldWhere() {
    }

    @Test
    public void updateNotNullWhere() {
    }

    @Test
    public void updateDynamic() {
    }

    @Test
    public void updateAllField() {
    }

    @Test
    public void updateNotNull() {
    }

    @Test
    public void find() {
    }

    @Test
    public void find1() {
    }

    @Test
    public void findLimit() {
    }

    @Test
    public void find2() {

    }

    @Test
    public void findFirst() {
        TdCompany first = companyDao.findExprFirst(w -> {
            BinaryExpr id1 = $(w.getFd_id(), ExprBuilder.eq(), companieIds[0]);
            BinaryExpr id2 = $(w.getFd_id(), ExprBuilder.eq(), companieIds[1]);
            return $(id1, ExprBuilder.or(), id2);
        });
        assertNotNull(first);
    }

    @Test
    public void findFirstNullable() {
        TdCompany first = companyDao.findExprFirst(w -> {
            BinaryExpr id1 = $(w.getFd_id(), ExprBuilder.eq(), companieIds[0]);
            BinaryExpr id2 = $(w.getFd_id(), ExprBuilder.eq(), companieIds[1]);
            return $(id1, ExprBuilder.and(), id2);
        });
        assertNull(first);
    }

    @Test
    public void findExpr() {
        List<TdCompany> list = companyDao.findExpr(w -> $(w.getFd_id(), ExprBuilder.eq(), companieIds[0]));
        assertNotNull(list);
        assertEquals(1,list.size());
    }

    @Test
    public void findExprLimitTest() {
        List<TdCompany> list = companyDao.findExpr(w -> $(w.getFd_id(), ExprBuilder.in(), ExprBuilder.list(companieIds[0],companieIds[1])),1);
        assertNotNull(list);
        assertEquals(1,list.size());
    }

    @Test
    public void findExpr2() {
        List<TdCompany> list = companyDao.findExpr(
                w -> $(w.getFd_id(), ExprBuilder.in(), ExprBuilder.list(companieIds[1],companieIds[0])),
                w-> $(w.getFd_id(), ExprBuilder.asc(),w.getFd_name(), ExprBuilder.desc())
                ,2);
        assertNotNull(list);
        assertEquals(2,list.size());
        assertTrue(list.get(0).getFd_id()<list.get(1).getFd_id());
    }

    @Test
    public void findExprFirst() {
    }

    @Test
    public void findExprFirstNullable() {
    }


    @Test
    public void findExample() {
        TdCompany company = new TdCompany();
        company.setFd_id(companieIds[0]);
        List<TdCompany> list = companyDao.findExample(company);
        assertNotNull(list);
        assertEquals(1,list.size());


        company.setFd_name(companies.get(0).getFd_name());
        List<TdCompany> list1 = companyDao.findExample(company);
        assertNotNull(list1);
        assertEquals(1,list1.size());

        company.setFd_id(companies.get(0).getFd_id());
        company.setFd_name(companies.get(0).getFd_name());
        company.setFd_del(companies.get(0).getFd_del());
        company.setFd_erpcode(companies.get(0).getFd_erpcode());
        List<TdCompany> list2 = companyDao.findExample(company);
        assertNotNull(list2);
        assertEquals(1,list2.size());

    }


    @Test
    public void findExample1() {
    }

    @Test
    public void findExample2() {
    }

    @Test
    public void findFirstByExample() {
    }

    @Test
    public void findFirstByExampleNullable() {
    }

    @Test
    public void findExprForUpdate() {
    }

    @Test
    public void findExprForUpdate1() {
    }

    @Test
    public void findExprForUpdate2() {
    }

    @Test
    public void findExprLimitForUpdate() {
    }

    @Test
    public void findExprFirstForUpdate() {
    }

    @Test
    public void findEqualTest() {
        List<TdCompany> list = companyDao.findEqual(w -> w.getFd_id(), companieIds[0]);
        assertNotNull(list);
        assertEquals(1,list.size());
    }

    @Test
    public void findInTest() {
        List<TdCompany> list = companyDao.findIn(w -> w.getFd_id(), Arrays.stream(companieIds).boxed().collect(Collectors.toList()));
        assertNotNull(list);
        assertEquals(companieIds.length,list.size());
    }

    @Test
    public void findForUpdate() {
//        List<TdCompany> list = companyDao.findEqual(w -> w.getFd_id(), companieIds[0]);
    }

    @Test
    public void findLimitForUpdate() {
    }

    @Test
    public void findForUpdate1() {
    }

    @Test
    public void findFirstForUpdate() {
    }

    @Test
    public void findPage() {
    }
}