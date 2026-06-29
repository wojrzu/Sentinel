package com.github.wojrzu.sentinel.repository;

import com.github.wojrzu.sentinel.model.Officer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OfficerRepository extends JpaRepository<Officer, UUID> {
    List<Officer> findByStatus(int status);
}
