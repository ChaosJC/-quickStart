package quick.start.core.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * MybatisPlus多租户、防全表更新与删除、分页配置
 * @author Chaos
 */
@Configuration
public class MybatisTenantPlusConfig {

    @Resource
    private MyTenantLineHandler myTenantLineHandler;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //多租户插件，不用可注释
        TenantLineInnerInterceptor tenantLineInnerInterceptor = new MyTenantLineInnerInterceptor();
        tenantLineInnerInterceptor.setTenantLineHandler(myTenantLineHandler);
        interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        //防止更新全表插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        //分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;

    }

}