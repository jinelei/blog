package cn.jinelei.rainbow.blog.authorization.annotation.handler;

import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.constant.Constants;
import cn.jinelei.rainbow.blog.entity.TokenEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.service.TokenService;
import cn.jinelei.rainbow.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author zhenlei
 */
@Component
public class CurrentUserArgumentResolver extends HandlerInterceptorAdapter implements HandlerMethodArgumentResolver {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws BlogException {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        boolean isCurrentUser = false;
        for (Parameter parameter : method.getParameters()) {
            if (parameter.getAnnotation(CurrentUser.class) != null) {
                isCurrentUser = true;
            }
        }
        if(isCurrentUser){
           return true;
        }
//        if (method.getAnnotation(Authorization.class) == null) {
//            return true;
//        }
//        Authorization authorization = method.getAnnotation(Authorization.class);
//        if (authorization == null) {
//            return true;
//        }
        String authorizationToken = request.getHeader(Constants.AUTHORIZATION);
        if (!StringUtils.isEmpty(authorizationToken)) {
            authorizationToken = authorizationToken.split(" ")[1];
        }
        TokenEntity tokenEntity = tokenService.getToken(authorizationToken);
        if (tokenEntity == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        request.setAttribute(Constants.CURRENT_USER_ID, tokenEntity.getUserEntity().getUserId());
        return true;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(UserEntity.class) &&
                methodParameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer container,
                                  NativeWebRequest request,
                                  WebDataBinderFactory factory) throws BlogException {
        String currentUserId = (String) request.getAttribute(Constants.CURRENT_USER_ID, RequestAttributes.SCOPE_REQUEST);
        if (currentUserId != null) {
            return userService.findUserById(Integer.valueOf(currentUserId));
        } else {
            throw new BlogException.UserNotLogin();
        }
    }
}

