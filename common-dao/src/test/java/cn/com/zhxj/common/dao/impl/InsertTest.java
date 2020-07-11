package cn.com.zhxj.common.dao.impl;

import cn.com.zhxj.common.dao.BaseTest;
import cn.com.zhxj.common.dao.entity.InsertTestCompany;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class InsertTest extends BaseTest {


    private InsertTestCompany insertOne(){
        InsertTestCompany entity = new InsertTestCompany();
        entity.setFd_name(UUID.randomUUID().toString());
        entity.setFd_phone(UUID.randomUUID().toString().substring(0,13));
        entity.setFd_createtime(new Date());
        entity.setFd_erpcode("erpcodetest");
        entity.setFd_del(0);
        int row = insertTestCompanyDao.insert(entity);
        return entity;
    }

    @Test
    public void insertIdUtilTest() {
        InsertTestCompany entity = new InsertTestCompany();
        entity.setFd_createtime(new Date());
        entity.setFd_erpcode("erpcodetest");
        entity.setFd_del(0);
        int row = insertTestCompanyDao.insert(entity);
        Assert.assertEquals(row,1);
        assertTrue(entity.getFd_id() > 1L);
        assertTrue(entity.getFd_name().length()>10);
    }

    @Test
    public void batchUpdateTest() {
        InsertTestCompany insert1 = insertOne();
        InsertTestCompany insert2 = insertOne();

        InsertTestCompany entity = new InsertTestCompany();
        entity.setFd_id(insert1.getFd_id());
        entity.setFd_createtime(null);
        entity.setFd_updatetime(null);
        entity.setFd_erpcode(null);
        entity.setFd_name("xiaojun");
        entity.setFd_del(0);

        InsertTestCompany entity2 = new InsertTestCompany();
        entity2.setFd_id(insert2.getFd_id());
        entity2.setFd_createtime(null);
        entity2.setFd_updatetime(null);
        entity2.setFd_erpcode(null);
        entity2.setFd_name("xiaojun");
        entity2.setFd_del(0);

        insertTestCompanyDao.updateBatch(Arrays.asList(entity,entity2));

        InsertTestCompany first = insertTestCompanyDao.findFirst(w -> w.setFd_id(entity.getFd_id()));
        assertNotNull(first);
        assertEquals(first.getFd_name(),entity.getFd_name());

        InsertTestCompany sec = insertTestCompanyDao.findFirst(w -> w.setFd_id(entity2.getFd_id()));
        assertNotNull(sec);
        assertEquals(sec.getFd_name(),entity2.getFd_name());
    }

    @Test
    public void updateUpdateTimeTest() {
        InsertTestCompany insert1 = insertOne();

        int row = insertTestCompanyDao.update()
                .whereIdEqual(insert1.getFd_id())
                .set(w -> {
                    w.setFd_name("test445");
                    w.setFd_updatetime(DateUtils.addDays(new Date(),-10));
                }).execute();

        assertEquals(row,1);

        InsertTestCompany cp = insertTestCompanyDao.findById(insert1.getFd_id());

        assertNotNull(cp);
        assertTrue(cp.getFd_updatetime().after(DateUtils.addMinutes(new Date(),-60)));
    }

    @Test
    public void updateUpdateTimeFillTest() {
        InsertTestCompany insert1 = insertOne();
        int row = insertTestCompanyDao.update()
                .whereIdEqual(insert1.getFd_id())
                .setNotNull(insert1)
                .execute();

        assertEquals(row,1);

        InsertTestCompany cp = insertTestCompanyDao.findById(insert1.getFd_id());

        assertNotNull(cp);
        assertTrue(cp.getFd_updatetime().after(DateUtils.addMinutes(new Date(),-60)));
    }


    @Test
    public void batchInsertInsertTimeTest() {
//        String erpCode=UUID.randomUUID().toString();
//        Date date5m = DateUtils.addMinutes(new Date(), -5);
//        List<InsertTestCompany> entitys=new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            InsertTestCompany entity = new InsertTestCompany();
//            entity.setFd_createtime(new Date());
//            entity.setFd_erpcode(erpCode);
//            entity.setFd_del(0);
//            entitys.add(entity);
//        }
//        insertTestCompanyDao.insertBatch(entitys);
//
//        String[] longs = entitys.stream().map(InsertTestCompany::getFd_erpcode).toArray(String[]::new);
//        Long count = insertTestCompanyDao.query()
//                .whereExpr(w -> $(w.getFd_erpcode(), in(),longs))
//                .count();
//
//        Assert.assertEquals(count.intValue(),entitys.size());
//        assertTrue(entitys.stream().allMatch(w->w.getFd_id()!=null));
//        assertTrue(entitys.stream().allMatch(w->w.getFd_createtime().after(date5m)));
    }

    @Test
    public void insertInsertTimeTest() {
//        Date date10 = DateUtils.addDays(new Date(), -10);
//        Date date9 = DateUtils.addDays(new Date(), -9);
//        Date date5m = DateUtils.addMinutes(new Date(), -5);
//        InsertTestCompany entity = new InsertTestCompany();
//        entity.setFd_createtime(new Date());
//        entity.setFd_erpcode("erpcodetest");
//        entity.setFd_del(0);
//        entity.setFd_createtime(date10);
//        int row = insertTestCompanyDao.insert(entity);
//        Assert.assertEquals(row,1);
//        assertTrue(entity.getFd_id() > 1L);
//        assertTrue(entity.getFd_createtime().after(date9));
//        assertTrue(insertTestCompanyDao.findFirst(w->w.setFd_id(entity.getFd_id())).getFd_createtime().after(date5m));
    }





}