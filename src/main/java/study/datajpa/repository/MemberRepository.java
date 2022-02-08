package study.datajpa.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    // username = :username, age > :age 쿼리로 변환되어 나감
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // @Query 로 JPQL 쿼리를 바로 작성할 수 있음. 해당 Query 는 컴파일 타임 문법 체크 가능
    // @Param 으로 파라미터 지정 가능
    // 복잡한 정적 쿼리는 @Query 로 정의하고 동적 쿼리는 QueryDSL 을 사용할 것
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // @Query 를 DTO 로 조회
    // 이것도 new 가 귀찮기 때문에 추후 QueryDSL 로 간소화 가능
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 컬렉션 파라미터 바인딩 가능
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    // 반환 타입은 어떻게 지정해도 유연하게 지원
    List<Member> findListByUsername(String username);   // 다수 조회
    Member findMemberByUsername(String username);   // 단일 조회
    Optional<Member> findOptionalByUsername(String username);   // 단일 Optional 조회

    // 페이징 기능, Page 와 Slice 사용
    // Page 는 Slice 를 상속받음. Slice 는 totalCount 등의 계산이 빠지고 부분 데이터만 반환 (보통 무한스크롤 데이터에 사용)
    // totalCount 부분이 데이터가 많아질수록 성능이 나빠지기 쉬움
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    // 벌크 연산 쿼리, 데이터 변경 시 @Modifying 필요
    // 벌크 연산 후에는 영속성 컨텍스트를 반드시 비워 줘야 함 -> 기존 엔터티가 변경됐음을 영속성 컨텍스트가 감지하지 못하기 때문
    // clearAutomatically 옵션을 주면 벌크 연산 이후 영속성 컨텍스트를 비울 수 있음
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // Fetch join 예제. 직접 fetch JPQL 작성해도 되지만
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // @EntityGraph 를 사용해도 된다. attributePaths 에 left join 할 엔터티 명시하면 됨
    // 부모 클래스의 findAll 오버라이드 예시
    @Override
    @EntityGraph(attributePaths = { "team" })
    List<Member> findAll();

    // @Query 에 fetch 키워드를 사용하지 않아도 @EntityGraph 만 명시해서 fetch join 사용이 가능함
    // 보통은 fetch 키워드를 작성하되 아주 간단한 형태의 fetch join 은 @EntityGraph 만 명시해서 간략하게 사용하면 좋음
    @EntityGraph(attributePaths = { "team" })
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = { "team" })
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // 읽기 전용 엔티티임을 @QueryHints 로 명시
    // 원래 Dirty checking 이 동작하려면 원본 엔티티와 가져온 엔티티가 각각 메모리에 존재해야 함
    // 조회용 엔티티는 읽기 전용으로 명시해 두면 엔티티를 두 개 유지하지 않아도 됨
    // 엔티티를 수정해도 Dirty checking 동작하지 않음 (그냥 알아 두기만 할 것)
    @QueryHints(value =
        @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    // 가져온 엔티티가 타 쿼리에서 수정되는 것을 막음 (Lock)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockByUsername(String username);
}
