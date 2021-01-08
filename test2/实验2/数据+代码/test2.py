import pymysql
import matplotlib.pyplot as plt
import numpy as np
import copy

C_one=[]#存储C1的所有成绩
Con=[]#存储体能成绩
Code = [ [ 0 for j in range(9) ] for i in range(107) ]#存储C1~C9的成绩
Sno=[]#存储学生ID

con_engine = pymysql.connect(host = 'localhost' ,user = 'root', password = 'liaocan', database = 's_t', port=3306, charset = 'utf8')# 建立数据库连接
cursor = con_engine.cursor()
sql_ = "select * from data;"# SQL语句
try:
   # 执行SQL语句
   cursor.execute(sql_)
   # fetchall()获取所有记录，形成的是元组，results = cursor.fetchmany(10)获取前10条，results = cursor.fetchone()获取一条数据
   results = cursor.fetchall()
   line=-1#记录数据库的行数，line+1为目前学生数,最终的结果line=106
   for row in results:  #依次获取每一行数据
      line+=1
      C1 = row[5]  #第1列
      constistution = row[15]
      #将体能成绩转化为数组，优=4，好=3，中=2，坏=1
      if constistution=='good':
         constistution=3
      elif constistution=='general':
         constistution=2
      elif constistution=='excellent':
         constistution=4
      elif constistution=='bad':
         constistution=1
      C_one.append(C1)
      Con.append(constistution)
      Sno.append(row[0])
      for i in range(9):#读取C1~C9成绩
         Code[line][i]=row[i+5]

except:
   print ("Error: unable to fetch data")

con_engine.close()# 关闭数据库连接

#求归一化矩阵，由于C10为空，体能成绩是优良评定，所以只求了C1~C9的归一化矩阵
mean=[0,0,0,0,0,0,0,0,0 ]#存储每门成绩平均值
for i in range(line+1):#求各门成绩的和
   for j in range(9):
      mean[j]+=Code[i][j]
for i in range(9):#求平均值，且平均值去到小数点后三位
   mean[i]=round(mean[i]/(line+1),3)
std=[0,0,0,0,0,0,0,0,0]#存储每门成绩的标准差
for i in range(line+1):#求Xi-meanxi的平方
   for j in range(9):
      std[j]+=((Code[i][j]-mean[j])**2)
for i in range(9):#求标准差
   std[i]=round((std[i]/(line+1))**0.5,3)
Copy_Code=copy.deepcopy(Code)#深拷贝成绩数组
for i in range(line+1):#求归一化矩阵
   for j in range(9):
      Copy_Code[i][j]=round((Copy_Code[i][j]-mean[j])/std[j],3)
      print(Copy_Code[i][j],end="\t")
   print()           
 
#求相关矩阵
Avg_student=[0 for i in range(line+1)]#存储每个学生平均成绩
Std_student=[0 for j in range(line+1)]#存储每个学生成绩标准差
for i in range(line+1):#求每个学生平均成绩
   Avg_student.append(0)
   for j in range(9):
      Avg_student[i]+=Code[i][j]
   Avg_student[i]=round((Avg_student[i]/9),3)

for i in range(line+1):#求每个学生成绩的标准差
   Std_student.append(0)
   for j in range(9):
      Std_student[i]+=((Code[i][j]-Avg_student[i])**2)
   Std_student[i]=round((Std_student[i]/9)**0.5,3)

Array_Correlation=[ [ 0 for j in range(line+1) ] for i in range(line+1) ]#记录学生间的相关系数，得到相关矩阵,根据相关矩阵
for i in range(line+1):
   for j in range(line+1):
      for n in range(9):
         Array_Correlation[i][j]+=((Code[i][n]-Avg_student[i])*(Code[j][n]-Avg_student[j]))
      Array_Correlation[i][j]=round(((Array_Correlation[i][j]/9)/(Std_student[i]*Std_student[j])),3)
   #print(Array_Correlation[i])
                                 
#将距离每个让本最近的id存入data.txt文本中
Min_dis=[0,0,0]#存储距离样本最近三个让本的id下标
tset='\n'
my_file=open('data.txt','a')#建立一个data文件，用于储存距离
Copy_AC=copy.deepcopy(Array_Correlation)#为不改变相关矩阵数据，复制数据
for i in range(line+1):
   list1=Copy_AC[i]
   for n in range(3):#循环找到该行样本的最近距离三个样本的下标
      if list1.index(max(list1))==i:
         list1[list1.index(max(list1))]=0
      Min_dis[n]=list1.index(max(list1))
      list1[list1.index(max(list1))]=0
   for i in range(3):#根据下标找到学生id
      Min_dis[i]=Sno[Min_dis[i]]
   my_file.write(str(Min_dis))#id写入文本
   my_file.write(tset)#换行
my_file.close()
print('finsh')
                                 
# 画散点图
fig = plt.figure()# 创建画图窗口
ax1 = fig.add_subplot(1, 1, 1)# 将画图窗口分成1行1列，选择第一块区域作子图
ax1.set_title('1')# 设置标题
ax1.set_xlabel('C1')# 设置横坐标名称
ax1.set_ylabel('Constitution')# 设置纵坐标名称
ax1.scatter(C_one, Con, s=5, c='k', marker='.')# 画散点图
plt.xlim(xmax=95, xmin=65)#  调整横坐标的上下界，C1的成绩区间在65~95
plt.show()# 显示


#画直方图
df = pd.read_csv('stu.csv')
plt.rcParams['font.sans-serif']='SimHei'#正常显示中文
plt.rcParams['axes.unicode_minus']=False#正常显示负号
fig = plt.figure()
ax = fig.add_subplot(1,1,1)
ax.hist(df['C1'],bins=25)
plt.title('C1成绩')
plt.xlabel('成绩')
plt.ylabel('人数')
plt.show()
