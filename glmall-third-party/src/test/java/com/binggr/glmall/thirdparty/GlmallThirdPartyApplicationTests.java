package com.binggr.glmall.thirdparty;

import com.aliyun.oss.OSSClient;
import com.binggr.glmall.thirdparty.component.SmsComponent;
import com.binggr.glmall.thirdparty.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;

@SpringBootTest
class GlmallThirdPartyApplicationTests {

    @Resource //@Autowired
            OSSClient ossClient;

    @Autowired
    SmsComponent smsComponent;

    @Test
    void sendCode(){
        smsComponent.sendCode("14708285181","666666");
    }





    @Test
    void testUpload() throws FileNotFoundException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
//        String endpoint = "http://oss-cn-chengdu.aliyuncs.com";
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//        String accessKeyId = "LTAI4G23DLaTZhH8EUFgRrpL";
//        String accessKeySecret = "zBpPY2QZsrFC3hUHoPa9ivVV6WG0F8";

        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\bing\\Desktop\\photo\\miku.jpg");
        ossClient.putObject("glmall-binggr", "miku2.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        out.println("上传成功!");
    }
}
