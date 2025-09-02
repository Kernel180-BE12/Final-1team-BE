package org.fastcampus.jober.common;

public enum EndpointGroup {
    PUBLIC(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/h2-console/**",
            "/swagger-ui.html",
            "/admin/sessions/**"
    ),
    USER(
            "/user/register",
            "/user/login"
    );

    private final String[] patterns;

    EndpointGroup(String... patterns) {
        this.patterns = patterns;
    }

    public String[] getPatterns() {
        return patterns;
    }
}

