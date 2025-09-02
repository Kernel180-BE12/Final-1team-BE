package org.fastcampus.jober.space.controller;

import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.space.dto.request.SpaceMemberAddRequestDto;
import org.fastcampus.jober.space.entity.SpaceMember;
import org.fastcampus.jober.space.service.SpaceMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SpaceMemberController {
    private final SpaceMemberService spaceMemberService;

    @PostMapping
    public ResponseEntity<Void> addSpaceMember(SpaceMemberAddRequestDto memberAddRequestDto) {
        spaceMemberService.addSpaceMember(memberAddRequestDto);
        return ResponseEntity.ok().build();

    }
}
