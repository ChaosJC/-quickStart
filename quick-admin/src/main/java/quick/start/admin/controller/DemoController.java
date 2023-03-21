package quick.start.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quick.start.commons.base.R;

/**
 * @author chaos
 * @create-date 2023/3/21
 * @description 类描述
 */
@RestController
@RequestMapping
public class DemoController {

    @GetMapping("/index")
    public R index() {
        return R.success("请求成功！");
    }
}
