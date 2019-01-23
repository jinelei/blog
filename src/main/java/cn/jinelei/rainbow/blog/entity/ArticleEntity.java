package cn.jinelei.rainbow.blog.entity;

import cn.jinelei.rainbow.blog.entity.enumerate.BrowsePrivilege;
import cn.jinelei.rainbow.blog.entity.enumerate.CommentPrivilege;
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
@Table(name = "article")
@JacksonXmlRootElement(localName = "article")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class ArticleEntity {
    public interface BaseArticleView extends
            UserEntity.WithoutPasswordView,
            CategoryEntity.BaseCategoryView,
            CommentEntity.BaseCommentView,
            TagEntity.BaseTagView {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement
    @Column(name = "article_id", unique = true, nullable = false)
    @JsonView(value = BaseArticleView.class)
    private Integer articleId;
    @XmlElement
    @Column(name = "create_time", nullable = false)
    @JsonView(value = BaseArticleView.class)
    private Long createTime;
    @XmlElement
    @Column(name = "modify_time", nullable = true)
    @JsonView(value = BaseArticleView.class)
    private Long modifyTime;
    @XmlElement
    @Column(name = "access_time", nullable = true)
    @JsonView(value = BaseArticleView.class)
    private Long accessTime;
    @XmlElement
    @Column(name = "title", nullable = false, length = 127)
    @JsonView(value = BaseArticleView.class)
    private String title;
    @XmlElement
    @Convert(converter = CommentPrivilege.class)
    @Column(name = "comment_privilege")
    @JsonView(value = BaseArticleView.class)
    private CommentPrivilege commentPrivilege = CommentPrivilege.ALLOW_MYSELF;
    @XmlElement
    @Convert(converter = BrowsePrivilege.class)
    @Column(name = "browse_privilege")
    @JsonView(value = BaseArticleView.class)
    private BrowsePrivilege browsePrivilege = BrowsePrivilege.ALLOW_MYSELF;
    @XmlElement
    @Column(name = "content", nullable = false, length = 4095)
    @JsonView(value = BaseArticleView.class)
    private String content;
    @XmlElement
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "author", nullable = false)
    @JsonView(value = BaseArticleView.class)
    private UserEntity author;
    @XmlElement
    @ManyToOne(targetEntity = CategoryEntity.class)
    @JoinColumn(name = "category")
    @JsonView(value = BaseArticleView.class)
    private CategoryEntity category;
    @XmlElement
    @ManyToMany
    @JoinTable(name = "article_tag",
            joinColumns = {@JoinColumn(name = "article_id", referencedColumnName = "article_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "tag_id")})
    @JsonView(value = BaseArticleView.class)
    private List<TagEntity> tags;
    @Column
    @XmlElement
    @OneToMany(targetEntity = CommentEntity.class, cascade = CascadeType.REFRESH, mappedBy = "article")
    @JsonView(value = BaseArticleView.class)
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
                Objects.equals(title, that.title) &&
                Objects.equals(content, that.content) &&
                Objects.equals(author, that.author) &&
                Objects.equals(category, that.category) &&
                Objects.equals(tags, that.tags) &&
                Objects.equals(commentPrivilege, that.commentPrivilege) &&
                Objects.equals(browsePrivilege, that.browsePrivilege) &&
                Objects.equals(comments, that.comments);
    }

    public ArticleEntity(Long createTime, Long modifyTime, Long accessTime, String title, CommentPrivilege commentPrivilege, BrowsePrivilege browsePrivilege, String content, UserEntity author, CategoryEntity category, List<TagEntity> tags, List<CommentEntity> comments) {
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.accessTime = accessTime;
        this.title = title;
        this.commentPrivilege = commentPrivilege;
        this.browsePrivilege = browsePrivilege;
        this.content = content;
        this.author = author;
        this.category = category;
        this.tags = tags;
        this.comments = comments;
    }

    public ArticleEntity() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public CommentPrivilege getCommentPrivilege() {
        return commentPrivilege;
    }

    public void setCommentPrivilege(CommentPrivilege commentPrivilege) {
        this.commentPrivilege = commentPrivilege;
    }

    public BrowsePrivilege getBrowsePrivilege() {
        return browsePrivilege;
    }

    public void setBrowsePrivilege(BrowsePrivilege browsePrivilege) {
        this.browsePrivilege = browsePrivilege;
    }
}
