package dev.aniket.Instagram_api.model;

import dev.aniket.Instagram_api.dto.UserDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "stories")
public class Story {
    @Id
    private String id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "user_id")),
            @AttributeOverride(name = "email", column = @Column(name = "user_email"))
    })
    private UserDto user;

    @NotNull
    private String path;
    private String caption;
    private LocalDateTime timestamp;

    @Embedded
    @ElementCollection
    @JoinTable(name = "story_viewers", joinColumns = @JoinColumn(name = "user_id"))
    private Set<UserDto> storyViewers = new HashSet<>();
}
