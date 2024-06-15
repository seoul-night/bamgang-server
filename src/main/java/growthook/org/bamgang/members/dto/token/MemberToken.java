package growthook.org.bamgang.members.dto.token;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class MemberToken {
    private String nickName;
    private String profile;
    private String id;

    @Builder
    public MemberToken(String id, String nickName, String profile){
        this.id = id;
        this.nickName = nickName;
        this.profile = profile;
    }
}
