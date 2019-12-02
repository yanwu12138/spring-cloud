package com.yanwu.spring.cloud.base.filter;

import com.yanwu.spring.cloud.base.cache.YanwuCacheManager;
import com.yanwu.spring.cloud.base.service.YanwuUserService;
import com.yanwu.spring.cloud.common.core.common.AccessToken;
import com.yanwu.spring.cloud.common.core.common.ContextUtil;
import com.yanwu.spring.cloud.common.core.exception.AuthorizationException;
import com.yanwu.spring.cloud.common.utils.AccessTokenUtil;
import com.yanwu.spring.cloud.common.utils.IpMacUtil;
import com.yanwu.spring.cloud.common.data.base.HiveContext;
import com.yanwu.spring.cloud.common.data.base.HiveContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * token
 *
 * @author XuBaofeng.
 * @date 2018/6/11.
 */
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception arg3) throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView model) throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String reqMethod = request.getMethod();
        String reqUri = request.getRequestURI();
        log.info("preHandle Request: {} ,Method: {} ,URL: {}", request.getClass().getSimpleName(), reqMethod, reqUri);
        log.debug("Attrib: {}", enumeration2String(request.getAttributeNames()));
        log.info("Header: {}", getFullHeaderInfo(request));
        log.info("Param: {}", getFullParamInfo(request));
        if (request instanceof MultipartHttpServletRequest) {
            HttpHeaders headers = ((MultipartHttpServletRequest) request).getRequestHeaders();
            log.info("MultipartHttpServletRequest Headers: {}", headers);
        }
        response.setCharacterEncoding("utf-8");
        String token = request.getParameter("token");
        if (token == null) {
            String tmpStr = request.getHeader("Authorization");
            if (tmpStr != null && tmpStr.startsWith("Bearer ")) {
                token = tmpStr.substring(7);
            }
        }
        String originHeader = request.getHeader(HttpHeaders.ORIGIN);
        if (originHeader != null && reqMethod.compareToIgnoreCase("OPTIONS") == 0) {
            log.info("CORS URL: {},method: {},originHeader: {}", reqUri, reqMethod, originHeader);
            return true;
        }
        if (null != token) {
            try {
                AccessToken tokenObj = AccessTokenUtil.verifyToken(token);
                YanwuCacheManager tokenCache = (YanwuCacheManager) ContextUtil.getBean("tokenCache");
                String tokenStr = tokenCache.get(tokenObj.getUserId());
                if (StringUtils.isBlank(tokenStr) || !token.equals(tokenStr)) {
                    response.setHeader("Access-Control-Allow-Origin", "*");
                    throw new AuthorizationException(AuthorizationException.EXCEPTIONCODE_AUTH_TOKEN_ISNULL,
                            "request.token.notoken", "no token in request");
                }
                YanwuUserService userService = ContextUtil.getBean(YanwuUserService.class);
                String userName = userService.findUserNameById(tokenObj.getUserId());

                HiveContext context = new HiveContext();
                context.setUserId(tokenObj.getUserId());
                context.setReqUrl(reqUri);
                context.setUserName(userName);
                context.setAccessToken(token);
                context.setHostIp(IpMacUtil.getIpAddr(request));
                HiveContextHolder.setContext(context);
                return true;
            } catch (Exception e) {
                response.setHeader("Access-Control-Allow-Origin", "*");
                throw new AuthorizationException(AuthorizationException.EXCEPTIONCODE_AUTH_TOKEN_ISNULL,
                        "request.token.notoken", "no token in request");
            }
        } else {
            response.setHeader("Access-Control-Allow-Origin", "*");
            throw new AuthorizationException(AuthorizationException.EXCEPTIONCODE_AUTH_TOKEN_ISNULL,
                    "request.token.notoken", "no token in request");
        }
    }

    private String enumeration2String(Enumeration<String> enum1) {
        String attList = "";
        if (enum1 != null) {
            while (enum1.hasMoreElements()) {
                attList = attList + "," + enum1.nextElement();
            }
        }
        return attList;
    }

    private String getFullHeaderInfo(HttpServletRequest request) {
        String retStr = "";
        try {
            Enumeration<String> heads = request.getHeaderNames();
            if (heads != null) {
                int i = 0;
                while (heads.hasMoreElements()) {

                    if (i++ > 100) {
                        log.error("hasMoreElements{}", i);
                        break;
                    }
                    String head = heads.nextElement();
                    String value = request.getHeader(head);
                    retStr = retStr + "," + head + "=[" + value + "]";
                }
            }
        } catch (Exception e) {
            log.error("getFullHeaderInfo", e);
        }
        return retStr;
    }

    private String getFullParamInfo(HttpServletRequest request) {
        String retStr = "";
        try {
            Enumeration<String> params = request.getHeaderNames();

            if (params != null) {
                int i = 0;
                while (params.hasMoreElements()) {
                    if (i++ > 100) {
                        log.error("hasMoreElements{}", i);
                        break;
                    }
                    String param = params.nextElement();
                    String value = request.getParameter(param);
                    retStr = retStr + "," + param + "=[" + value + "]";
                }
            }
        } catch (Exception e) {
            log.error("getFullHeaderInfo", e);
        }
        return retStr;
    }

}
