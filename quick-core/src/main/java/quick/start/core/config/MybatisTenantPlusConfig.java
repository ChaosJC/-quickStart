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
 */
@Configuration
public class MybatisTenantPlusConfig {

    @Resource
    private MyTenantLineHandler myTenantLineHandler;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        TenantLineInnerInterceptor tenantLineInnerInterceptor = new MyTenantLineInnerInterceptor();
        tenantLineInnerInterceptor.setTenantLineHandler(myTenantLineHandler);
        //多租户插件
        //interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;

    }

}