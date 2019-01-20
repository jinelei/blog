package cn.jinelei.rainbow.blog.entity;

/**
 * @author zhenlei
 */
public class TokenEntity {
    private UserEntity userEntity;
    private String token;

    public TokenEntity() {
    }

    public TokenEntity(UserEntity userEntity, String token) {
        this.userEntity = userEntity;
        this.token = token;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
