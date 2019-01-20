package cn.jinelei.rainbow.blog.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.Objects;

/**
 * @author zhenlei
 */
@Entity
@Table(name = "article")
@JacksonXmlRootElement(localName = "article")
public class ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement
    @Column(name = "article_id")
    private Integer articleId;
    @XmlElement
    @Column(name = "create_time")
    private Long createTime;
    @XmlElement
    @Column(name = "modify_time")
    private Long modifyTime;
    @XmlElement
    @Column(name = "access_time")
    private Long accessTime;
    @XmlElement
    @Column(name = "content")
    private String content;
    @XmlElement
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "author")
    private UserEntity author;
    @XmlElement
    @ManyToOne(targetEntity = CategoryEntity.class)
    @JoinColumn(name = "category")
    private CategoryEntity category;
    @XmlElement
    @ManyToMany
    @JoinTable(name = "article_tag",
            joinColumns = {@JoinColumn(name = "article_id", referencedColumnName = "article_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "tag_id")})
    private List<TagEntity> tags;
    @Column
    @XmlElement
    @OneToMany(targetEntity = CommentEntity.class, cascade = CascadeType.REFRESH, mappedBy = "article")
    private List<CommentEntity> comments;

    public boolean equalsWithoutId(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArticleEntity that = (ArticleEntity) o;
        return Objects.equals(createTime, that.createTime) &&
                Objects.equals(modifyTime, that.modifyTime) &&
                Objects.equals(accessTime, that.accessTime) &&
                Objects.equals(content, that.content) &&
                Objects.equals(author, that.author) &&
                Objects.equals(category, that.category) &&
                Objects.equals(tags, that.tags) &&
                Objects.equals(comments, that.comments);
    }

    @Override
    public String toString() {
        return "ArticleModel{" +
                "articleId=" + articleId +
                ", createTime=" + createTime + ", modifyTime=" + modifyTime +
                ", accessTime=" + accessTime +
                ", content='" + content + '\'' +
                ", author=" + author +
                ", category=" + category +
                ", tags=" + tags +
                ", comments=" + comments +
                '}';
    }

    public ArticleEntity() {
    }

    public ArticleEntity(Long createTime, Long modifyTime, Long accessTime, String content, UserEntity author, CategoryEntity category, List<TagEntity> tags, List<CommentEntity> comments) {

        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.accessTime = accessTime;
        this.content = content;
        this.author = author;
        this.category = category;
        this.tags = tags;
        this.comments = comments;
    }

    public Integer getArticleId() {

        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public List<TagEntity> getTagList() {
        return tags;
    }

    public void setTagList(List<TagEntity> tags) {
        this.tags = tags;
    }

    public List<CommentEntity> getCommentList() {
        return comments;
    }

    public void setCommentList(List<CommentEntity> comments) {
        this.comments = comments;
    }
}

