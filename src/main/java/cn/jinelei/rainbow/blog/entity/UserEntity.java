package cn.jinelei.rainbow.blog.entity;

import cn.jinelei.rainbow.blog.entity.enumerate.GroupPrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.UserPrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.convert.GroupPrivilegeConvert;
import cn.jinelei.rainbow.blog.entity.enumerate.convert.UserPrivilegeConvert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.Objects;

/**
 * @author zhenlei
 */
@Entity
@Table(name = "user")
@JacksonXmlRootElement(localName = "user")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler", "fieldHandler"})
public class UserEntity {
    public interface WithoutPasswordView {};
    public interface WithPasswordView extends WithoutPasswordView {};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement
    @JsonView(WithoutPasswordView.class)
    private Integer userId;
    @Column(unique = true, length = 20)
    @XmlElement
    @JsonView(WithoutPasswordView.class)
    private String username;
    @Column(nullable = true, length = 20)
    @XmlElement
    @JsonView(WithoutPasswordView.class)
    private String nickname;
    @Column(nullable = false, length = 255)
    @XmlElement
    @JsonView(WithPasswordView.class)
    private String password;
    @Column(nullable = false, unique = true, length = 20)
    @XmlElement
    @JsonView(WithoutPasswordView.class)
    private String phone;
    @Column(nullable = true, unique = true, length = 30)
    @XmlElement
    @JsonView(WithoutPasswordView.class)
    private String email;
    @Column(nullable = true, length = 10)
    @XmlElement
    @JsonView(WithoutPasswordView.class)
    private String province;
    @Column(nullable = true, length = 20)
    @XmlElement
    @JsonView(WithoutPasswordView.class)
    private String city;
    @Column(nullable = false)
    @XmlElement
    @Convert(converter = UserPrivilegeConvert.class)
    @JsonView(WithoutPasswordView.class)
    private UserPrivilege userPrivilege = UserPrivilege.TOURIST_USER;
    @Column(nullable = false)
    @XmlElement
    @Convert(converter = GroupPrivilegeConvert.class)
    @JsonView(WithoutPasswordView.class)
    private GroupPrivilege groupPrivilege = GroupPrivilege.TOURIST_GROUP;
    @Column
    @XmlElement
    @JsonIgnore
    @OneToMany(targetEntity = ArticleEntity.class, cascade = CascadeType.REFRESH, mappedBy = "author", fetch = FetchType.LAZY)
    private List<ArticleEntity> articles;
    @Column
    @XmlElement
    @JsonIgnore
    @OneToMany(targetEntity = CategoryEntity.class, cascade = CascadeType.REFRESH, mappedBy = "categoryCreator", fetch = FetchType.LAZY)
    @JsonView(WithoutPasswordView.class)
    private List<CategoryEntity> categories;
    @Column
    @XmlElement
    @JsonIgnore
    @OneToMany(targetEntity = TagEntity.class, cascade = CascadeType.REFRESH, mappedBy = "tagCreator", fetch = FetchType.LAZY)
    @JsonView(WithoutPasswordView.class)
    private List<TagEntity> tags;
    @Column
    @XmlElement
    @JsonIgnore
    @OneToMany(targetEntity = CommentEntity.class, cascade = CascadeType.REFRESH, mappedBy = "commentator", fetch = FetchType.LAZY)
    @JsonView(WithoutPasswordView.class)
    private List<CommentEntity> comments;

    public boolean equalsWithId(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserEntity that = (UserEntity) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(nickname, that.nickname) &&
                Objects.equals(password, that.password) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(email, that.email) &&
                Objects.equals(province, that.province) &&
                Objects.equals(city, that.city) &&
                userPrivilege == that.userPrivilege &&
                groupPrivilege == that.groupPrivilege &&
                Objects.equals(articles, that.articles) &&
                Objects.equals(categories, that.categories) &&
                Objects.equals(tags, that.tags) &&
                Objects.equals(comments, that.comments);
    }

    public UserEntity() {
    }

    public UserEntity(String username, String nickname, String password, String phone, String email, String province,
                      String city, UserPrivilege userPrivilege, GroupPrivilege groupPrivilege,
                      List<ArticleEntity> articles, List<CategoryEntity> categories, List<TagEntity> tags,
                      List<CommentEntity> comments) {

        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.province = province;
        this.city = city;
        this.userPrivilege = userPrivilege;
        this.groupPrivilege = groupPrivilege;
        this.articles = articles;
        this.categories = categories;
        this.tags = tags;
        this.comments = comments;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public UserPrivilege getUserPrivilege() {
        return userPrivilege;
    }

    public void setUserPrivilege(UserPrivilege userPrivilege) {
        this.userPrivilege = userPrivilege;
    }

    public GroupPrivilege getGroupPrivilege() {
        return groupPrivilege;
    }

    public void setGroupPrivilege(GroupPrivilege groupPrivilege) {
        this.groupPrivilege = groupPrivilege;
    }

    public List<ArticleEntity> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleEntity> articles) {
        this.articles = articles;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }
}
