package quick.start.commons.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chaos
 * @create-date 2023/2/1
 * @description Entity基类
 */
@Data
public class BaseEntity<T extends Model<?>> extends Model<T> implements Serializable {

    /**
     * 乐观锁
     */
    @ApiModelProperty("乐观锁")
    @TableField("revision")
    private Integer revision;

    @ApiModelProperty("创建者ID")
    @TableField("create_id")
    private Long createId;

    @ApiModelProperty("创建者")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("更新者ID")
    @TableField("update_id")
    private Long updateId;

    @ApiModelProperty("更新者")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

/*
    @ApiModelProperty("删除标志（0代表存在 1代表删除）")
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;


    public void logCreator(BaseUser user) {
        if (user != null) {
            this.creatorId = user.getUserId();
            this.creatorName = user.getUsername();
        }
    }

    public void logUpdater(BaseUser user) {
        if (user != null) {
            this.updaterId = user.getUserId();
            this.updaterName = user.getUsername();
        }
    }
*/
}
