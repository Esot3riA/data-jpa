package study.datajpa.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import study.datajpa.entity.Member;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    // 요렇게 생성자 만들어 두면 유용함
    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.teamName = null;
    }
}
