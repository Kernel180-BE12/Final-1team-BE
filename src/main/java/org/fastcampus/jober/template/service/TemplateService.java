package org.fastcampus.jober.template.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.template.entity.Template;
import org.fastcampus.jober.template.repository.TemplateRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TemplateService {
    private final TemplateRepository templateRepository;

    @Transactional
    public Boolean saveTemplate(Long id, Long spaceId, Boolean isSaved) {
        Template template = templateRepository.findByIdAndSpaceIdAndIsSaved(id, spaceId, isSaved)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "템플릿을 찾을 수 없습니다."));

        return template.updateIsSaved(isSaved);
    }
}
