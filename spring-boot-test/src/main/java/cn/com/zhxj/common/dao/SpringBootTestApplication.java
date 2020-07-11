package cn.com.zhxj.common.dao;

import cn.com.zhxj.common.dao.service.TestService;
import cn.com.zhxj.common.dao.spring.EnableCommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableCommonDao
@EnableTransactionManagement
public class SpringBootTestApplication {

    @Autowired
    private TestService testService;

    public static void main(String[] args) throws ClassNotFoundException {
//        Class.forName("");
        SpringApplication.run(SpringBootTestApplication.class, args);
    }


    @PostConstruct
    public void test(){
        testService.insert();
    }

}
