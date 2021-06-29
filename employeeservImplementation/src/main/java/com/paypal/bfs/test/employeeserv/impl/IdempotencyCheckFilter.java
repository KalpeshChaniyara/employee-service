package com.paypal.bfs.test.employeeserv.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Order(1)
public class IdempotencyCheckFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(IdempotencyCheckFilter.class.getName());

    private Map<String, LocalDateTime> idempotencyKeysMap = new ConcurrentHashMap<>();

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        executorService.scheduleAtFixedRate(this::run,0,5,TimeUnit.SECONDS);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        final String method = request.getMethod();
        if ("POST".equalsIgnoreCase(method)) {
            final String idempotencyKey = request.getHeader("idempotency-key");
            final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            if (!isValidIdempotencyKey(idempotencyKey)) {
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                httpServletResponse.flushBuffer();
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
                if (httpServletResponse.getStatus() == HttpStatus.CREATED.value()) {
                    idempotencyKeysMap.put(idempotencyKey, LocalDateTime.now());
                }
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private boolean isValidIdempotencyKey(String idempotencyKey) {
        return idempotencyKey != null && idempotencyKey.length() > 0
                && !idempotencyKeysMap.containsKey(idempotencyKey);
    }

    @Override
    public void destroy() {
        executorService.shutdown();
    }

    private void run() {
        logger.info("removing old ids");
        final Iterator<Map.Entry<String, LocalDateTime>> iterator = idempotencyKeysMap.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, LocalDateTime> entry = iterator.next();
            if (entry.getValue().plus(5, ChronoUnit.SECONDS)
                    .compareTo(LocalDateTime.now()) > 0) {
                iterator.remove();
            }
        }
    }
}
