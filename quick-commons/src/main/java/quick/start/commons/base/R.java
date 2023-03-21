package quick.start.commons.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.BindingResult;
import quick.start.commons.constant.RequestCode;

/**
 * 响应信息主体
 *
 * @author valarchie
 */
@Data
@AllArgsConstructor
public class R<T> {
    /**
     * 返回码  <br/>200-成功  <br/>201-失败
     */
    private Integer code;
    /**
     * 返回信息
     */
    private String msg;
    /**
     * 返回业务数据
     */
    private T data;

    public R(Integer code, String message) {
        this(code, message, null);
    }

    //成功
    public static <T> R<T> success() {
        return success(null);
    }

    public static <T> R<T> success(T data) {
        return new R<>(RequestCode.SUCCESS, "成功", data);
    }

    //失败
    public static <T> R<T> fail() {
        return fail(RequestCode.FAIL, "失败");
    }

    public static <T> R<T> fail(String message) {
        return fail(RequestCode.FAIL, message);
    }

    public static <T> R<T> fail(BindingResult result) {
        return fail(RequestCode.FAIL, result.getFieldError().getDefaultMessage());
    }

    public static <T> R<T> fail(Integer code, String message) {
        return new R<>(code, message, null);
    }

    public R code(Integer code) {
        this.code = code;
        return this;
    }

    public R msg(String msg) {
        this.msg = msg;
        return this;
    }

    public R data(T data) {
        this.data = data;
        return this;
    }

    public Boolean isSuccess() {
        return this.code == RequestCode.SUCCESS;
    }
}

