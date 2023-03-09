package quick.start.commons.exception;

import com.google.common.util.concurrent.UncheckedExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import quick.start.commons.base.R;
import quick.start.commons.exception.error.ErrorCode;
import quick.start.commons.exception.error.ErrorCode.Internal;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 *
 * @author valarchie
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionInterceptor {

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                    HttpServletRequest request) {
        log.error("请求地址'{}',不支持'{}'请求", request.getRequestURI(), e.getMethod());
        return R.fail(ErrorCode.Client.COMMON_REQUEST_METHOD_INVALID, e.getMethod());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ApiException.class)
    public R<?> handleServiceException(ApiException e) {
        log.error(e.getMessage(), e);
        if (e.getErrorCode() == Internal.DB_INTERNAL_ERROR) {
            return R.fail(e.getErrorCode(), "请联系管理员查看错误日志");
        }
        return R.fail(e);
    }

    /**
     * 捕获缓存类当中的错误
     */
    @ExceptionHandler(UncheckedExecutionException.class)
    public R<?> handleServiceException(UncheckedExecutionException e) {
        log.error(e.getMessage(), e);
        return R.fail(Internal.GET_CACHE_FAILED);
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("请求地址'{}',发生未知异常.", request.getRequestURI(), e);
        return R.fail(Internal.UNKNOWN_ERROR);
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e, HttpServletRequest request) {
        log.error("请求地址'{}',发生系统异常.", request.getRequestURI(), e);
        return R.fail(Internal.UNKNOWN_ERROR);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public R<?> handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return R.fail(ErrorCode.Client.COMMON_REQUEST_PARAMETERS_INVALID, message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return R.fail(ErrorCode.Client.COMMON_REQUEST_PARAMETERS_INVALID, message);
    }


}
