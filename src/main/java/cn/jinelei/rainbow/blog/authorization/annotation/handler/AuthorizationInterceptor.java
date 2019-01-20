package cn.jinelei.rainbow.blog.authorization.annotation.handler;

import cn.jinelei.rainbow.blog.authorization.annotation.Authorization;
import cn.jinelei.rainbow.blog.constant.Constants;
import cn.jinelei.rainbow.blog.entity.TokenEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.OperatorPrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.UserPrivilege;
import cn.jinelei.rainbow.blog.exception.CustomizeException;
import cn.jinelei.rainbow.blog.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author zhenlei
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws CustomizeException {
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
        // 或者条件
        Authorization.AuthorizationCondition[] orAuthorizationConditions = authorization.orConditions();
        // 并且条件
        Authorization.AuthorizationCondition[] andAuthorizationConditions = authorization.andConditions();
        String authorizationToken = request.getHeader(Constants.AUTHORIZATION);
        if (!StringUtils.isEmpty(authorizationToken)) {
            authorizationToken = authorizationToken.split(" ")[1];
        }
        TokenEntity tokenEntity = tokenService.getToken(authorizationToken);
        if (tokenEntity == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        boolean validateResult = true;
        UserEntity currentUser = tokenEntity.getUserEntity();
        // 检查并且条件
        for (Authorization.AuthorizationCondition condition : andAuthorizationConditions) {
            if (condition.grantGroup() != GroupPrivilege.INVALID_VALUE &&
                    currentUser.getGroupPrivilege().getCode() < condition.grantGroup().getCode()) {
                validateResult = false;
                break;
            }
            switch (condition.grantOperator()) {
                case ONLY_ROOT:
                    if (!currentUser.getUserPrivilege().equals(UserPrivilege.ROOT_USER)) {
                        validateResult = false;
                    }
                    break;
                case ONLY_MYSELF:
                    if (StringUtils.isEmpty(condition.parameterName())) {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }
                    String userId = request.getParameter(condition.parameterName());
                    if (!currentUser.getUserId().equals(Integer.valueOf(userId))) {
                        validateResult = false;
                    }
                    break;
                case INVALID_VALUE:
                    break;
            }
        }
        if (!validateResult) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        validateResult = false;
        // 检查或者条件
        for (Authorization.AuthorizationCondition condition : orAuthorizationConditions) {
            if (condition.grantGroup() != GroupPrivilege.INVALID_VALUE &&
                    currentUser.getGroupPrivilege().getCode() >= condition.grantGroup().getCode()) {
                validateResult = true;
                break;
            }
            switch (condition.grantOperator()) {
                case ONLY_ROOT:
                    if (currentUser.getUserPrivilege().equals(UserPrivilege.ROOT_USER)) {
                        validateResult = true;
                    }
                    break;
                case ONLY_MYSELF:
                    if (StringUtils.isEmpty(condition.parameterName())) {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return false;
                    }
                    String userId = request.getParameter(condition.parameterName());
                    if (currentUser.getUserId().equals(Integer.valueOf(userId))) {
                        validateResult = true;
                    }
                    break;
                case INVALID_VALUE:
                    break;
            }
        }
        if (validateResult) {
            request.setAttribute(Constants.CURRENT_USER_ID, tokenEntity.getUserEntity().getUserId());
            return true;
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

    }
}
