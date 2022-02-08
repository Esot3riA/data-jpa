package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    // 복잡한 쿼리들은 CustomRepository 로 따로 구현해서 쓸 수 있음
    // 주로 QueryDSL 사용 시에 유용함
    // Custom Interface 이름은 무엇으로 해도 무방하나 Custom Class 이름은 XXXRepositoryImpl 로 지어야 함

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
