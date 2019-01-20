package cn.jinelei.rainbow.blog.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

/**
 * @author zhenlei
 */
@Entity
@Table(name = "comment")
@JacksonXmlRootElement(localName = "comment")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement
    @Column(name = "comment_id")
    private Integer commentId;
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
    @JoinColumn(name = "commentator")
    private UserEntity commentator;
    @XmlElement
    @ManyToOne(targetEntity = ArticleEntity.class)
    @JoinColumn(name = "article")
    private ArticleEntity article;

    public boolean equalsWithoutId(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommentEntity that = (CommentEntity) o;
        return Objects.equals(createTime, that.createTime) &&
                Objects.equals(modifyTime, that.modifyTime) &&
                Objects.equals(accessTime, that.accessTime) &&
                Objects.equals(content, that.content) &&
                Objects.equals(commentator, that.commentator) &&
                Objects.equals(article, that.article);
    }

    public CommentEntity() {
    }

    public CommentEntity(Long createTime, Long modifyTime, Long accessTime, String content, UserEntity commentator, ArticleEntity article) {

        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.accessTime = accessTime;
        this.content = content;
        this.commentator = commentator;
        this.article = article;
    }

    public Integer getCommentId() {

        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
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

    public UserEntity getCommentator() {
        return commentator;
    }

    public void setCommentator(UserEntity commentator) {
        this.commentator = commentator;
    }

    public ArticleEntity getArticle() {
        return article;
    }

    public void setArticle(ArticleEntity article) {
        this.article = article;
    }
}
