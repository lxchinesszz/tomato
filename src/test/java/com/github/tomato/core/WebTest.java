package com.github.tomato.core;

import com.github.tomato.annotation.Repeat;
import com.github.tomato.annotation.TomatoToken;
import com.github.tomato.support.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liuxin
 * 2023/1/31 20:11
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//开启虚拟MVC调用
@AutoConfigureMockMvc
public class WebTest {


    @Component
    public static class WebController {

        @GetMapping("/key")
        @Repeat(headValue = "key")
        public String testObject4(User user) {
            return "123";
        }
    }

    @Autowired
    MockMvc mvc;
    @Test
    //注入虚拟MVC调用对象
    public void testWeb() throws Exception {
        //创建虚拟请求，当前访问/books
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/key");
    }
}
