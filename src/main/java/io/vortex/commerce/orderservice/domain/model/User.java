package io.vortex.commerce.orderservice.domain.model;

import java.util.Set;

public record User(Long id, String username, String password, Set<Role> roles) {}
