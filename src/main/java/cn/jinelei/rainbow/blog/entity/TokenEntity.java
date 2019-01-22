package cn.jinelei.rainbow.blog.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author zhenlei
 */
@Entity
@Table(name = "token")
@JacksonXmlRootElement(localName = "token")
public class TokenEntity {
    @Id
    @XmlElement
    @Column(unique = true, nullable = false, updatable = false, name = "token")
    private String token;
    @OneToOne
    @XmlElement
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private UserEntity userEntity;
    @Column(unique = true, length = 20)
    @XmlElement
    private Long effectiveDate;
    @Column(unique = true, length = 20)
    @XmlElement
    private Long expiryDate;

    public TokenEntity() {
    }

    public TokenEntity(UserEntity userEntity, String token, Long effectiveDate, Long expiryDate) {
        this.userEntity = userEntity;
        this.token = token;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
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

    public Long getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Long effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }
}
