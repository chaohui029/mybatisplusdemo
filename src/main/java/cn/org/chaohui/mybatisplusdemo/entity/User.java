package cn.org.chaohui.mybatisplusdemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.File;
import java.util.Date;

/**
 * @Auther: chaohui
 * @Date: 2020/3/29
 * @Description: cn.org.chaohui.mybatisplusdemo.entity
 * @version: 1.0
 */

@Data
public class User {

    //    @TableId(type = IdType.ID_WORKER_STR)  //mp自带策略,生成19位值,字符串类型使用这种策略
//    @TableId(type = IdType.AUTO)  //自动递增;需要在创建数据表的时候设置主键自增
    @TableId(type = IdType.ID_WORKER)  //mp自带策略(默认此种策略),生成19位值,数字类型使用这种策略,比如long
    private Long id;

    private String name;
    private Integer age;
    private String email;

    //create_time
    @TableField(fill = FieldFill.INSERT) //mp自动填充(在进行添加操作的时候,若没有设置相应的createTime,加上该注解,就会自动添加)
    private Date createTime;

    //update_time
    @TableField(fill = FieldFill.INSERT_UPDATE) //mp自动填充,在添加和修改的时候都有值
    private Date updateTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version; //版本号

    @TableLogic  //处理逻辑删除
    private Integer deleted; //此处不需要设置自动填充@TableField(在数据库表中已经设置默认值0,就不能再次设置自动填充)


}
