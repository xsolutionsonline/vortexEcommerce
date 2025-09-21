package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository;

import io.vortex.commerce.orderservice.domain.model.User;
import io.vortex.commerce.orderservice.domain.port.out.UserPort;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper.UserPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPort {

    private final JpaUserRepository jpaUserRepository;
    private final UserPersistenceMapper userMapper;

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }
}