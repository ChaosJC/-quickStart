package quick.start.core.config.mybatis;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自动注入创建时间和更新时间
 * @author Chaos
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createBy", String.class, StringUtils.isNotEmpty(StpUtil.getLoginIdAsString()) ? StpUtil.getLoginIdAsString() : "未获取到用户信息（未登录）!");
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateBy", String.class, StringUtils.isNotEmpty(StpUtil.getLoginIdAsString()) ? StpUtil.getLoginIdAsString() : "未获取到用户信息（未登录）!");
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}
