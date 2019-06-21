package com.yanwu.spring.cloud.common.mvc.exception;

import com.yanwu.spring.cloud.common.core.exception.BusinessException;
import com.yanwu.spring.cloud.common.mvc.exception.model.ExceptionResult;
import com.yanwu.spring.cloud.common.mvc.exception.model.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Slf4j
public class CustomizeMappingExceptionResolver extends SimpleMappingExceptionResolver {

    private MessageSource messageSource;

    private String exceptionAttribute = DEFAULT_EXCEPTION_ATTRIBUTE;

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                              Exception ex) {

        if (ex instanceof BusinessException) {
            log.warn("The business exception is cached and resolved", ex);
        } else if (ex instanceof HttpException) {
            log.warn("The http exception is cached and resolved", ex);
        } else {
            log.error("The runtime exception is cached and resolved", ex);
        }
        // Expose ModelAndView for chosen error view.
        String viewName = determineViewName(ex, request);
        if (viewName != null) {
            // Apply HTTP status code for error views, if specified and not
            // HttpException.
            // Only apply it if we're processing a top-level request.
            if (ex instanceof HttpException) {
                applyStatusCodeIfPossible(request, response, ((HttpException) ex).getHttpStatusCode());
            } else {
                Integer statusCode = determineStatusCode(request, viewName);
                if (statusCode != null) {
                    applyStatusCodeIfPossible(request, response, statusCode);
                }
            }
            return getModelAndView(viewName, ex, request);
        } else {
            return null;
        }
    }

    @Override
    protected ModelAndView getModelAndView(String viewName, Exception ex, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView(viewName);
        if (this.exceptionAttribute != null) {
            Locale locale = RequestContextUtils.getLocale(request);
            if (ex instanceof BusinessException) {
                String message = messageSource.getMessage(((BusinessException) ex).getMessageCode(),
                        ((BusinessException) ex).getMessageArgs(), ((BusinessException) ex).getMessage(), locale);
                ExceptionResult exceptionResult = new ExceptionResult(this.getStatusCodesAsMap().get(viewName), message,
                        ((BusinessException) ex).getExceptionCode(), ex.getMessage());
                mv.addObject(this.exceptionAttribute, exceptionResult);
            } else if (ex instanceof HttpException) {
                String message = messageSource.getMessage(
                        HttpResult.HTTP_STATUS_CODE_MAPPING_MESSAGE_PREFIX + ((HttpException) ex).getHttpStatusCode(),
                        null, ((HttpException) ex).getMessage(), locale);
                ExceptionResult exceptionResult = new ExceptionResult(((HttpException) ex).getHttpStatusCode(), message,
                        ((HttpException) ex).getHttpStatusCode(), ex.getMessage());
                mv.addObject(this.exceptionAttribute, exceptionResult);
            } else {
                String message = messageSource.getMessage(
                        HttpResult.HTTP_STATUS_CODE_MAPPING_MESSAGE_PREFIX + this.getStatusCodesAsMap().get(viewName),
                        null, ex.getMessage(), locale);
                ExceptionResult exceptionResult = new ExceptionResult(this.getStatusCodesAsMap().get(viewName), message,
                        ex.getMessage());
                mv.addObject(this.exceptionAttribute, exceptionResult);
            }
        }
        return mv;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getExceptionAttribute() {
        return exceptionAttribute;
    }

    @Override
    public void setExceptionAttribute(String exceptionAttribute) {
        this.exceptionAttribute = exceptionAttribute;
    }
}
