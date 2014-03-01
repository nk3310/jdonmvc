package com.jdon.mvc.plugin.spring;

import com.jdon.mvc.annotations.Plugin;
import com.jdon.mvc.core.ComponentHolder;
import com.jdon.mvc.plugin.JdonMvcPlugin;
import com.jdon.mvc.util.TypeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletContext;

/**
 * 要使用spring，就在classpath中放一个SpringContext.xml配置文件
 * 然后在mvc.properties中设置useSpring?=true
 * User: oojdon
 * Date: 13-6-17
 * Time: 下午4:42
 */
@Plugin
public class SpringPlugin implements JdonMvcPlugin {

    private final static Log LOG = LogFactory.getLog(SpringPlugin.class);

    public static final String IOC_CONFIG = "useSpring?";

    private static final String CONFIG_FILE_DEFAULT_VALUE = "classpath:SpringContext.xml";

    protected static final String SPRING_CONTEXT_KEY = SpringPlugin.class.getName() + ".ROOT";


    @Override
    public void init(ComponentHolder holder) {
        if (TypeUtil.boolTrue(holder.getConfigItem(IOC_CONFIG))) {
            ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONFIG_FILE_DEFAULT_VALUE);
            holder.getServletContext().setAttribute(SPRING_CONTEXT_KEY, applicationContext);
            LOG.info("will replace the default IOC container with spring");
            holder.setBeanProvider(new SpringProvider());
        }
    }

    @Override
    public void dispose(ServletContext servletContext) {
        servletContext.removeAttribute(SPRING_CONTEXT_KEY);
    }
}
