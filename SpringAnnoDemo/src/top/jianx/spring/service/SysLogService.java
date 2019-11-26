package top.jianx.spring.service;

import top.jianx.spring.annotation.Lazy;
import top.jianx.spring.annotation.Service;

/**
 * @author jianx
 * @since 1.0
 *
 * 注解测试类
 */
@Service("sysLogService")
@Lazy(true)
public class SysLogService {
    public SysLogService() {
        System.out.println("SysLogService");
    }
}
