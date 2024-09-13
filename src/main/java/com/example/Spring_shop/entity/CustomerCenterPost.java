package com.example.Spring_shop.entity;


import com.example.Spring_shop.constant.Notice;
import com.example.Spring_shop.dto.CustomerCenterPostFormDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name="customerCenterPost")
public class CustomerCenterPost {

    @Id
    @Column(name="customerCenterPost_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "member_customerCenterPost",
            joinColumns = @JoinColumn(name = "customerCenterPost_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Member member;

    @Enumerated(EnumType.STRING)
    private Notice notice;

    @Column(name = "views ")
    private int views;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @OrderBy("id asc") // 댓글 정렬
    @JsonManagedReference // 순환 참조 방지
    private List<Comment> comments;

    @Builder
    public CustomerCenterPost (String title, String content,String comment){
        this.title=title;
        this.content=content;
    }

    public CustomerCenterPost() {
        // 필드 초기화 또는 아무 작업도 하지 않아도 됩니다.
    }

    public void updateCustomerCenterPost(CustomerCenterPostFormDto customerCenterPostFormDto){
        this.title = customerCenterPostFormDto.getTitle();
        this.content = customerCenterPostFormDto.getContent();
    }

    public CustomerCenterPost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static CustomerCenterPost createCustomerCenterPost(CustomerCenterPostFormDto customerCenterPostFormDto){

        CustomerCenterPost customerCenterPost = new CustomerCenterPost(customerCenterPostFormDto.getTitle(),
                customerCenterPostFormDto.getContent());

        customerCenterPost.setTitle(customerCenterPostFormDto.getTitle());
        customerCenterPost.setContent(customerCenterPostFormDto.getContent());

        return customerCenterPost;
    }
    @Override
    public String toString() {
        return "CustomerCenterPost{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", commentsCount=" + (comments != null ? comments.size() : 0) +
                '}';
    }
}