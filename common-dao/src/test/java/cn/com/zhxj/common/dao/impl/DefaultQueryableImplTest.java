package cn.com.zhxj.common.dao.impl;

import cn.com.zhxj.common.dao.BaseTest;
import cn.com.zhxj.common.dao.OrdCarSendRecord;
import cn.com.zhxj.common.dao.Queryable;
import cn.com.zhxj.common.dao.entity.TdCompany;
import cn.com.zhxj.common.dao.core.*;
import cn.com.zhxj.common.dao.expr.OrderEntryExpr;
import cn.com.zhxj.common.dao.expr.QueryExpr;
import cn.com.zhxj.common.dao.expr.SqlExpr;
import cn.com.zhxj.common.dao.core.*;
import org.junit.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static cn.com.zhxj.common.dao.core.ExprBuilder.*;
import static org.junit.Assert.*;


public class DefaultQueryableImplTest extends BaseTest {

    @Test
    public void toTuple1() {
        List<Tuple1<Long>> tuple1s = companyDao.query()
                .where(w -> w.setFd_name("xj-unittest-1"))
                .toTuple1(w -> ExprBuilder.tuple(w.getFd_id()));

        Assert.assertEquals(1,tuple1s.size());
        assertNotNull(tuple1s.get(0).getF1());
    }

    @Test
    public void toTuple2() {
        List<Tuple2<Long, String>> tuple2s = companyDao.query()
                .where(w -> w.setFd_name("xj-unittest-1"))
                .toTuple2(w -> ExprBuilder.tuple(w.getFd_id(), w.getFd_phone()));

        Assert.assertEquals(1,tuple2s.size());
        assertNotNull(tuple2s.get(0).getF1());
        assertNotNull(tuple2s.get(0).getF2());
    }

    @Test
    public void toTuple3() {
    }

    @Test
    public void toTuple4() {
    }

    @Test
    public void toTuple5() {
    }

    @Test
    public void toTuple6() {
    }

    @Test
    public void toTuple7() {
    }

    @Test
    public void count() {
        Long aLong = companyDao.query()
                .whereExpr(w -> $(w.getFd_name(), ExprBuilder.in(), new String[]{"xj-unittest-1", "xj-unittest-2"}))
                .count();
        assertEquals(2L,aLong.longValue());
    }

    @Test
    public void countDistinct() {
        Long aLong = companyDao.query()
                .whereExpr(w -> $(w.getFd_name(), ExprBuilder.in(), new String[]{"xj-unittest-1", "xj-unittest-2"}))
                .countDistinct(w -> list(w.getFd_del()));
        assertEquals(2L,aLong.longValue());
    }

    @Test
    public void distinct() {
        List<TdCompany> distinct = companyDao.query()
                .whereExpr(w -> $(w.getFd_name(), ExprBuilder.in(), new String[]{"xj-unittest-1", "xj-unittest-2"}))
                .distinct(w -> ExprBuilder.list(w.getFd_id(), w.getFd_name()));
        assertNotNull(distinct);
        assertTrue(distinct.size()>0);

        List<TdCompany> distinct2 = companyDao.query()
                .whereExpr(w -> $(w.getFd_del(), ExprBuilder.in(), new Integer[]{1,0}))
                .distinct(w -> list(w.getFd_del()));
        assertNotNull(distinct2);
        assertEquals(2, distinct2.size());
    }


    @Test
    public void whereExampleExcludeTest() {
        TdCompany cp = new TdCompany();
        cp.setFd_id(companieIds[0]);
        cp.setFd_name(UUID.randomUUID().toString());
        TdCompany first = companyDao.query()
                .whereExampleExclude(cp, ExamplePolicy.EXCLUDE_NULL_EMPTY_BLANK, w -> list(w.getFd_name()))
                .first();
        assertNotNull(first);
    }

    @Test
    public void sum() {
        long[] ids = companies.stream().mapToLong(TdCompany::getFd_id).toArray();

        BigDecimal sum = companyDao.query()
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.in(),ids))
                .sum(w -> ExprBuilder.tuple(w.getFd_id()));

        long sum1 = companies.stream().mapToLong(w -> w.getFd_id()).sum();

        assertEquals(sum.longValue(),sum1);

    }

    @Test
    public void avg() {
        long[] ids = companies.stream().mapToLong(TdCompany::getFd_id).toArray();

        BigDecimal avg2 = companyDao.query()
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.in(),ids))
                .avg(w -> ExprBuilder.tuple(w.getFd_id()));

        double avg3 = companies.stream().mapToLong(w -> w.getFd_id()).average().getAsDouble();

        assertEquals(avg2.doubleValue(),avg3,0.5);
    }

    @Test
    public void min() {
        long[] ids = companies.stream().mapToLong(TdCompany::getFd_id).toArray();

        BigDecimal v1 = companyDao.query()
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.in(),ids))
                .min(w -> ExprBuilder.tuple(w.getFd_id()));

        long v2 = companies.stream().mapToLong(w -> w.getFd_id()).min().getAsLong();

        assertEquals(v1.longValue(),v2);

    }

    @Test
    public void max() {
        long[] ids = companies.stream().mapToLong(TdCompany::getFd_id).toArray();

        BigDecimal v1 = companyDao.query()
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.in(),ids))
                .max(w -> ExprBuilder.tuple(w.getFd_id()));

        long v2 = companies.stream().mapToLong(w -> w.getFd_id()).max().getAsLong();

        assertEquals(v1.longValue(),v2);

    }

    @Test
    public void subQueryTest() {

        Queryable<TdCompany> queryable = companyDao.query()
                .where(w -> w.setFd_id(companieIds[0]))
                .orderByAsc(w-> list(w.getFd_id()))
                .orderByDesc(w-> list(w.getFd_del()))
                .select(w -> list(w.getFd_id()));


        TdCompany first = companyDao.query()
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.in(), queryable))
                .first();


        assertNotNull(first);

    }

    @Test
    public void exists() {
        boolean exists = companyDao.query()
                .where(w -> {
                    w.setFd_id(companies.get(0).getFd_id());
                    w.setFd_name(companies.get(0).getFd_name());
                })
                .exists();
        assertTrue(exists);
    }

    @Test
    public void first() {
        TdCompany first = companyDao.query()
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.in(), new Long[]{companies.get(0).getFd_id(), companies.get(1).getFd_id()}))
                .first();

        assertNotNull(first);
        assertEquals(companies.get(0).getFd_id(),first.getFd_id());
    }

    @Test
    public void top() {
        List<TdCompany> tops = companyDao.query()
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.in(), new Long[]{companies.get(0).getFd_id(), companies.get(1).getFd_id()}))
                .top(1);

        assertNotNull(tops);
        assertEquals(tops.size(),1);
        assertEquals(companies.get(0).getFd_id(),tops.get(0).getFd_id());
    }

    @Test
    public void toList() {
        List<TdCompany> tops = companyDao.query()
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.in(), new Long[]{companies.get(0).getFd_id(), companies.get(1).getFd_id()}))
                .toList();

        assertNotNull(tops);
        assertEquals(tops.size(),2);
        assertEquals(companies.get(0).getFd_id(),tops.get(0).getFd_id());
    }

    @Test
    public void toStream() {
        List<TdCompany> tops = companyDao.query()
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.in(), new Long[]{companies.get(0).getFd_id(), companies.get(1).getFd_id()}))
                .toStream().collect(Collectors.toList());

        assertNotNull(tops);
        assertEquals(tops.size(),2);
        assertEquals(companies.get(0).getFd_id(),tops.get(0).getFd_id());
    }

    @Test
    public void toList1() {
    }

    @Test
    public void toPage() {
        long[] ids = companies.stream().mapToLong(w -> w.getFd_id()).sorted().toArray();
        Paged<TdCompany> page = companyDao.query()
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.in(), ids))
                .orderByAsc(w-> list(w.getFd_id()))
                .toPage(2, 3);
        assertNotNull(page);
        assertEquals(ids.length,page.getRowCount());
        assertEquals(2,page.getPageIndex());
        assertEquals(3,page.getPageSize());
        assertNotNull(page.getData());
        assertEquals(3,page.getData().size());
        assertEquals(ids[3],page.getData().get(0).getFd_id().longValue());
        assertEquals(ids[5],page.getData().get(2).getFd_id().longValue());
    }

    @Test
    public void toPageHllpTest() {
        Paged<OrdCarSendRecord> page = ordCarSendRecordDao.query()
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.isNotNull()))
//                .orderByAsc(w->$list(w.getFd_id()))
                .toPageHLLP(2, 5, OrdCarSendRecord::getFd_id);

        System.out.println(page.getRowCount());

    }

    @Test
    public void toPageCountLess() {
        long[] ids = companies.stream().mapToLong(w -> w.getFd_id()).sorted().toArray();
        List<TdCompany> page = companyDao.query()
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.in(), ids))
                .orderByAsc(w-> list(w.getFd_id()))
                .toPageCountLess(3, 3);
        assertNotNull(page);
        assertEquals(3,page.size());
        assertEquals(ids[6],page.get(0).getFd_id().longValue());
        assertEquals(ids[8],page.get(2).getFd_id().longValue());
    }

    @Test
    public void getQueryExpr() {
    }

    @Test
    public void specialValueTest() {
//        companyDao.updateDynamic(w->{
//            w.setFd_createtime(SpecialValue.currentTimestamp());
//        },w-> w.setFd_id(companieIds[0]));
//
//        TdCompany company = companyDao.findFirst(w -> w.setFd_id(companieIds[0]));
//        assertNotNull(company);
//        assertNotEquals(company.getFd_createtime(),);

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//        }
//        TdCompany first = companyDao.query()
//                .whereExpr(w -> $(w.getFd_createtime(), in(), $list(SpecialValue.currentDateTime())))
//                .first();
//
//        assertNull(first);

//        List<Tuple2<TdCompany, Tuple2<Long, Long>>> tuple2s = companyDao.query()
//                .groupBy2(w -> $tuple($sum(w.getFd_id()), $count(w.getFd_id())))
//                .toTuple();
//
//        for (Tuple2<TdCompany, Tuple2<Long, Long>> tuple2 : tuple2s) {
//            TdCompany f1 = tuple2.getF1();
//            Tuple2<Long, Long> f2 = tuple2.getF2();
//
//        }

    }

    @Test
    public void whereExpr() {
    }

    @Test
    public void where() {
    }

    @Test
    public void orderByFields() {
    }

    @Test
    public void orderByAsc() {
    }

    @Test
    public void orderByDesc() {
    }

    @Test
    public void orderByExprs() {
    }

    @Test
    public void orderByExpr() {
    }

    @Test
    public void orderByMap() {
    }

    @Test
    public void orderByMap1() {
    }

    @Test
    public void unsafeOrderByMap() {
        QueryExpr desc = companyDao.query()
                .where(w -> w.setFd_id(companieIds[0]))
                .unsafeOrderByMap(w -> {
                    w.put("fd_id+1", "desc");
                }).getQueryExpr();
        assertNotNull(desc);
        assertNotNull(desc.getOrderBy());
        assertNotNull(desc.getOrderBy().getOrderEntryExprs());
        OrderEntryExpr[] orderEntryExprs = desc.getOrderBy().getOrderEntryExprs();
        assertEquals(1,orderEntryExprs.length);
        assertEquals(true,orderEntryExprs[0].isDesc());
        assertTrue(orderEntryExprs[0].getExpr() instanceof SqlExpr);

        SqlExpr expr = (SqlExpr) orderEntryExprs[0].getExpr();
        assertEquals("fd_id+1",expr.getSql());

        List<TdCompany> desc1 = companyDao.query()
                .whereExpr(w->$(w.getFd_id(), ExprBuilder.isNotNull()))
                .unsafeOrderByMap(w -> {
                    w.put("fd_id+1", "desc");
                }).toList(2);

        assertNotNull(desc1);
        assertEquals(2,desc1.size());
        assertTrue(desc1.get(0).getFd_id()>desc1.get(1).getFd_id());
    }

    @Test
    public void selectFields() {
        TdCompany first = companyDao.query()
                .where(w -> w.setFd_id(companieIds[0]))
                .select(w -> ExprBuilder.list(w.getFd_name(), w.getFd_id()))
                .first();

        assertNotNull(first);
        assertNotNull(first.getFd_name());
        assertNotNull(first.getFd_id());
        assertNull(first.getFd_createtime());
        assertNull(first.getFd_phone());
        assertNull(first.getFd_del());
    }

    @Test
    public void select() {
        TdCompany first = companyDao.query()
                .where(w -> w.setFd_id(companieIds[0]))
                .select(w-> ExprBuilder.list(w.getFd_id(),w.getFd_name()))
                .first();

        assertNotNull(first);
        assertNotNull(first.getFd_name());
        assertNotNull(first.getFd_id());
        assertNull(first.getFd_createtime());
        assertNull(first.getFd_phone());
        assertNull(first.getFd_del());

    }

    @Test
    public void selectExclude() {

        TdCompany first = companyDao.query()
                .where(w -> w.setFd_id(companieIds[0]))
                .selectExclude(w-> ExprBuilder.list(w.getFd_createtime(),w.getFd_phone()))
                .first();

        assertNotNull(first);
        assertNotNull(first.getFd_name());
        assertNotNull(first.getFd_del());
        assertNotNull(first.getFd_id());
        assertNull(first.getFd_createtime());
        assertNull(first.getFd_phone());

    }

    @Test
    public void selectDistinct() {

        List<TdCompany> tdCompanies = companyDao.query()
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.in(), new long[]{companieIds[0],  companieIds[2]}))
                .selectDistinct(w -> list(w.getFd_del()))
                .toList();

        assertNotNull(tdCompanies);

        assertEquals(1,tdCompanies.size());

        TdCompany first = tdCompanies.get(0);
        assertNull(first.getFd_name());
        assertNotNull(first.getFd_del());
        assertNull(first.getFd_id());
        assertNull(first.getFd_createtime());
        assertNull(first.getFd_phone());
    }

}
