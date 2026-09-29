package com.wuji.framework.security.service;


import com.wuji.common.constant.CacheConstants;
import com.wuji.common.constant.Constants;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.TimeUtils;
import com.wuji.common.utils.redis.RedisCache;
import com.wuji.framework.model.domain.LoginUserDomain;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Service
public class TokenService {
    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // sso 解码ticket密钥
    @Value("${sso.client.secret:brRsKjrPk4}")
    private String ssoSecret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 55 * 60 * 1000L;

    @Autowired
    private RedisCache redisCache;


    public int getTokenExpireMinuter() {
        return this.expireTime * 24;
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUserDomain getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            if (token.startsWith("mcpToken_")) {
                token = token.replace("mcpToken_", "");
            }
            try {
                return getLoginUserDomain(token);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public LoginUserDomain getLoginUserDomain(String token) {
        Claims claims = parseToken(token);
        // 解析对应的权限以及用户信息
        String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
        String userKey = getTokenKey(uuid);
        return redisCache.getCacheObject(userKey);
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isEmpty(token)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(Constants.AUTH_COOKIE_NAME)) {
                        token = cookie.getValue();
                        // 使用获取到的value进行后续操作
                        break;
                    }
                }
            }
        }
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = getTokenKey(token);
            redisCache.deleteObject(userKey);
        }
    }

    public String parseSSOTicket(String ticket, String ssoSecret) {
        if (StringUtils.isEmpty(ssoSecret)) {
            ssoSecret = this.ssoSecret;
        }
        return Jwts.parser().setSigningKey(ssoSecret).parseClaimsJws(ticket).getBody().getSubject();
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUserDomain loginUser) {
        if (ObjectUtils.isNotEmpty(loginUser) && StringUtils.isNotEmpty(loginUser.getToken())) {
            refreshToken(loginUser, null);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUserDomain loginUser, Date expireTimeDate) {
        loginUser.setLoginTime(System.currentTimeMillis());
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        if (expireTimeDate == null) {
            loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
            redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
        } else {
            loginUser.setExpireTime(expireTimeDate.getTime());
            Long timeDifference = TimeUtils.getTimeDifference(new Date(), expireTimeDate);
            redisCache.setCacheObject(userKey, loginUser, timeDifference.intValue(), TimeUnit.DAYS);
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser  用户信息
     * @param expireTime
     * @return 令牌
     */
    public String createToken(LoginUserDomain loginUser, Date expireTime) {
        String token = ObjectId.getGuid();
        loginUser.setToken(token);
        refreshToken(loginUser, expireTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    // /**
    //  * 设置用户代理信息
    //  *
    //  * @param loginUser 登录信息
    //  */
    // public void setUserAgent(LoginUserDomain loginUser) {
    //     String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
    //     loginUser.setIpaddr(ip);
    // }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    public void verifyToken(LoginUserDomain loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser, null);
        }
    }

    public String getToken(HttpServletRequest request, String key) {
        String token = request.getHeader(header);
        if (StringUtils.isEmpty(token)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(key)) {
                        token = cookie.getValue();
                        // 使用获取到的value进行后续操作
                        break;
                    }
                }
            }
        }
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return CacheConstants.LOGIN_TOKEN_KEY + uuid;
    }
}
