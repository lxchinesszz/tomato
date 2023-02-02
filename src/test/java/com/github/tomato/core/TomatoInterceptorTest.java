package com.github.tomato.core;

import com.github.tomato.annotation.Repeat;
import com.github.tomato.annotation.TomatoToken;
import com.github.tomato.configuration.TomatoAutoConfiguration;
import com.github.tomato.support.DefaultTokenProviderSupport;
import com.github.tomato.support.Phone;
import com.github.tomato.support.TokenProviderSupport;
import com.github.tomato.support.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * @author liuxin
 * 2021/5/16 2:46 下午
 */
public class TomatoInterceptorTest {

    public static class WebController {

        @Repeat
        public void testHttp(@TomatoToken(value = "name", prefix = "ts:") HttpServletRequest httpServletRequest) {

        }

        @Repeat
        public void testObject(@TomatoToken("${name}") User user) {

        }

        @Repeat
        public void testObject2(@TomatoToken("${phone.phoneNo}") User user) {

        }

        @Repeat
        public void testObject3(@TomatoToken("${#c.age}") User user) {

        }

        @Repeat(headValue = "key")
        public void testObject4(User user) {

        }
    }

    @Test
    public void testHttp() throws Exception {
        String expectedValue = "test value in http request parameter";
        TokenProviderSupport tokenProviderSupport = new DefaultTokenProviderSupport();
        Method test = WebController.class.getDeclaredMethod("testHttp", HttpServletRequest.class);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("name", expectedValue);
        String tomatoToken = tokenProviderSupport.findTomatoToken(test, new Object[]{mockHttpServletRequest});
        Assert.assertEquals("ts:" + expectedValue, tomatoToken);
    }

    @Test
    public void testObject() throws Exception {
        TokenProviderSupport tokenProviderSupport = new DefaultTokenProviderSupport();
        Method test = WebController.class.getDeclaredMethod("testObject", User.class);
        User user = new User("lx", 23, new Phone("137123123"));
        String tomatoToken = tokenProviderSupport.findTomatoToken(test, new Object[]{user});
        // name = lx
        System.out.println(tomatoToken);
        Assert.assertEquals(user.getName(), tomatoToken);
    }

    @Test
    public void testObject2() throws Exception {
        TokenProviderSupport tokenProviderSupport = new DefaultTokenProviderSupport();
        Method test = WebController.class.getDeclaredMethod("testObject2", User.class);
        User user = new User("lx2", 23, new Phone("137123123"));
        String tomatoToken = tokenProviderSupport.findTomatoToken(test, new Object[]{user});
        // phone = 137123123
        System.out.println(tomatoToken);
        Assert.assertEquals(user.getPhone().getPhoneNo(), tomatoToken);
    }

    /**
     * #c是变量,参数本身
     */
    @Test
    public void testObject3() throws Exception {
        TokenProviderSupport tokenProviderSupport = new DefaultTokenProviderSupport();
        Method test = WebController.class.getDeclaredMethod("testObject3", User.class);
        User user = new User("lx2", 23, new Phone("137123123"));
        String tomatoToken = tokenProviderSupport.findTomatoToken(test, new Object[]{user});
        // phone = 137123123
        System.out.println(tomatoToken);
        assertEquals((int) user.getAge(), Integer.parseInt(tomatoToken));
    }



    @Test
    public void testHttpHead() throws Exception {
        AnnotationConfigWebApplicationContext app = new AnnotationConfigWebApplicationContext();
        app.register(WebController.class, TomatoAutoConfiguration.class);
        app.refresh();
        WebController bean = app.getBean(WebController.class);
        System.out.println(bean);

//        String expectedValue = "baidu";
//        TokenProviderSupport tokenProviderSupport = new DefaultTokenProviderSupport();
//        Method test = WebController.class.getDeclaredMethod("testObject4", User.class);
//        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
//        mockHttpServletRequest.addHeader("key","baidu");
//        String tomatoToken = tokenProviderSupport.findTomatoToken(test, new Object[]{user});
//        Assert.assertEquals("ts:" + expectedValue, tomatoToken);
    }
}