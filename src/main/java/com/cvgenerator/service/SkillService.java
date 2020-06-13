package com.cvgenerator.service;

import com.cvgenerator.domain.entity.Skill;

public interface SkillService {

    void saveSkill(Long userCvId, Skill skill);

    void updateSkill(Skill skill);

    void deleteSkillById(Long id);
}
