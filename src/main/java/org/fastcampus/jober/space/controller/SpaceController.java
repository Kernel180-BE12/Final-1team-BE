package org.fastcampus.jober.space.controller;

import org.apache.coyote.Response;
import org.fastcampus.jober.space.dto.request.SpaceMemberRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceMemberResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
import org.fastcampus.jober.space.repository.SpaceMemberRepository;
import org.fastcampus.jober.space.repository.SpaceRepository;
import org.fastcampus.jober.space.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 각 예외처리 추가 예정
@Controller
@RequestMapping("/space")
public class SpaceController {
    private SpaceService spaceService;

    public SpaceController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @PostMapping("/add-space")
    public ResponseEntity<SpaceResponseDto> addSpace(@RequestBody SpaceRequestDto dto) {
        SpaceResponseDto result = spaceService.createSpace(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/update-space")
    public ResponseEntity<SpaceResponseDto> updateSpace(Long id, @RequestBody SpaceRequestDto dto) {
        SpaceResponseDto result = spaceService.updateSpace(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping
    public ResponseEntity<SpaceMemberResponseDto> addSpaceMember(
            Long spaceId, @RequestBody SpaceMemberRequestDto dto) {
        spaceService.addSpaceMember(spaceId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping
    public ResponseEntity<SpaceMemberResponseDto> deleteSpaceMember(
            Long spaceId, @RequestBody SpaceMemberRequestDto dto) {

    }

    @GetMapping
    public ResponseEntity<List<SpaceResponseDto>> findAllSpace(Long memberId) {

    }

}
