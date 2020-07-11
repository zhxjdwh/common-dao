package cn.com.zhxj.common.dao.core;

import cn.com.zhxj.common.dao.BaseTest;
import cn.com.zhxj.common.dao.entity.TdCompany;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static cn.com.zhxj.common.dao.core.ExprBuilder.*;
import static org.junit.Assert.*;

public class ExprBuilderTest extends BaseTest {

    @Test
    public void orderByExprTest2() {

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(companieIds[0]);
        ids.add(companieIds[1]);
        List<TdCompany> companies = companyDao
                .query()
                .orderByExpr(w -> $(w.getFd_id(), ExprBuilder.desc(), w.getFd_del(), ExprBuilder.asc()))
                .whereExpr(w -> $(w.getFd_id(), ExprBuilder.in(),ids))
                .toList();

        assertEquals(ids.size(),companies.size());
        assertTrue(companies.get(0).getFd_id()>companies.get(1).getFd_id());
    }
}