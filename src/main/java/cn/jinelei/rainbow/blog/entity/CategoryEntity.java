package cn.jinelei.rainbow.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.Objects;

/**
 * @author zhenlei
 */
@Entity
@Table(name = "category")
@JacksonXmlRootElement(localName = "category")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class CategoryEntity {
    public interface BaseCategoryView extends UserEntity.WithoutPasswordView {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", unique = true, nullable = false)
    @XmlElement
    @JsonView(BaseCategoryView.class)
    private Integer categoryId;
    @XmlElement
    @Column(name = "create_time", nullable = false)
    @JsonView(BaseCategoryView.class)
    private Long createTime;
    @XmlElement
    @Column(name = "modify_time", nullable = true)
    @JsonView(BaseCategoryView.class)
    private Long modifyTime;
    @XmlElement
    @Column(name = "access_time", nullable = true)
    @JsonView(BaseCategoryView.class)
    private Long accessTime;
    @XmlElement
    @Column(name = "name", unique = true, nullable = false, length = 20)
    @JsonView(BaseCategoryView.class)
    private String name;
    @XmlElement
    @Column(name = "summarty", nullable = true, length = 255)
    @JsonView(BaseCategoryView.class)
    private String summary;
    @XmlElement
    @JsonIgnore
    @OneToMany(targetEntity = ArticleEntity.class, mappedBy = "category")
    @JsonView(BaseCategoryView.class)
    private List<ArticleEntity> articles;
    @XmlElement
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "categoryCreator")
    @JsonView(BaseCategoryView.class)
    private UserEntity categoryCreator;

    public boolean equalsWithoutId(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CategoryEntity that = (CategoryEntity) o;
        return Objects.equals(createTime, that.createTime) &&
                Objects.equals(modifyTime, that.modifyTime) &&
                Objects.equals(accessTime, that.accessTime) &&
                Objects.equals(name, that.name) &&
                Objects.equals(summary, that.summary) &&
                Objects.equals(articles, that.articles) &&
                Objects.equals(categoryCreator, that.categoryCreator);
    }

    public CategoryEntity() {
    }

    public CategoryEntity(Long createTime, Long modifyTime, Long accessTime, String name, String summary, List<ArticleEntity> articles, UserEntity categoryCreator) {
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.accessTime = accessTime;
        this.name = name;
        this.summary = summary;
        this.articles = articles;
        this.categoryCreator = categoryCreator;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Long accessTime) {
        this.accessTime = accessTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<ArticleEntity> getArticleList() {
        return articles;
    }

    public void setArticleList(List<ArticleEntity> articles) {
        this.articles = articles;
    }

    public UserEntity getCategoryCreator() {
        return categoryCreator;
    }

    public void setCategoryCreator(UserEntity categoryCreator) {
        this.categoryCreator = categoryCreator;
    }
}
