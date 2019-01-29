package cn.jinelei.rainbow.blog.entity;

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
@Table(name = "tag")
@JacksonXmlRootElement(localName = "tag")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class TagEntity {
    public interface BaseTagView extends UserEntity.WithoutPasswordView{
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false, unique = false)
    @XmlElement
    @JsonView(value = BaseTagView.class)
    private Integer tagId;
    @XmlElement
    @Column(name = "create_time", nullable = false)
    @JsonView(value = BaseTagView.class)
    private Long createTime;
    @XmlElement
    @Column(name = "modify_time", nullable = true)
    @JsonView(value = BaseTagView.class)
    private Long modifyTime;
    @XmlElement
    @Column(name = "access_time", nullable = true)
    @JsonView(value = BaseTagView.class)
    private Long accessTime;
    @XmlElement
    @Column(name = "name", nullable = false, length = 20)
    @JsonView(value = BaseTagView.class)
    private String name;
    @XmlElement
    @Column(name = "summary", nullable = true, length = 255)
    @JsonView(value = BaseTagView.class)
    private String summary;
    @XmlElement
    @JsonIgnore
    @ManyToMany(targetEntity = ArticleEntity.class, mappedBy = "tags", fetch = FetchType.LAZY)
    @JsonView(value = BaseTagView.class)
    private List<ArticleEntity> articles;
    @XmlElement
    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "tagCreator")
    @JsonView(value = BaseTagView.class)
    private UserEntity tagCreator;

    public boolean equalsWithoutId(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TagEntity tagEntity = (TagEntity) o;
        return Objects.equals(createTime, tagEntity.createTime) &&
                Objects.equals(modifyTime, tagEntity.modifyTime) &&
                Objects.equals(accessTime, tagEntity.accessTime) &&
                Objects.equals(name, tagEntity.name) &&
                Objects.equals(summary, tagEntity.summary) &&
                Objects.equals(articles, tagEntity.articles) &&
                Objects.equals(tagCreator, tagEntity.tagCreator);
    }

    public TagEntity() {
    }

    public TagEntity(Long createTime, Long modifyTime, Long accessTime, String name, String summary, List<ArticleEntity> articles, UserEntity tagCreator) {
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.accessTime = accessTime;
        this.name = name;
        this.summary = summary;
        this.articles = articles;
        this.tagCreator = tagCreator;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
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

    public UserEntity getTagCreator() {
        return tagCreator;
    }

    public void setTagCreator(UserEntity tagCreator) {
        this.tagCreator = tagCreator;
    }
}
