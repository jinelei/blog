package cn.jinelei.rainbow.blog.exception.handler;


import cn.jinelei.rainbow.blog.entity.TagEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
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
        return new ResponseEntity<BlogException>(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public ResponseEntity<Exception> handleException(Exception e) {
        return new ResponseEntity<Exception>(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
