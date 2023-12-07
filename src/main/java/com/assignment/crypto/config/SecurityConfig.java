package com.assignment.crypto.config;

import com.assignment.crypto.config.ratelimiter.IpRateLimiter;
import com.assignment.crypto.config.ratelimiter.RateLimitFilter;
import com.assignment.crypto.config.ratelimiter.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.Duration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${rate.limiter.duration}")
    private Duration rateLimiterDuration;

    @Value("${rate.limiter.requests}")
    private int rateLimiterRequests;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(rateLimitFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(cust->
                        cust.requestMatchers("**").permitAll()
                );

        return http.build();
    }

    @Bean
    public RateLimitFilter rateLimitFilter() {

        RateLimiter rateLimiter = new IpRateLimiter(this.rateLimiterRequests, this.rateLimiterDuration);
        return new RateLimitFilter("/api/**", rateLimiter);
    }
}
