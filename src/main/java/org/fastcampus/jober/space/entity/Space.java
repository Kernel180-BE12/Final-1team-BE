package org.fastcampus.jober.space.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String adminName;
    private String adminNum;
    // 이 아래로 꼭 필요할지?
    private String faxNum;
    private String email;
    private String url;
    private String businessType;
    private String corporateRegistrationNo;
    private String businessRegistrationNo;
    private String signatureImgUrl;
    private LocalDate businessOpenDate;
    private String businessCategory;
    private String businessItem;
    private String taxEmail;

}
