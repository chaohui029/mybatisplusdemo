package cn.org.chaohui.mybatisplusdemo;

import cn.org.chaohui.mybatisplusdemo.entity.User;
import cn.org.chaohui.mybatisplusdemo.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Wrapper ： 条件构造抽象类，最顶端父类
 *     AbstractWrapper ： 用于查询条件封装，生成 sql 的 where 条件
 *         QueryWrapper ： Entity 对象封装操作类，不是用lambda语法
 *         UpdateWrapper ： Update 条件封装，用于Entity对象更新操作
 *     AbstractLambdaWrapper ： Lambda 语法使用 Wrapper统一处理解析 lambda 获取 column。
 *         LambdaQueryWrapper ：看名称也能明白就是用于Lambda语法使用的查询Wrapper
 *         LambdaUpdateWrapper ： Lambda 更新封装Wrapper
 */
//@RunWith(SpringRunner.class)
@SpringBootTest
public class QueryWrapperTests {
    
    @Autowired
    private UserMapper userMapper;

    //ge、gt、le、lt、isNull、isNotNull
    @Test
    public void testDelete() {

        /*
            UPDATE
                user
            SET
                deleted=1
            WHERE
                deleted=0
                AND age >= 61
                AND email IS NOT NULL
         */
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .ge("age", 61)
                .isNotNull("email");
        int result = userMapper.delete(queryWrapper);
        System.out.println("delete return count = " + result);
    }

    //eq,ne
    @Test
    public void testSelectOne() {
        /*
           SELECT
                id,
                name,
                age,
                email,
                create_time,
                update_time,
                version,
                deleted
            FROM
                user
            WHERE
                deleted=0
                AND name = 'jone'
         */
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", "jone");

        User user = userMapper.selectOne(queryWrapper);
        System.out.println(user);
    }

    //between, notBetween--包含大小边界
   @Test
    public void testSelectCount(){
        /*
            SELECT
                COUNT(1)
            FROM
                user
            WHERE
                deleted=0
                AND age BETWEEN 20 AND 30
         */
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("age",20, 30);

       Integer count = userMapper.selectCount(queryWrapper);
       System.out.println(count);
    }

    //allEq
    @Test
    public void testSelectList() {


        /*
           SELECT
                id,
                name,
                age,
                email,
                create_time,
                update_time,
                version,
                deleted
            FROM
                user
            WHERE
                deleted=0
                AND name = 'Jack'
                AND id = 2
                AND age = 120
         */
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("id", 2);
        map.put("name", "Jack");
        map.put("age", 120);

        queryWrapper.allEq(map);
        List<User> users = userMapper.selectList(queryWrapper);

        users.forEach(System.out::println);//User(id=2, name=Jack, age=120, email=test2@baomidou.com, createTime=null, updateTime=null, version=null, deleted=0)
    }


    //like、notLike、likeLeft、likeRight
    @Test
    public void testSelectMaps() {

        /*
            SELECT
                id,
                name,
                age,
                email,
                create_time,
                update_time,
                version,
                deleted
            FROM
                user
            WHERE
                deleted=0
                AND name NOT LIKE '%e%'
                AND email LIKE 't%'
         */
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .notLike("name", "e")
                .likeRight("email", "t");

        List<Map<String, Object>> maps = userMapper.selectMaps(queryWrapper);//返回值是Map列表;selectMaps返回Map集合列表
        /*
        {deleted=false, name=Jack, id=2, age=120, email=test2@baomidou.com}
        {deleted=false, name=Tom, id=3, age=28, email=test3@baomidou.com}
        {deleted=false, name=Sandy, id=4, age=21, email=test4@baomidou.com}
         */
        maps.forEach(System.out::println);
    }

    //in、notIn、inSql、notinSql、exists、notExists
    /*
        in、notIn：
        notIn("age",{1,2,3})--->age not in (1,2,3)
        notIn("age", 1, 2, 3)--->age not in (1,2,3)
        inSql、notinSql：可以实现子查询
        例: inSql("age", "1,2,3,4,5,6")--->age in (1,2,3,4,5,6)
        例: inSql("id", "select id from table where id < 3")--->id in (select id from table where id < 3)
     */
    @Test
    public void testSelectObjs() {

        /*
            SELECT
                id,
                name,
                age,
                email,
                create_time,
                update_time,
                version,
                deleted
            FROM
                user
            WHERE
                deleted=0
                AND id IN (
                    select
                        id
                    from
                        user
                    where
                        id < 3
                )
         */
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //queryWrapper.in("id", 1, 2, 3);
        queryWrapper.inSql("id", "select id from user where id < 3"); //子查询

        List<Object> objects = userMapper.selectObjs(queryWrapper);//返回值是Object列表
        objects.forEach(System.out::println);
    }

    /**
     * ====================================================================UpdateWrapper===============================================================================
     * ================================================================================================================================================================
     */

    //or、and
    /*
        注意：这里使用的是 UpdateWrapper  / 不调用or则默认为使用 and 连
     */
    @Test
    public void testUpdate1() {
        /*
        ==>  Preparing: UPDATE user SET name=?, age=?, update_time=? WHERE deleted=0 AND name LIKE ? OR age BETWEEN ? AND ?
        ==> Parameters: Andy(String), 99(Integer), 2020-04-02 13:48:25.442(Timestamp), %h%(String), 20(Integer), 30(Integer)
        <==    Updates: 4

            UPDATE
                user
            SET
                name='Andy',
                age=99,
                update_time='2020-04-02 13:48:25.442'
            WHERE
                deleted=0
                AND name LIKE '%h%'
                OR age BETWEEN 20 AND 30

         */

        //修改值
        User user = new User();
        user.setAge(99);
        user.setName("Andy");

        //修改条件
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper
                .like("name", "h")
                .or()
                .between("age", 20, 30);

        int result = userMapper.update(user, userUpdateWrapper);

        System.out.println(result);
    }

    //嵌套or、嵌套and
    /*
         这里使用了lambda表达式，or中的表达式最后翻译成sql时会被加上圆括号
     */
    @Test
    public void testUpdate2() {
        /*
        ==>  Preparing: UPDATE user SET name=?, age=?, update_time=? WHERE deleted=0 AND name LIKE ? OR ( name = ? AND age <> ? )
        ==> Parameters: Apple(String), 56(Integer), 2020-04-02 13:59:40.178(Timestamp), %h%(String), 李白(String), 20(Integer)
        <==    Updates: 1

            UPDATE
                user
            SET
                name='Apple',
                age=56,
                update_time='2020-04-02 13:59:40.178'
            WHERE
                deleted=0
                AND name LIKE '%h%'
                OR (
                    name = '李白'
                    AND age <> 20
                )
         */

        //修改值
        User user = new User();
        user.setAge(56);
        user.setName("Apple");

        //修改条件
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper
                .like("name", "h")
                .or(i -> i.eq("name", "李白").ne("age", 20));

        int result = userMapper.update(user, userUpdateWrapper);

        System.out.println(result);
    }

    //orderBy、orderByDesc、orderByAsc
    @Test
    public void testSelectListOrderBy() {
        /*
            SELECT
                id,
                name,
                age,
                email,
                create_time,
                update_time,
                version,
                deleted
            FROM
                user
            WHERE
                deleted=0
            ORDER BY
                id DESC
         */

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    //last--直接拼接到 sql 的最后
    /*
        注意：只能调用一次,多次调用以最后一次为准 有sql注入的风险,请谨慎使用
     */
    @Test
    public void testSelectListLast() {

        /*
            SELECT
                id,
                name,
                age,
                email,
                create_time,
                update_time,
                version,
                deleted
            FROM
                user
            WHERE
                deleted=0 limit 1
         */
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("limit 1");

        List<User> users = userMapper.selectList(queryWrapper);
//        users.forEach(System.out::println);
        users.forEach((t) -> System.out.println(t)); //User(id=1, name=Jone, age=18, email=test1@baomidou.com, createTime=null, updateTime=null, version=null, deleted=0)
    }

    //指定要查询的列
    @Test
    public void testSelectListColumn() {
        /*
            SELECT
                id,
                name,
                age
            FROM
                user
            WHERE
                deleted=0
         */

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "name", "age");

        List<User> users = userMapper.selectList(queryWrapper);
//        users.forEach(System.out::println);
        users.forEach((User t) -> System.out.println(t));
    }


    //set、setSql
    /*
        最终的sql会合并 user.setAge()，以及 userUpdateWrapper.set()  和 setSql() 中 的字段
     */
    @Test
    public void testUpdateSet() {
        /*
          ==>  Preparing: UPDATE user SET age=?, update_time=?, name=?, email = '123@qq.com' WHERE deleted=0 AND name LIKE ?
          ==> Parameters: 27(Integer), 2020-04-02 14:26:36.526(Timestamp), 老李头(String), %A%(String)
          <==    Updates: 9

            UPDATE
                user
            SET
                age=27,
                update_time='2020-04-02 14:26:36.526',
                name='老李头',
                email = '123@qq.com'
            WHERE
                deleted=0
                AND name LIKE '%A%'
         */

        //修改值
        User user = new User();
        user.setAge(27);

        //修改条件
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper
                .like("name", "A")
                .set("name", "老李头")//除了可以查询还可以使用set设置修改的字段
                .setSql(" email = '123@qq.com'");//可以有子查询

        int result = userMapper.update(user, userUpdateWrapper);
    }


}