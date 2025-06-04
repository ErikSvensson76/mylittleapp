package org.example.mylittleapp.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "base")
@Validated
public record BaseUrl(
    @Valid @NotNull String url
) {}
