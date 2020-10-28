package com.jutiyunyuan.shirodemo2;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShiroDemo2ApplicationTests {



    @Test
    void test()  {
        System.out.println("hello world");
        System.out.println("didid");
    }

    @Test
    void contextLoads() {
        //原始密码
        String password = "123456";
        //盐
        String salt = "qwer";
        //散列次数
        int hashIterations = 3;
        //方式一
        Md5Hash md5Hash = new Md5Hash(password, salt, hashIterations);
        System.out.println("md5Hash = " + md5Hash);

        //方式二
        SimpleHash md5 = new SimpleHash("md5", password, salt, hashIterations);
        System.out.println("md5 = " + md5);
    }

}
