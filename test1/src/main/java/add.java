//合并表
import util.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class add{
    public static void main(String[] args) throws ClassNotFoundException, SQLException{
        Connection connection = JdbcUtil.getConnection();
        String sql="CREATE TABLE student\n" +
                "select * from student1\n" +
                "UNION ALL\n" +
                "select * from student2";
        PreparedStatement ps=connection.prepareStatement(sql);
        ps.executeUpdate();//执行更新
        ps.close();
        connection.close();
    }
}
