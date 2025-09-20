package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper;

import io.vortex.commerce.orderservice.domain.model.Role;
import io.vortex.commerce.orderservice.domain.model.User;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.RoleEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesEntityToRoles")
    User toDomain(UserEntity userEntity);

    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToRolesEntity")
    UserEntity toEntity(User user);

    @Named("rolesEntityToRoles")
    default Set<Role> rolesEntityToRoles(Set<RoleEntity> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
    }

    @Named("rolesToRolesEntity")
    default Set<RoleEntity> rolesToRolesEntity(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(roleName -> new RoleEntity(null, roleName))
                .collect(Collectors.toSet());
    }
}