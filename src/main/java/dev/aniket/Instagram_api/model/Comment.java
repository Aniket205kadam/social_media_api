package dev.aniket.Instagram_api.model;

import dev.aniket.Instagram_api.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    private String id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "user_id")),
            @AttributeOverride(name = "email", column = @Column(name = "user_email"))
    })
    private UserDto user;

    private String content;

    @Embedded
    @ElementCollection
    private Set<UserDto> likedByUsers = new HashSet<>();

    private LocalDateTime createdAt;
}
