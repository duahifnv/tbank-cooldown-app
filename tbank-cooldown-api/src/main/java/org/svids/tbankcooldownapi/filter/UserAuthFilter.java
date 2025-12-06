package org.svids.tbankcooldownapi.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.svids.tbankcooldownapi.dto.AuthDto;
import org.svids.tbankcooldownapi.repository.UserRepo;

import java.io.IOException;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserAuthFilter extends OncePerRequestFilter {

    private final UserRepo userRepo;
    private final Environment environment;

    private static final List<String> EXCLUDED_PATHS = List.of(
        "/api/user/authenticate",
        "/actuator/health",
        "/v3/api-docs",
        "/swagger-ui",
        "/error"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        boolean isNoAuth = Arrays.stream(environment.getActiveProfiles()).anyMatch(x -> x.equalsIgnoreCase("noauth"));

        // Проверяем, нужно ли проверять аутентификацию для этого пути
        String requestPath = request.getRequestURI();
        if (isNoAuth || isExcludedPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Получаем X-USER-ID из заголовка
        String userIdHeader = request.getHeader(AuthDto.AUTH_HEADER);
        
        if (userIdHeader == null || userIdHeader.isBlank()) {
            log.warn("Missing X-USER-ID header for path: {}", requestPath);
            sendErrorResponse(response, "Missing X-USER-ID header");
            return;
        }
        
        try {
            var userId = UUID.fromString(userIdHeader);
            
            // Проверяем существование пользователя
            boolean userExists = userRepo.existsById(userId);
            
            if (!userExists) {
                log.warn("User not found with ID: {} for path: {}", userId, requestPath);
                sendErrorResponse(response, "User not found");
                return;
            }
            
            // Продолжаем цепочку фильтров
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format in X-USER-ID header: {}", userIdHeader);
            sendErrorResponse(response, "Invalid UUID format");
        } catch (Exception e) {
            log.error("Error during user validation", e);
            sendErrorResponse(response, "Internal server error");
        }
    }
    
    private boolean isExcludedPath(String requestPath) {
        return EXCLUDED_PATHS.stream().anyMatch(requestPath::startsWith);
    }
    
    private void sendErrorResponse(HttpServletResponse response, String message) 
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        errorResponse.put("message", message);

        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
