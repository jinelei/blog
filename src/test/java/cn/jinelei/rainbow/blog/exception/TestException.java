package cn.jinelei.rainbow.blog.exception;

import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
import org.junit.Test;

public class TestException {

    @Test
    public void testException() {
        BlogException exception = new BlogException.Builder()
                .setExceptionEnum(BlogExceptionEnum.UNKNOWN_ERROR)
                .build();
        System.out.println(exception);
        System.out.println(exception.getClass());
        exception = new BlogException.Builder()
                .setExceptionEnum(BlogExceptionEnum.USERNAME_OR_PASSWORD_WRONG)
                .build();
        System.out.println(exception.getClass());
    }
}
