package quick.start.core.config;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Table;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chaos
 * @create-date 2023/2/3
 * @description 重写mybatis-plus的多租户条件拼接方法，改为in拼接
 */
public class MyTenantLineInnerInterceptor extends TenantLineInnerInterceptor {
    /**
     * 处理条件:
     * 创建InExpression，即封装where appId in ('','')
     */
    @Override
    protected Expression builderExpression(Expression currentExpression, List<Table> tables) {
        // 没有表需要处理直接返回
        if (CollectionUtils.isEmpty(tables)) {
            return currentExpression;
        }
        // 构造每张表的条件
        List<Table> tempTables = tables.stream()
                .filter(x -> !super.getTenantLineHandler().ignoreTable(x.getName()))
                .collect(Collectors.toList());
        // 没有表需要处理直接返回
        if (CollectionUtils.isEmpty(tempTables)) {
            return currentExpression;
        }
        //Expression tenantId = super.getTenantLineHandler().getTenantId();
        //切割多租户ID字符串并转换成JSQLParser需要的元素列表
        ItemsList itemsList = new ExpressionList(
                Arrays.stream(
                                String.valueOf(super.getTenantLineHandler().getTenantId()).split(","))
                        .map(StringValue::new).collect(Collectors.toList()));
        List<InExpression> inExpressions = tempTables.stream()
                .map(item -> new InExpression(getAliasColumn(item), itemsList))
                .collect(Collectors.toList());
        // 注入的表达式
        Expression injectExpression = inExpressions.get(0);
        // 如果有多表，则用 and 连接
        if (inExpressions.size() > 1) {
            for (int i = 1; i < inExpressions.size(); i++) {
                injectExpression = new AndExpression(injectExpression, inExpressions.get(i));
            }
        }
        if (currentExpression == null) {
            return injectExpression;
        }
        if (currentExpression instanceof OrExpression) {
            return new AndExpression(new Parenthesis(currentExpression), injectExpression);
        } else {
            return new AndExpression(currentExpression, injectExpression);
        }
        //return super.builderExpression(currentExpression, tables);

    }
}
