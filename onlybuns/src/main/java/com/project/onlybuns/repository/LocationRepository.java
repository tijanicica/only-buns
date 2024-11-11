package com.project.onlybuns.repository;

import com.project.onlybuns.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
