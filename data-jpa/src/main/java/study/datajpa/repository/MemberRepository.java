package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    //List<Member> findByUsername(String username); //구현하지 않아도 동작함 --> 쿼리 메소드 기능
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    List<Member> findTop3HelloBy(); //전체 조회
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username")String username);
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username")String username, @Param("age")int age);
    @Query("select m.username from Member m")
    List<String> findUsernameList();
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto(); //new operation으로 생성자와 매핑
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names")List<String> names);
    List<Member> findListByUsername(String username); //전체 조회
    Member findMemberByUsername(String username); //단건 조회
    Optional<Member> findOptionalByUsername(String username); //Optional 조회
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable); //Pageable은 page에 대한 정보
    //Slice<Member> findByAge(int age, Pageable pageable); //Pageable은 page에 대한 정보
    //List<Member> findByAge(int age, Pageable pageable); //Pageable은 page에 대한 정보
    @Modifying(clearAutomatically = true) //JPA executeUpdate() 호출
    @Query("update Member m set m.age = m.age +1 where m.age >= :age")
    int bulkAgePlus(@Param("age")int age);
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username")String username);
    @EntityGraph("Member.all")
    List<Member> findNamedEntityGraphByUsername(@Param("username") String username);
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
