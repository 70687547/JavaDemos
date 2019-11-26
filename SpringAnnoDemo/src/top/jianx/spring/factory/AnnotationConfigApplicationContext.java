package top.jianx.spring.factory;

import top.jianx.spring.annotation.ComponentScan;
import top.jianx.spring.annotation.Lazy;
import top.jianx.spring.annotation.Service;
import top.jianx.spring.config.SpringConfig;
import top.jianx.spring.service.SysLogService;
import top.jianx.spring.service.SysUserService;
import top.jianx.spring.vo.BeanDefinition;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jianx
 * @since 1.0
 *
 * Spring配置解析工厂类
 *      IOC实现类
 */
@SuppressWarnings("all")
public class AnnotationConfigApplicationContext {
    //定义两个map分别存储bean对象的配置信息和对象实例
    private Map<String, BeanDefinition> beanMap = new HashMap<>();//bean对象配置
    private Map<String, Object> instanceMap = new HashMap<>();//对象实例

    public AnnotationConfigApplicationContext(Class<?> configClass) throws Exception {
        //1.读取配置类指定的包名
        ComponentScan cs = configClass.getDeclaredAnnotation(ComponentScan.class);
        //获取扫描到的包
        String pkg = cs.value();
        System.out.println(pkg);

        //2.扫描指定包中的类
        //将包换成路径
        String classPath = pkg.replace(".","/");
        System.out.println(classPath);

        //获取所在的绝对路径
        URL url = configClass.getClassLoader().getResource(classPath);


        //File  找到文件目录
        File fileDir = new File(url.getPath());//classPath为相对路径
        System.out.println(fileDir);

        //拦截判断是否是.class文件
        File[] classFiles = fileDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".class");
            }
        });
        //迭代拿对应的“.class"类文件
//        for (File file : classFiles) {
//            System.out.println(file.getName());
//        }

        //3.封装文件信息
        processClassFiles(pkg, classFiles);


    }

    /**
     * 3.封装文件信息
     * @param classFiles
     */
    private void processClassFiles(String pkgName, File[] classFiles) throws Exception {
        //迭代拿对应的“.class"类文件
        for (File file : classFiles) {
            String pkgAndClass = pkgName+"."+file.getName().substring(0, file.getName().lastIndexOf("."));
            System.out.println(pkgAndClass);

            //通过 包名+类名 获取到类
            Class<?> targetClass = Class.forName(pkgAndClass);
            //判断该类上有没有Service注解 * 1 *
            //if (!targetClass.isAnnotationPresent(Service.class))continue;

            //获取到类上的注解
            Service service = targetClass.getDeclaredAnnotation(Service.class);
            //判断该类上有没有Service注解 * 2 *
            if (service==null)continue;//同上if (!targetClass.isAnnotationPresent(Service.class))continue;

            BeanDefinition bd = new BeanDefinition();
            //注解有无value的处理
            bd.setId((""==service.value())?targetClass.getSimpleName():service.value());
            bd.setPkgClass(pkgAndClass);
            Lazy lazy = targetClass.getDeclaredAnnotation(Lazy.class);
            if (lazy!=null)
                bd.setLazy(lazy.value());

            //封装bean对象配置信息
            beanMap.put(bd.getId(), bd);
            if (!bd.getLazy()) {
                Object obj = newBeanInstance(targetClass);
                instanceMap.put(bd.getId(), obj);
            }
        }
    }

    /**
     * 反射构建一个类的实例
     * @param pkgAndClass
     * @return
     */
    private Object newBeanInstance(Class<?> pkgAndClass) throws Exception {
        //通过包名.类名 反射 取得类对象实例
//        Class<?> cls = Class.forName(pkgAndClass);
        //构造
        Constructor<?> constructor = pkgAndClass.getDeclaredConstructor();
        //私有构造设置可访问
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    /**
     * 获取bean实例对象
     * @param key
     * @param t
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T>T getBean(String key, Class<T> t) throws Exception {
        //1.判定当前instanceMap中是否有key对应bean的实例
        Object obj = instanceMap.get(key);
        if (obj != null)return (T)obj;
        //2.没有对象就创建对象
        obj = newBeanInstance(t);
        instanceMap.put(key, obj);
        return (T)obj;
    }

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext atx = new AnnotationConfigApplicationContext(SpringConfig.class);
        SysUserService sysUserService = atx.getBean("sysUserService", SysUserService.class);
        SysLogService sysLogService = atx.getBean("sysLogService", SysLogService.class);
        System.out.println(sysUserService);
        System.out.println(sysLogService);
    }
}
