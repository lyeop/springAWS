package com.example.Spring_shop.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

import static java.awt.SystemColor.text;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "comment")
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;
    private String maskedName;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerCenterPost_id")
    @JsonBackReference
    private CustomerCenterPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    private boolean isAdmin;


    @Builder
    public Comment(CustomerCenterPost post, Member member, String comment, LocalDateTime createdDate, LocalDateTime modifiedDate,String maskedName ,boolean isAdmin ) {
        this.post = post;
        this.member = member;
        this.comment = comment;
        this.createdDate = createdDate != null ? createdDate : LocalDateTime.now();
        this.modifiedDate = modifiedDate != null ? modifiedDate : LocalDateTime.now();
        this.maskedName=maskedName;
        this.isAdmin=isAdmin;
    }

    // 댓글 수정
    public void update(String comment) {
        this.comment = comment;
        this.modifiedDate = LocalDateTime.now(); // 수정일 갱신
    }
    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", memberId=" + (member != null ? member.getId() : "none") +
                ", postId=" + (post != null ? post.getId() : "none") +
                '}';
    }
}
