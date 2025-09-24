package org.fastcampus.jober.space.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class InviteResult {
    private final List<String> successEmails;
    private final List<String> duplicateEmails;

}
