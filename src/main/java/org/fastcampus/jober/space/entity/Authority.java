package org.fastcampus.jober.space.entity;

public enum Authority {
  ADMIN, // 최고 관리자
  DOCUMENT_MANAGER, // 문서 관리자
  MEMBER, // 구성원
  NO_PERMISSION // 접근 권한 없음 -> 문서 요청만 가능
}
