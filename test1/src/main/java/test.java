import util.JdbcUtil;

import java.sql.*;

public class test {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        //获取数据库连接
        Connection connection= JdbcUtil.getConnection();

        //求成绩平均值
        //统计学生中家乡在Beijing的所有课程的平均成绩。
        PreparedStatement outgrade = connection.prepareStatement("select name,avggrade from newstudent ");
        ResultSet rsGrade=outgrade.executeQuery();
        while (rsGrade.next()){
            if (rsGrade.getString("avggrade")!=null){
                System.out.println("在北京的"+rsGrade.getString("Name")+"同学的平均成绩是"+rsGrade.getString("avggrade"));
            }
        }
        rsGrade.close();
//
        //统计学生中家乡在广州，课程1在80分以上，且课程10在9分以上的男同学的数量。
        PreparedStatement outcount = connection.prepareStatement("select C1,C9 from newstudent where City='Beijing' and Gender='male'");
        ResultSet rscount=outcount.executeQuery();
        int count=0;//统计数量
        while (rscount.next()){
            if (rscount.getInt("C1")>80&&rscount.getInt("C9")>9){
                count++;
            }
        }
        System.out.println("学生中家乡在广州，课程1在80分以上，且课程10在9分以上的男同学的数量为"+count+"个");
        rscount.close();

//        比较广州和上海两地女生的平均体能测试成绩，哪个地区的更强些？
//        由于体能测试是根据excellent,good,general,bad四个评价来评分，没有具体的分数
//        所以我将excellent设置为4分,good设置为3分,general设置为2分,bad设置为1分
//        根据这四种分数来进行平均,最后将两座城市的平均数作比较,得出两座城市哪个城市的女生平均体能测试成绩更好
        PreparedStatement gradeCompare = connection.prepareStatement("select Constitution,City from newstudent where City between 'Guangzhou' and 'Shanghai'");
        ResultSet rsCompare=gradeCompare.executeQuery();
        int Guangzhou_count=0,Shanghai_count=0;//统计数量
        int Guangzhou_grade=0,Shanghai_grade=0;//统计成绩
        while (rsCompare.next()){
            if (rsCompare.getString("Ciyt")=="Guangzhou"){
                if (rsCompare.getString("Constitution")=="excellent") Guangzhou_grade=Guangzhou_grade+4;
                if (rsCompare.getString("Constitution")=="good") Guangzhou_grade=Guangzhou_grade+3;
                if (rsCompare.getString("Constitution")=="general") Guangzhou_grade=Guangzhou_grade+2;
                if (rsCompare.getString("Constitution")=="bad") Guangzhou_grade=Guangzhou_grade+1;
                Guangzhou_count++;//广州人数加一
            }
            if (rsCompare.getString("City")=="Shanghai"){
                if (rsCompare.getString("Constitution")=="excellent") Shanghai_grade=Shanghai_grade+4;
                if (rsCompare.getString("Constitution")=="good") Shanghai_grade=Shanghai_grade+3;
                if (rsCompare.getString("Constitution")=="general") Shanghai_grade=Shanghai_grade+2;
                if (rsCompare.getString("Constitution")=="bad") Shanghai_grade=Shanghai_grade+1;
                Shanghai_count++;//上海人数加一
            }
        }
        if (Guangzhou_grade/Guangzhou_count>Shanghai_grade/Shanghai_count) System.out.println("广州女生的平均体能测试成绩更好");
        if (Guangzhou_grade/Guangzhou_count==Shanghai_grade/Shanghai_count) System.out.println("两地女生的平均体能测试成绩一样好");
        if (Guangzhou_grade/Guangzhou_count<Shanghai_grade/Shanghai_count) System.out.println("上海女生的平均体能测试成绩更好");
        rsCompare.close();

        //计算general均值和标准差
        PreparedStatement grade_general = connection.prepareStatement("select Constitution from newstudent");
        ResultSet grade1_general=grade_general.executeQuery();
        int cons[]=new int[107];
        int sum=0;
        while (grade1_general.next()){
            for (int i=0;i<107;i++){
                if (grade1_general.getString("Constitution")=="excellent")
                    cons[i]=4;
                if (grade1_general.getString("Constitution")=="good")
                    cons[i]=3;
                if (grade1_general.getString("Constitution")=="general")
                    cons[i]=2;
                if (grade1_general.getString("Constitution")=="bad")
                    cons[i]=1;
            }
        }
        //求general总和
        for (int i=0;i<cons.length;i++){
            sum=sum+cons[i];
            System.out.println(sum);
        }
        //求general均值
        int sum_general=sum/cons.length;
        //计算方差
        int total=0;
        for (int i=0;i<cons.length;i++){
            total=total+(sum-cons[i])*(sum-cons[i]);
        }
        int total_general=total/cons.length;

        int general_diancheng[]=new int[107];
        for (int i=0;i<107;i++){
            general_diancheng[i]=(cons[i]-sum_general)/total_general;
        }

        //grade函数用来求除体育课外各科的成绩，平均值，方差，标准差，点乘，以及最后的相关性
        //对于十分制的成绩，将其乘以十化为百分制
        //将体测成绩的点乘传入，与求出的各科成绩点乘相乘累加，求出最后的相关性
        grade(general_diancheng,"C1");
        grade(general_diancheng,"C2");
        grade(general_diancheng,"C3");
        grade(general_diancheng,"C4");
        grade(general_diancheng,"C5");
        grade(general_diancheng,"C6");
        grade(general_diancheng,"C7");
        grade(general_diancheng,"C8");
        grade(general_diancheng,"C9");

    }


    //将成绩化为百分制，十分制的乘以十
    public static void grade(int general_diancheng[],String x) throws SQLException {
        Connection conn= JdbcUtil.getConnection();
        PreparedStatement grade_C = conn.prepareStatement("select "+x+" from newstudent");
        ResultSet grade1_C=grade_C.executeQuery();
        while (grade1_C.next()){
            if (grade1_C.getInt(x)/10==0){
                PreparedStatement newgrade = conn.prepareStatement("update newstudent set x=x*100");
                ResultSet newgrade_C=newgrade.executeQuery();
            }
        }


        int cons[]=new int[107];
        int sum=0,total=0;
        while (grade1_C.next()){
            for (int i=0;i<107;i++){
                sum=sum+cons[i];
            }
        }
        int sum_c=sum/cons.length;
        for (int i=0;i<cons.length;i++){
            total=total+(sum-cons[i])*(sum-cons[i]);
        }
        int total_c=total/cons.length;
        int C_diancheng[]=new int[107];
        int diancheng=0;
        for (int i=0;i<107;i++){
            C_diancheng[i]=(cons[i]-sum_c)/total_c;
            diancheng=diancheng+general_diancheng[i]*C_diancheng[i];
        }
        System.out.println(x+"与体能测试成绩的相关性为"+diancheng);
    }
}
