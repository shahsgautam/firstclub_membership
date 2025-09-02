package com.firstclub.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firstclub.membership.model.entity.TierCriteria;

public interface TierCriteriaRepository extends JpaRepository<TierCriteria, Long> {}
