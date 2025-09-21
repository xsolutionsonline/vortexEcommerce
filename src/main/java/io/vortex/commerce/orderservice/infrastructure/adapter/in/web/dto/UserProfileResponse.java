package io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto;

import java.util.Collection;

public record UserProfileResponse(String username, Collection<String> roles) {}