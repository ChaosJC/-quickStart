package quick.start.commons.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chaos
 * @create-date 2023/2/1
 * @description 基础分页
 */
@Data
public class BasePageReq implements Serializable {
    /**
     * 分页-当前页数
     */
    private int pageNum = 1;
    /**
     * 分页-每页条数
     */
    private int pageSize = 10;


}
