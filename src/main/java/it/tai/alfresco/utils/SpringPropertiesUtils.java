package it.tai.alfresco.utils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.springframework.beans.factory.config.PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE;

/**
 * @author Francesco Fornasari T.A.I Software Solution s.r.l
 */
public final class SpringPropertiesUtils extends PropertyPlaceholderConfigurer {

    private static Map<String, String> propertiesMap;
    private static PropertiesConfiguration propertiesConfiguration;
    private static final Logger logger = LoggerFactory.getLogger(SpringPropertiesUtils.class);

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
        super.processProperties(beanFactory, props);
        propertiesMap = new HashMap<>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String valueStr = resolvePlaceholder(keyStr, props, SYSTEM_PROPERTIES_MODE_OVERRIDE);
            propertiesMap.put(keyStr, valueStr);
        }

        try {

            if (System.getProperty("conf") != null) {
                Resource resource = new FileSystemResource(System.getProperty("conf"));
                propertiesConfiguration = new PropertiesConfiguration(resource.getFile());
            } else {
                URL fileConfig = this.getClass().getClassLoader().getResource("conf.properties");
                propertiesConfiguration = new PropertiesConfiguration(fileConfig);
            }

        } catch (Exception e) {
            logger.error("SpringPropertiesUtil error initializing properties", e);
        }
    }

    public static String getProperty(String name) {
        return propertiesConfiguration.getString(name);
    }

    public static Map<String, String> getPropertyList(String name) {
        List<Object> objects = propertiesConfiguration.getList(name);
        Map<String, String> map = null;
        if (null != objects) {
            map = new HashMap<>();
            String row, u, p;
            for (Object item : objects) {
                row = (String) item;
                if (null != row && !row.isEmpty()) {
                    Integer pos = row.indexOf("#");
                    if (pos > 0 && pos + 1 < row.length()) {
                        u = row.substring(0, pos);
                        p = row.substring(pos + 1, row.length());
                        if (!u.isEmpty() && !p.isEmpty()) {
                            map.put(u, p);
                        }
                    }
                }
            }
        }
        return map;
    }
}
