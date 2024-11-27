package com.network_applications.source_safe.Repository;

import com.network_applications.source_safe.Model.Entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
