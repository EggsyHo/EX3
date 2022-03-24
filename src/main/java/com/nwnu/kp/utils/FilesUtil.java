package com.nwnu.kp.utils;
import org.springframework.boot.env.ConfigTreePropertySource;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FilesUtil {
    static int Weight[];
    static int Value[];
    static int m,n;
    public String WeightList="";
    static String ValueList="";

    public boolean ReadFile(String FilePath) throws FileNotFoundException {
        try {
            File Files=new File(FilePath);
            Scanner In=new Scanner(new FileReader(Files));
            m=In.nextInt();
            n=In.nextInt();
            Weight=new int[10010];
            Value=new int[10010];
            for (int i=1;i<=n;i++) {
                Weight[i] = In.nextInt();
                WeightList+=Weight[i]+" ";
                Value[i] = In.nextInt();
                ValueList+=Value[i]+" ";
            }
            return true;
        } catch (IOException e) {
            System.out.println("没有该文件");
            return false;
        }
    }

    public void WriteDB(File Files) throws SQLException {
        Connection Conn=JDBCUtil.getConnection();
        Statement Stat=Conn.createStatement();
        String Query="SELECT * FROM `knapsack` WHERE `filename`='" + Files.getName() + "'";
        ResultSet Res=Stat.executeQuery(Query);
        if (Res.next()) {
            return;
        }
        String Insert="insert into `knapsack` (`m`, `n`, `weight`, `value`, `filename`) " +
                "VALUES ('" + m + "','" + n + "','" + WeightList + "','" + ValueList + "','" + Files.getName() + "')";
        Stat.execute(Insert);
        JDBCUtil.release(Stat,Conn);
        JDBCUtil.recordJournal("存储文件","插入操作");
    }
}
