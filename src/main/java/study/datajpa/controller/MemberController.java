package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 도메인 클래스 컨버터 사용 (알아두기만 할 것)
    // 이렇게 받아 온 엔티티는 읽기 전용
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    // 페이징 기능
    // URL 뒤에 파라미터 명시하면 (ex: ?page=1&size=3&sort=id,desc) 커스텀 페이징 가능
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page.map(MemberDto::new);
    }

    @PostConstruct
    public void init() {
        for (int i = 1; i <= 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
