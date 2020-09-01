package cn.org.chaohui.mybatisplusdemo;

import cn.org.chaohui.mybatisplusdemo.entity.User;
import cn.org.chaohui.mybatisplusdemo.mapper.UserMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class MybatisplusdemoApplicationTests {

    @Autowired
    private UserMapper userMapper;

    //查询User表的所有数据
    @Test
    public void findAll() {
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
    }

    //添加操作
    @Test
    public void addUser(){
        /**
         * mybatisplus会自动添加id值,并且不重复
         */
        User user = new User();
        user.setName("amazon");
        user.setAge(61);
        user.setEmail("amazon@163.com");

//        user.setCreateTime(new Date());  //手动创建时间
//        user.setUpdateTime(new Date());
        //改进: 可以使用mybatisplus的自动填充方式实现

        int insert = userMapper.insert(user);
        System.out.println("insert : " + insert);
    }

    //修改操作
    @Test
    public void updateUser(){
        User user = new User();
        user.setAge(120);
        user.setId(1244512257237389314L);

        int row = userMapper.updateById(user);
        System.out.println(row);
    }

    //测试乐观锁插件
    @Test
    public void testOptimisticLocker(){
        /**
         * 需要先进行查询操作,然后才修改(需要比较)
         * 若乐观锁生效,当前的版本号在之前如果没有修改的情况下会递增
         */
        //根据id查询数据
        User user = userMapper.selectById(1244541680841936898L);

        //进行修改
        user.setAge(60);
        userMapper.updateById(user);
    }


    //测试乐观锁插件 失败
    @Test
    public void testOptimisticLockerFail() {

        //查询
        User user = userMapper.selectById(1244541680841936898L);
        //修改数据
        user.setName("Helen Yao1");
        user.setEmail("helen@qq.com1");

        //模拟取出数据后，数据库中version实际数据比取出的值大，即已被其它线程修改并更新了version
        user.setVersion(user.getVersion() - 1);

        //执行更新
        userMapper.updateById(user);
    }


    //多个id的批量查询
    @Test
    public void testSelectBatchIds(){
        List<User> users = userMapper.selectBatchIds(Arrays.asList(1, 2, 3));
        users.forEach(System.out::println);
    }

    //通过map封装查询条件
    @Test
    public void testSelectByMap(){

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "Helen");
        map.put("age", 18);
        List<User> users = userMapper.selectByMap(map);

        users.forEach(System.out::println);
    }

    //分页查询
    @Test
    public void testSelectPage(){

        //创建Page对象
        //传入参数: 当前页和每页显示记录数
        Page<User> page = new Page<>(1,5);
        //调用分页查询的方法
        //调用mp分页查询过程中,底层封装
        //将分页的所有数据封装到page对象中
        userMapper.selectPage(page, null);//null-->Wrapper条件

        //通过page对象获取分页数据
        System.out.println(page.getCurrent()); //当前页
        System.out.println(page.getRecords()); //当前的数据列表
        System.out.println(page.getSize()); //每页显示记录数
        System.out.println(page.getTotal()); //总记录数
        System.out.println(page.getPages()); //当前分页总页数

        System.out.println(page.hasNext()); //下一页
        System.out.println(page.hasPrevious()); //上一页

    }

    //测试selectMapsPage分页：结果集是Map
    @Test
    public void testSelectMapsPage() {
        Page<User> page = new Page<>(1, 5);

        IPage<Map<String, Object>> mapIPage = userMapper.selectMapsPage(page, null);

        //注意：此行必须使用 mapIPage 获取记录列表，否则会有数据类型转换错误
        System.out.println(page.getRecords());
        System.out.println(mapIPage.getRecords());
        mapIPage.getRecords().forEach(System.out::println);

        System.out.println(page.getCurrent());
        System.out.println(page.getPages());
        System.out.println(page.getSize());
        System.out.println(page.getTotal());
        System.out.println(page.hasNext());
        System.out.println(page.hasPrevious());
    }


    //根据id删除记录--物理删除
    @Test
    public void testDeleteById(){
        int result = userMapper.deleteById(1245284685911756801L);
        System.out.println(result);
    }

    //批量删除
    @Test
    public void testDeleteBatchIds() {

        int result = userMapper.deleteBatchIds(Arrays.asList(8, 9, 10));
        System.out.println(result);
    }

    /**
     * 测试 性能分析插件
     */
    @Test
    public void testPerformance() {
        User user = new User();
        user.setName("我是Helen");
        user.setEmail("helen@sina.com");
        user.setAge(18);
        userMapper.insert(user);
    }


}
