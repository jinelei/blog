package cn.jinelei.rainbow.blog.authorization.annotation.handler;

import cn.jinelei.rainbow.blog.authorization.annotation.CurrentUser;
import cn.jinelei.rainbow.blog.constant.Constants;
import cn.jinelei.rainbow.blog.entity.TokenEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.exception.enumerate.BlogExceptionEnum;
import cn.jinelei.rainbow.blog.service.TokenService;
import cn.jinelei.rainbow.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        boolean isCurrentUserRequire = true;
        for (Parameter parameter : method.getParameters()) {
            CurrentUser currentUser = parameter.getAnnotation(CurrentUser.class);
            if (currentUser != null) {
                isCurrentUser = true;
                isCurrentUserRequire = currentUser.require();
            }
        }
        if (isCurrentUserRequire && !isCurrentUser) {
            return true;
        }
        String authorizationToken = request.getHeader(Constants.AUTHORIZATION);
        TokenEntity tokenEntity = null;
        if (!StringUtils.isEmpty(authorizationToken)) {
            if (authorizationToken.startsWith("Bearer ")) {
                authorizationToken = authorizationToken.split(" ")[1];
            }
            tokenEntity = tokenService.getToken(authorizationToken);
        }
        if (isCurrentUserRequire && tokenEntity == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        if (tokenEntity != null) {
            request.setAttribute(Constants.CURRENT_USER_ID, tokenEntity.getUserEntity().getUserId());
        }
        request.setAttribute(Constants.REQUIRED_USER, isCurrentUserRequire);
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
        Object currentUserId = request.getAttribute(Constants.CURRENT_USER_ID, RequestAttributes.SCOPE_REQUEST);
        Object requiredUser = request.getAttribute(Constants.REQUIRED_USER, RequestAttributes.SCOPE_REQUEST);
        if (currentUserId != null) {
            return userService.findUserById(Integer.valueOf(currentUserId.toString()));
        } else {
            if (Boolean.getBoolean(requiredUser.toString())) {
                throw new BlogException.Builder(BlogExceptionEnum.USER_NOT_LOGIN, "id: " + currentUserId).build();
            } else {
                return null;
            }
        }
    }
}

