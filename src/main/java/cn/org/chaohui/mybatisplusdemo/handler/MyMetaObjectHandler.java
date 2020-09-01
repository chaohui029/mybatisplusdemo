package cn.org.chaohui.mybatisplusdemo.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Auther: chaohui
 * @Date: 2020/3/30
 * @Description: cn.org.chaohui.mybatisplusdemo.handler
 * @version: 1.0
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyMetaObjectHandler.class);

    //使用mp实现添加操作,此方法会执行
    @Override
    public void insertFill(MetaObject metaObject) {
        LOGGER.info("start insert fill ....");
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);

        //设置添加时的初始版本为1
        this.setFieldValByName("version", 1, metaObject);

    }

    //使用mp实现修改操作,此方法会执行
    @Override
    public void updateFill(MetaObject metaObject) {
        LOGGER.info("start update fill ....");
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}
