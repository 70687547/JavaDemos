package top.jianx.spring.factory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import top.jianx.spring.vo.BeanDefinition;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jianx
 * Context
 */
@SuppressWarnings("all")
public class ClassPathXmlApplicationContext {
    //定义两个map分别存储bean对象的配置信息和对象实例
    private Map<String, BeanDefinition> beanMap= new HashMap<>();
    private Map<String, Object> instanceMap= new HashMap<>();

    public ClassPathXmlApplicationContext(String file) throws Exception{
        //1.读取配置文件(流)
        InputStream is = getClass().getClassLoader().getResourceAsStream(file);
        //2.解析文件
        parse(is);
        //3.封装配置信息数据

    }
    /**
     * 2.解析文件
     *  该方法是基于dom实现的xml解析
     *  市场主流xml解析：dom,dom4j,sax,pull,...
     *
     * @param inputStream
     * @throws ParserConfigurationException  DocumentBuilderx解析可能出现的异常
     * @throws IOException IO异常
     * @throws SAXException SAX解析异常
     */
    private void parse(InputStream inputStream) throws Exception {
        //2.1.创建解析器对象
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        //2.2.解析流对象
        Document doc = builder.parse(inputStream);
        //2.3.处理document
        processDocument(doc);
    }

    /**
     * 2.3 处理document
     * @param doc
     */
    private void processDocument(Document doc) throws Exception {
        //2.3.1 获取所有的bean元素
        NodeList nodeList = doc.getElementsByTagName("bean");
        //2.3.2 迭代bean元素
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);//下面是打印bean标签
            System.out.println("bean标签: "+node.getNodeName());

            /*准备node信息的NamedNodeMap类*/
            NamedNodeMap nodeMap = node.getAttributes();
            System.out.println(nodeMap);

            /*准备bean对象配置信息存储类*/
            BeanDefinition bd = new BeanDefinition();
            bd.setId(nodeMap.getNamedItem("id").getNodeValue());
            System.out.println("bean标签对应的beanID: "+bd.getId());
            bd.setPkgClass(nodeMap.getNamedItem("class").getNodeValue());
            bd.setLazy(Boolean.valueOf(nodeMap.getNamedItem("lazy").getNodeValue()));
            System.out.println(bd);

            //3.存储配置信息
            beanMap.put(bd.getId(),bd);
            if (!bd.getLazy()){
                //通过包名.类名 反射 取得类对象实例
                Class<?> cls = Class.forName(bd.getPkgClass());
                Object obj = newBeanInstance(cls);
            }
        }
    }

    /**
     * 反射构建一个类的实例
     * @param pkgAndClass
     * @return
     */
    private Object newBeanInstance(Class<?> pkgAndClass) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //通过包名.类名 反射 取得类对象实例
//        Class<?> cls = Class.forName(pkgAndClass);
        //构造
        Constructor<?> constructor = pkgAndClass.getDeclaredConstructor();
        //私有构造设置可访问
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    public <T>T getBean(String key, Class<T> t) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //1.判定当前instanceMap中是否有key对应bean的实例
        Object obj = instanceMap.get(key);
        if (obj != null)return (T)obj;
        //2.没有对象就创建对象
        obj = newBeanInstance(t);
        instanceMap.put(key, obj);
        return (T)obj;
    }

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-config.xml");
        Object obj = ctx.getBean("obj",Object.class);
        Date date = ctx.getBean("date", Date.class);
        System.out.println(obj);
        System.out.println(date);
    }

}
