package cn.com.zhxj.common.dao;

import cn.com.zhxj.common.dao.core.Entity;
import cn.com.zhxj.common.dao.entity.InsertTestCompany;
import cn.com.zhxj.common.dao.entity.TdCompany;
import cn.com.zhxj.common.dao.impl.DefaultDaoImpl;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseTest {

    protected static  JdbcTemplate                      jdbcTemplate;
    protected  static Connection                        connection;
    protected  static DefaultDaoImpl<TdCompany>         companyDao;
    protected         List<TdCompany>                   companies   =new ArrayList<>();
    protected         long[]                            companieIds =null;
    protected         DefaultDaoImpl<OrdCarSendRecord>  ordCarSendRecordDao;
    protected         DefaultDaoImpl<InsertTestCompany> insertTestCompanyDao;

    public static String COMPANY_NAME_PREFIX="xj-unittest-";
    public static String COMPANY_ERPCODE_PREFIX="erpcodetest_";


    @Before
    public  void before() throws SQLException {
        System.setProperty("user.timezone","GMT+8");
        System.setProperty("oracle.jdbc.J2EE13Compliant","true");
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource("jdbc:oracle:thin:@xxxxxx:1521:xxx","xxxx","xxxxx");
        jdbcTemplate=new JdbcTemplate(driverManagerDataSource);
        connection = driverManagerDataSource.getConnection();
        connection.setAutoCommit(false);
        ConnectionHolder connectionHolder = new ConnectionHolder(connection, true);
        TransactionSynchronizationManager.bindResource(driverManagerDataSource,connectionHolder);
        companyDao=new DefaultDaoImpl<>(TdCompany.class,new TestSqlDriverHolder(jdbcTemplate));
        ordCarSendRecordDao=new DefaultDaoImpl<>(OrdCarSendRecord.class,new TestSqlDriverHolder(jdbcTemplate));
        insertTestCompanyDao=new DefaultDaoImpl<>(InsertTestCompany.class,new TestSqlDriverHolder(jdbcTemplate));
        prepareData();
    }

    public <T extends Entity> Dao<T> createDao(Class<T> tClass){
        return new DefaultDaoImpl<>(tClass,new TestSqlDriverHolder(jdbcTemplate));
    }

    private  void prepareData(){
        for (int i = 0; i < 10; i++) {
            TdCompany entity = new TdCompany();
            entity.setFd_name(COMPANY_NAME_PREFIX+i);
            entity.setFd_phone("13656-"+i);
            entity.setFd_createtime(new Date());
            entity.setFd_erpcode(COMPANY_ERPCODE_PREFIX+i);
            entity.setFd_del(i%2);
            int row = companyDao.insert(entity);
            companies.add(entity);
        }
        companieIds=companies.stream().mapToLong(TdCompany::getFd_id).toArray();
    }

    @After
    public  void after() throws SQLException {
        connection.rollback();
    }
}
