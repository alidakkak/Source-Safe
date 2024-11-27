package com.network_applications.source_safe.Repository;

import com.network_applications.source_safe.Model.Entity.Group;
import com.network_applications.source_safe.Model.Entity.User;
import com.network_applications.source_safe.Model.Entity.UserGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    UserGroup findByUserAndGroupAndRoleType(User user, Group group, UserGroup.RoleType roleType);

    Optional<UserGroup> findByUserIdAndGroupIdAndRoleType(Long userId, Long groupId, UserGroup.RoleType roleType);
    UserGroup findByUserAndGroup(User user, Group group);
    List<UserGroup> findByGroupId(Long groupId);

    List<UserGroup> findByUserId(Long userId);

    boolean existsByUserIdAndGroupId(Long userId, Long groupId);

    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
            "FROM UserGroup ug " +
            "WHERE ug.user.id = :userId AND ug.group.id = " +
            "(SELECT f.group.id FROM File f WHERE f.id = :fileId)")
    boolean existsByUserIdAndFileGroupId(@Param("userId") Long userId, @Param("fileId") Long fileId);

}
