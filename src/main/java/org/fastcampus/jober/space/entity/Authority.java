package org.fastcampus.jober.space.entity;

public enum Authority {
    ADMIN(

    ) {
        @Override
        boolean isSuperAdmin() {
            return true;
        }
    },                  // 최고 관리자
    DOCUMENT_MANAGER() {
        @Override
        boolean isSuperAdmin() {
            return true;
        }
    },       // 문서 관리자
    MEMBER() {
        @Override
        boolean isSuperAdmin() {
            return false;
        }
    },                 // 구성원
    NO_PERMISSION() {
        @Override
        boolean isSuperAdmin() {
            return false;
        }

        @Override
        boolean hasUserRole() {
            return false;
        }
    };           // 접근 권한 없음 -> 문서 요청만 가능

    abstract boolean isSuperAdmin();

    boolean hasUserRole() {
        return this == ADMIN || this == DOCUMENT_MANAGER || this == MEMBER;
    }
}
