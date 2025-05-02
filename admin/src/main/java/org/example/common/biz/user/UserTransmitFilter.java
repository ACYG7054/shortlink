package org.example.common.biz.user;

 import com.alibaba.fastjson2.JSON;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 用户信息传输过滤器
 */
@RequiredArgsConstructor
@Slf4j
public class UserTransmitFilter implements Filter {
    private final StringRedisTemplate stringRedisTemplate;
    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String username = httpServletRequest.getHeader("username");
        String token = httpServletRequest.getHeader("token");
        log.info("Headers -> username: {}, token: {}", username, token);
        Object userInfoJsonStr= stringRedisTemplate.opsForHash().get("login_"+username,token);
        if(userInfoJsonStr!=null){
            UserInfoDTO userInfoDTO= JSON.parseObject(userInfoJsonStr.toString(),UserInfoDTO.class);
            UserContext.setUser(userInfoDTO);
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
