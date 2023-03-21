package quick.start.core.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quick.start.commons.base.R;

/**
 * @author chaos
 * @create-date 2023/3/21
 * @description 登录
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * 登录
     *
     * @return
     */
    @GetMapping
    public R login() {
        StpUtil.login("10000");
        return R.success(StpUtil.getTokenValue());
    }


}
