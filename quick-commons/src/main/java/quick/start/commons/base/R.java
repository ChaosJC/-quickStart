package quick.start.commons.base;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.BindingResult;
import quick.start.commons.exception.ApiException;
import quick.start.commons.exception.error.ErrorCode;
import quick.start.commons.exception.error.ErrorCodeInterface;

/**
 * 响应信息主体
 *
 * @author valarchie
 */
@Data
@AllArgsConstructor
public class R<T> {

    private Integer code;

    private String msg;

    private T data;

    public static <T> R<T> success() {
        return build(null, ErrorCode.SUCCESS);
    }
    public static <T> R<T> success(T data) {
        return build(data, ErrorCode.SUCCESS);
    }

    public static <T> R<T> fail() {
        return build(null, ErrorCode.FAIL);
    }

    public static <T> R<T> fail(ErrorCodeInterface code) {
        return build(null, code);
    }

    public static <T> R<T> fail(ApiException exception) {
        return new R<>(exception.getErrorCode().code(), exception.getMessage(), null);
    }

    public static <T> R<T> fail(ErrorCodeInterface code, Object... args) {
        return build(code, args);
    }

    public static <T> R<T> fail(T data) {
        return build(ErrorCode.FAIL, data);
    }

    public static <T> R<T> fail(String msg) {
        return  new R<>(ErrorCode.FAIL.code(), msg, null);
    }

    public static <T> R<T> fail(Integer code, String message) {
        return new R<>(code, message, null);
    }

    public static <T> R<T> fail(BindingResult result) {
        return fail(ErrorCode.FAIL.code(), result.getFieldError().getDefaultMessage());
    }

    public static <T> R<T> build(T data, ErrorCodeInterface code) {
        return new R<>(code.code(), code.message(), data);
    }

    public static <T> R<T> build(ErrorCodeInterface code, Object... args) {
        return new R<>(code.code(), StrUtil.format(code.message(), args), null);
    }

    public static <T> R<T> build(T data, ErrorCodeInterface code, Object... args) {
        return new R<>(code.code(), StrUtil.format(code.message(), args), data);
    }

}

