package quick.start.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import quick.start.commons.constant.Constants;

import java.io.File;

/**
 * @author chaos
 * @create-date 2023/3/9
 * @description 系统配置
 */
@Component
@ConfigurationProperties(prefix = "shishu")
@Data
public class SysConfig {

    /**
     * 获取地址开关
     */
    private static boolean addressEnabled;

    /**
     * 上传路径
     */
    private static String fileBaseDir;


    public static boolean isAddressEnabled() {
        return addressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled) {
        SysConfig.addressEnabled = addressEnabled;
    }

    public static String getFileBaseDir() {
        return fileBaseDir;
    }

    public void setFileBaseDir(String fileBaseDir) {
        SysConfig.fileBaseDir = fileBaseDir + File.separator + Constants.RESOURCE_PREFIX;
    }
}
