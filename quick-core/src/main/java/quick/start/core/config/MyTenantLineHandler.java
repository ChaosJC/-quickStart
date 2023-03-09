package quick.start.core.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.stereotype.Component;
import quick.start.commons.RedisKeyConstant;
import quick.start.commons.utils.RedisUtil;

import javax.annotation.Resource;

/**
 * @author Chaos
 */
@Slf4j
@Component
public class MyTenantLineHandler implements TenantLineHandler {
    @Resource
    private RedisUtil redisUtil;

    /**
     * 获取租户ID 实际应该从用户信息中获取
     * 在登录时，获取来源应用，通过该应用ID查询共享应用列表，该列表应用ID即多租户ID
     * 把这些ID通过逗号分割成字符串存入Redis即可
     *
     * @return
     */
    @Override
    public Expression getTenantId() {
        // 获取RedisUtil
//        RedisUtil redisUtil1 = SpringUtil.getBean(RedisUtil.class);
        //获取当前登录用户的租户ID列表
        return new StringValue((String) redisUtil.get(RedisKeyConstant.TENANT_APP_IDS));
    }

    /**
     * 获取租户表字段
     *
     * @return
     */
    @Override
    public String getTenantIdColumn() {
        return "app_id";
    }

    /**
     * 表过滤，返回true，表示当前表不进行租户过滤
     *
     * @param tableName 表名
     * @return
     */
    @Override
    public boolean ignoreTable(String tableName) {
        // 排除user表
        return "user".equalsIgnoreCase(tableName);
    }


}

