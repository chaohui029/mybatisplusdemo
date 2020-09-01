package cn.org.chaohui.mybatisplusdemo.mapper;

import cn.org.chaohui.mybatisplusdemo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * @Auther: chaohui
 * @Date: 2020/3/29
 * @Description: cn.org.chaohui.mybatisplusdemo.mapper
 * @version: 1.0
 */

/**
 * BaseMapper已经存在相应的crud操作,只需继承即可,类似于之前
 * 写过的OA项目中的MybatisBaseDao
 */
@Repository  //@Respository加在对应的mapper上,这样在测试时需要添加@Autowired的时候能够找到对应的实现类对象(存放在spring容器中)
public interface UserMapper extends BaseMapper<User> {
}
