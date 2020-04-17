package com.leyou.cart.interceptor;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.threadlocal.UserHolder;
import com.leyou.common.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * 这个类用来把cookie中的token解析出来后，把载荷部分放入ThreadLocal中
 * 方便我们在当前线程获取当前用户
 */
@Slf4j
@Component
public class UserInterceptor implements HandlerInterceptor {

    private final static String TOKEN_NAME = "LY_TOKEN";

    /**
     * 把用户放入ThreadLocal
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 获取cookie
            String token = CookieUtils.getCookieValue(request, TOKEN_NAME);
            // 解析token中用户信息
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, UserInfo.class);
            // 存入ThreadLocal
            UserHolder.setUser(payload.getUserInfo());
            // 放行
            return true;
        } catch (Exception e) {
            log.error("【购物车微服务】解析cookie中的用户信息失败：{}",e.getMessage(),e);
            return false;
        }
    }

    /**
     * 把ThreadLocal中的用户删除
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
