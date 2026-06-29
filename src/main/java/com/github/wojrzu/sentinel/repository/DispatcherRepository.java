package com.github.wojrzu.sentinel.repository;

import com.github.wojrzu.sentinel.model.Dispatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DispatcherRepository extends JpaRepository<Dispatcher, UUID> {
}
