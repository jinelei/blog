package cn.jinelei.rainbow.blog.exception.handler;


import cn.jinelei.rainbow.blog.entity.TagEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhenlei
 */
@ControllerAdvice
public class GlobalExceptionhandler {

    @ExceptionHandler(value = {BlogException.class})
    @ResponseBody
    @JsonAnyGetter
    public ResponseEntity<BlogException> handleBlogException(BlogException e) {
        return new ResponseEntity<>(e, HttpStatus.resolve(e.getStatus()));
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public ResponseEntity<BlogException> handleException(Exception e) {
        BlogException blogException = new BlogException.Builder(BlogExceptionEnum.UNKNOWN_ERROR, "")
                .setMessage(e.getMessage()).build();
        return new ResponseEntity<>(blogException, HttpStatus.resolve(blogException.getStatus()));
    }


}
