package cn.jinelei.rainbow.blog.authorization.annotation.handler;

import cn.jinelei.rainbow.blog.authorization.annotation.Authorization;
import cn.jinelei.rainbow.blog.constant.Constants;
import cn.jinelei.rainbow.blog.entity.TokenEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author zhenlei
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws BlogException {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if (method.getAnnotation(Authorization.class) == null) {
            return true;
        }
        Authorization authorization = method.getAnnotation(Authorization.class);
        if (authorization == null) {
            return true;
        }
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
}
