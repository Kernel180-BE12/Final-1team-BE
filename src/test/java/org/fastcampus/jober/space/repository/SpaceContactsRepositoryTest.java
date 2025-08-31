package org.fastcampus.jober.space.repository;

import org.fastcampus.jober.space.entity.SpaceContacts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SpaceContactsRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private SpaceContactsRepository spaceContactsRepository;

  private SpaceContacts contact1;
  private SpaceContacts contact2;

  @BeforeEach
  void setUp() {
    // 테스트용 연락처 데이터 생성
    contact1 = SpaceContacts.builder()
        .name("김철수")
        .phoneNum("010-1234-5678")
        .email("kim@example.com")
        .spaceId(1L)
        .build();

    contact2 = SpaceContacts.builder()
        .name("이영희")
        .phoneNum("010-9876-5432")
        .email("lee@example.com")
        .spaceId(1L)
        .build();
  }

  @Test
  @DisplayName("스페이스 ID로 연락처 조회 테스트")
  void findBySpaceId_Success() {
    // given
    entityManager.persistAndFlush(contact1);
    entityManager.persistAndFlush(contact2);

    // when
    List<SpaceContacts> foundContacts = spaceContactsRepository.findBySpaceId(1L);

    // then
    assertThat(foundContacts).hasSize(2);
    assertThat(foundContacts).extracting("name")
        .containsExactlyInAnyOrder("김철수", "이영희");
  }

  @Test
  @DisplayName("스페이스 ID로 연락처 삭제 테스트")
  void deleteBySpaceId_Success() {
    // given
    entityManager.persistAndFlush(contact1);
    entityManager.persistAndFlush(contact2);

    // when
    spaceContactsRepository.deleteBySpaceId(1L);

    // then
    List<SpaceContacts> remainingContacts = spaceContactsRepository.findBySpaceId(1L);
    assertThat(remainingContacts).isEmpty();
  }

  @Test
  @DisplayName("연락처 저장 테스트")
  void save_Success() {
    // given
    SpaceContacts newContact = SpaceContacts.builder()
        .name("박민수")
        .phoneNum("010-5555-1234")
        .email("park@example.com")
        .spaceId(2L)
        .build();

    // when
    SpaceContacts savedContact = spaceContactsRepository.save(newContact);

    // then
    assertThat(savedContact.getId()).isNotNull();
    assertThat(savedContact.getName()).isEqualTo("박민수");
    assertThat(savedContact.getSpaceId()).isEqualTo(2L);
  }

  @Test
  @DisplayName("다른 스페이스의 연락처는 삭제되지 않음 테스트")
  void deleteBySpaceId_OtherSpaceContactsNotDeleted() {
    // given
    SpaceContacts contact3 = SpaceContacts.builder()
        .name("박민수")
        .phoneNum("010-5555-1234")
        .email("park@example.com")
        .spaceId(2L)
        .build();

    entityManager.persistAndFlush(contact1);
    entityManager.persistAndFlush(contact2);
    entityManager.persistAndFlush(contact3);

    // when
    spaceContactsRepository.deleteBySpaceId(1L);

    // then
    List<SpaceContacts> space1Contacts = spaceContactsRepository.findBySpaceId(1L);
    List<SpaceContacts> space2Contacts = spaceContactsRepository.findBySpaceId(2L);

    assertThat(space1Contacts).isEmpty();
    assertThat(space2Contacts).hasSize(1);
    assertThat(space2Contacts.get(0).getName()).isEqualTo("박민수");
  }
}
