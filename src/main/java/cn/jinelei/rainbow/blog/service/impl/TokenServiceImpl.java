package cn.jinelei.rainbow.blog.service.impl;

import cn.jinelei.rainbow.blog.entity.TokenEntity;
import cn.jinelei.rainbow.blog.entity.UserEntity;
import cn.jinelei.rainbow.blog.exception.BlogException;
import cn.jinelei.rainbow.blog.repository.TokenRepository;
import cn.jinelei.rainbow.blog.service.TokenService;
import cn.jinelei.rainbow.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

/**
 * @author zhenlei
 */
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    UserService userService;

    @Autowired
    TokenRepository tokenRepository;

    @Override
    public TokenEntity createToken(UserEntity userEntity) {
        Optional<TokenEntity> optional = tokenRepository.findTokenEntityByUserEntity(userEntity);
        if (optional.isPresent()) {
            tokenRepository.delete(optional.get());
        }
        String uuid = UUID.randomUUID().toString().replace("-", "").toString();
        Instant now = Instant.now();
        TokenEntity tokenEntity = new TokenEntity(
                userEntity,
                uuid,
                now.toEpochMilli(),
                now.plus(1L, ChronoUnit.DAYS).toEpochMilli());
        TokenEntity operateResult = tokenRepository.save(tokenEntity);
        return operateResult;
    }

    @Override
    public TokenEntity getToken(String token) throws BlogException {
        Optional<TokenEntity> queryResult = tokenRepository.findById(token);
        if (queryResult.isPresent()) {
            TokenEntity tokenEntity = queryResult.get();
            Instant expriyDate = Instant.ofEpochMilli(tokenEntity.getExpiryDate());
            Instant effectiveDate = Instant.ofEpochMilli(tokenEntity.getEffectiveDate());
            Instant now = Instant.now();
            if (now.isBefore(effectiveDate)) {
                throw new BlogException.TokenNotEffective();
            } else if (now.isAfter(expriyDate)) {
                throw new BlogException.TokenIsExpired();
            } else {
                return tokenEntity;
            }
        } else {
            throw new BlogException.UserNotLogin();
        }
    }

    @Override
    public void deleteToken(String token) throws BlogException {
        Optional<TokenEntity> queryResult = tokenRepository.findById(token);
        if (queryResult.isPresent()) {
            TokenEntity tokenEntity = queryResult.get();
            Instant expriyDate = Instant.ofEpochMilli(tokenEntity.getExpiryDate());
            Instant effectiveDate = Instant.ofEpochMilli(tokenEntity.getEffectiveDate());
            Instant now = Instant.now();
            if (now.isBefore(effectiveDate)) {
                throw new BlogException.TokenNotEffective();
            } else if (now.isAfter(expriyDate)) {
                throw new BlogException.TokenIsExpired();
            } else {
                tokenRepository.delete(queryResult.get());
            }
        } else {
            throw new BlogException.UserNotLogin();
        }
    }
}
