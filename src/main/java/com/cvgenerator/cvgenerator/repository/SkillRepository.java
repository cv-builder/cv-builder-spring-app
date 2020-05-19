package com.cvgenerator.cvgenerator.repository;

import com.cvgenerator.cvgenerator.domain.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

}
