package org.example.common.biz.user;

 import cn.hutool.core.util.StrUtil;
 import com.alibaba.fastjson2.JSON;
 import com.google.common.collect.Lists;
 import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
 import jakarta.servlet.http.HttpServletResponse;
 import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
 import org.example.common.convention.exception.ClientException;
 import org.example.common.convention.result.Results;
 import org.springframework.data.redis.core.StringRedisTemplate;

 import java.io.IOException;
 import java.io.PrintWriter;
 import java.util.List;

 import static org.example.common.enums.UserErrorCodeEnum.USER_TOKEN_FAILED;

/**
 * 用户信息传输过滤器
 */
@RequiredArgsConstructor
@Slf4j
public class UserTransmitFilter implements Filter {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 忽略URI
     */
    private static final List<String> IGNORE_URI= Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username",
            "/api/short-link/admin/v1/user"
    );

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        if(!IGNORE_URI.contains(requestURI)){
            String username = httpServletRequest.getHeader("username");
            String token = httpServletRequest.getHeader("token");
            //判断username和token是否为空
            if(!StrUtil.isAllNotBlank(username,token)){
                returnJson((HttpServletResponse)servletResponse, Results.failure(new ClientException(USER_TOKEN_FAILED)));
                return;
            }
            //捕捉Redis抛出的异常
            Object userInfoJsonStr= null;
            try{
                userInfoJsonStr=stringRedisTemplate.opsForHash().get("login_"+username,token);
                if(userInfoJsonStr==null){
                    returnJson((HttpServletResponse)servletResponse, Results.failure(new ClientException(USER_TOKEN_FAILED)));
                    return;                }
            }catch(Exception e){
                returnJson((HttpServletResponse)servletResponse, Results.failure(new ClientException(USER_TOKEN_FAILED)));
                return;            }
            if(userInfoJsonStr!=null){
                UserInfoDTO userInfoDTO= JSON.parseObject(userInfoJsonStr.toString(),UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);
            }
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }

    /*返回客户端数据*/
    private void returnJson(HttpServletResponse response, String json) throws Exception{
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
        } finally {
            if (writer != null){
                writer.close();
            }
        }
    }
}
