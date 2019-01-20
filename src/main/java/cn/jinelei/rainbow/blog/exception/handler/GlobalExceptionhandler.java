package cn.jinelei.rainbow.blog.exception.handler;


import cn.jinelei.rainbow.blog.exception.CustomizeException;
import cn.jinelei.rainbow.blog.view.ErrorView;
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

    @ExceptionHandler(value = {CustomizeException.class})
    @ResponseBody
    public ResponseEntity<ErrorView> handleCustomizeException(CustomizeException e) {
        return new ResponseEntity<ErrorView>(new ErrorView(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public ResponseEntity<Exception> handleException(Exception e) {
        return new ResponseEntity<Exception>(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
