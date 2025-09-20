package io.vortex.commerce.orderservice.domain.port.out;

import io.vortex.commerce.orderservice.domain.model.User;
import java.util.Optional;

public interface UserPort {
    Optional<User> findByUsername(String username);
}
