package cn.jinelei.rainbow.blog.database;

import cn.jinelei.rainbow.blog.BlogApplication;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.UserPrivilege;
import cn.jinelei.rainbow.blog.repository.UserRepository;
import cn.jinelei.rainbow.blog.utils.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "home", classes = BlogApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InsertData {

    @Value("classpath:province-city.json")
    private Resource provinceCityJSON;

    private String[] emails = new String[]{"jinelei@163.com", "jinelei@gmail.com", "52224984@qq.com", "yumely@163.com"};

    @Autowired
    UserRepository userRepository;

    @Test
    public void testInsertUser() throws Exception {
        String res = FileUtils.fileRead(provinceCityJSON.getFile());
        JSONArray jsonArray = new JSONObject(res).getJSONArray("provinces");
        UserEntity userEntity = null;
        Random random = new Random();
        Long phoneNumber = 13100000000L;
        for (int index = 1; index < 21; index++) {
            JSONObject provinceObject = jsonArray.getJSONObject(random.nextInt(jsonArray.length()));
            JSONArray cityArray = provinceObject.getJSONArray("cities");
            userEntity = new UserEntity();
            userEntity.setUsername("username" + index);
            userEntity.setPassword("test");
            userEntity.setNickname("nickname" + index);
            userEntity.setProvince(provinceObject.getString("provinceName"));
            userEntity.setCity(cityArray.getJSONObject(random.nextInt(cityArray.length())).getString("cityName"));
            userEntity.setEmail(emails[random.nextInt(emails.length)]);
            userEntity.setEmail(String.format("username%d@163.com", index));
            userEntity.setPhone(String.valueOf(phoneNumber++));
            switch (random.nextInt(3)) {
                case 0:
                    userEntity.setGroupPrivilege(GroupPrivilege.TOURIST_GROUP);
                    break;
                case 1:
                    userEntity.setGroupPrivilege(GroupPrivilege.NORMAL_GROUP);
                    break;
                case 2:
                    userEntity.setGroupPrivilege(GroupPrivilege.ROOT_GROUP);
                    break;
            }
            switch (random.nextInt(3)) {
                case 0:
                    userEntity.setUserPrivilege(UserPrivilege.TOURIST_USER);
                    break;
                case 1:
                    userEntity.setUserPrivilege(UserPrivilege.NORMAL_USER);
                    break;
                case 2:
                    userEntity.setUserPrivilege(UserPrivilege.ROOT_USER);
                    break;
            }
            userRepository.save(userEntity);
        }
    }

//    @Test
//    public void testCity() throws Exception {
//        System.out.println(provinceCityJSON);
//        String res = FileUtils.fileRead(provinceCityJSON.getFile());
//        JSONObject jsonObject = new JSONObject(res);
//        System.out.println(jsonObject);
//        System.out.println(jsonObject.getJSONArray("四川省"));
//    }

}
