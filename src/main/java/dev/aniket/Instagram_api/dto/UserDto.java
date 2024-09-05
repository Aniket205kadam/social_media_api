package dev.aniket.Instagram_api.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class UserDto {
    private String id;
    private String username;
    private String name;
    private String email;
    private String profileImage;
}
