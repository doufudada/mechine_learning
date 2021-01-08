//将两个表的ID统一

import util.JdbcUtil;

import java.sql.*;

public class IDchange {
    public static void main(String[] args)  throws ClassNotFoundException, SQLException{
        //获取数据库连接
        Connection connection = JdbcUtil.getConnection();
        //需要执行的sql语句

        //规范ID,姓名，性别，身高
        Statement statement=connection.createStatement();
        {
            statement.execute("update student set ID=ID+202000");
            statement.execute("update student set name=replace(name,' ','');");
            statement.execute("update student set Gender='male' where Gender='boy';");
            statement.execute("update student set Gender='female' where Gender='girl';");
            statement.execute("update student set Height=Height*100 where Height<100 AND Height IS NOT NULL;");
        }
        statement.close();

        //删除重复数据,根据ID，姓名，城市，身高来删除，不选择性别是因为有一些数据，在两个数据源中的性别是不统一的
        //但由于我也不知道具体性别究竟是什么所以直接选择了随机挑选性别
        //jdbc连接sql语言会将数据项多的留下来，所以留下的表单已经完成了第一次空值填补
        String sqlNew="CREATE TABLE newstudent SELECT * FROM student GROUP BY ID,NAME,City,Height";
        PreparedStatement psNew=connection.prepareStatement(sqlNew);
        psNew.executeUpdate();//执行更新
        psNew.close();

        //删除原student表
        String sqlDrop="drop table student";
        PreparedStatement psDrop=connection.prepareStatement(sqlDrop);
        psDrop.executeUpdate();
        psDrop.close();

        //按ID排序
        String sqlSort="ALTER TABLE newstudent ORDER BY ID ASC";
        PreparedStatement psSort=connection.prepareStatement(sqlSort);
        psSort.executeUpdate();//执行更新
        psSort.close();

        //填写剩余的空值
        String sqlConstitution="update newstudent set Constitution='good' where Constitution=''";
        PreparedStatement psConstitution=connection.prepareStatement(sqlConstitution);
        psConstitution.executeUpdate();//执行更新
        psConstitution.close();

        connection.close();    //关闭jdbc连接
    }
}