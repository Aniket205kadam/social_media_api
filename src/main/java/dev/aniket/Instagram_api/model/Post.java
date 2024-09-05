package dev.aniket.Instagram_api.model;

import dev.aniket.Instagram_api.dto.UserDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "posts")
public class Post {
    @Id
    private String id;

    private String caption;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_content", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "post_urls")
    private List<String> postContent;
    private String location;
    private LocalDateTime createdAt;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "user_id")),
            @AttributeOverride(name = "email", column = @Column(name = "user_email"))
    })
    private UserDto user;

    @OneToMany
    private List<Comment> comments = new ArrayList<>();

    @Embedded
    @ElementCollection
    @JoinTable(name = "liked_by_users", joinColumns = @JoinColumn(name = "user_id"))
    private Set<UserDto> likedByUsers = new HashSet<>();

    @Embedded
    @ElementCollection
    @JoinTable(name = "post_viewers", joinColumns = @JoinColumn(name = "user_id"))
    private Set<UserDto> postViewers = new HashSet<>();
}
