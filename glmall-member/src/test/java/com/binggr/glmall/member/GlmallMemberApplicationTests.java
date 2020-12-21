package com.binggr.glmall.member;

import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class GlmallMemberApplicationTests {

    @Test
    void contextLoads() {
//        //e10adc3949ba59abbe56e057f20f883e
//        //抗修改性： 彩虹表。 123456->xxx 彩虹表
//        String s = DigestUtils.md5Hex("123456");
//        System.out.println(s);
//        //MD5不能直接进行密码的加密存储；
//
//        //盐值加密：随机值 加盐：$1$+8位字符
//        String s1 = Md5Crypt.md5Crypt("123456".getBytes(),"$1$qqqqqqqq");
//        System.out.println(s1);
        
        //盐值加密器
        //$2a$10$5hnza2S2nlcEYBj1LMgG4uKYa6g77..ru3HrRrEoU8fo8i/9vWjUa
        //$2a$10$8A5GHcO0xs0ckD/Z0F9d6uRkEebgf.YEbDc/rZ9kIdRampk6L0yLO
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");

        boolean matches = passwordEncoder.matches("123456", "$2a$10$5hnza2S2nlcEYBj1LMgG4uKYa6g77..ru3HrRrEoU8fo8i/9vWjUa");

        System.out.println(matches);

    }

}
