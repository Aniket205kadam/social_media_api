package dev.aniket.Instagram_api.model;

import dev.aniket.Instagram_api.dto.UserDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

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
@Table(name = "users")
public class User {
    @Id
    private String id;

    @NotEmpty(message = "username is mandatory")
    @Size(min = 3, max = 20, message = "username between 3 to 20")
    private String username;

    @NotEmpty(message = "name is mandatory")
    private String name;

    @NotEmpty(message = "email is mandatory")
    private String email;

    @NotEmpty(message = "mobile is mandatory")
    private String mobile;
    private String website;
    private String bio;
    private String gender;
    private String profileImage;

    @NotEmpty(message = "password is mandatory")
    @Size(min = 8, message = "password contain more the eight characters")
    private String password;

    @Embedded
    @ElementCollection
    private Set<UserDto> follower = new HashSet<>();

    @Embedded
    @ElementCollection
    private Set<UserDto> following = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Story> stories = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Post> savedPost = new ArrayList<>();
}
