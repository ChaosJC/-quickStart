package quick.start.commons;

/**
 * @author chaos
 * @create-date 2023/2/3
 * @description Redis_Key常量
 */
public class RedisKeyConstant {

    /**
     * 图片验证码Token
     */
    public static String IMAGE_CAPTCHA = "captcha:image:";
    /**
     * 手机验证码
     */
    public static String SMS_CAPTCHA = "captcha:sms:";
    /**
     * 多租户应用数据共享
     */
    public static String TENANT_APP_IDS = "tenant:appid:";


}
