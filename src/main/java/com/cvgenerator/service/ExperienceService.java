package com.cvgenerator.service;

import com.cvgenerator.domain.entity.Experience;

public interface ExperienceService {

    void createExperience(Long userCvId, Experience experience);
}
