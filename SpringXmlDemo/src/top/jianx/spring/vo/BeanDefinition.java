package top.jianx.spring.vo;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.Serializable;

/**
 * VO (Value Object)
 * @author jianx
 * 基于此对象存储Bean的配置信息
 */
public class BeanDefinition implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String pkgClass;
    private Boolean lazy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPkgClass() {
        return pkgClass;
    }

    public void setPkgClass(String pkgClass) {
        this.pkgClass = pkgClass;
    }

    public Boolean getLazy() {
        return lazy;
    }

    public void setLazy(Boolean lazy) {
        this.lazy = lazy;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "id='" + id + '\'' +
                ", pkgClass='" + pkgClass + '\'' +
                ", lazy=" + lazy +
                '}';
    }
}
