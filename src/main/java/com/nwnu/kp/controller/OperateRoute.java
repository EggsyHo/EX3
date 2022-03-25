package com.nwnu.kp.controller;
import com.alibaba.druid.support.spring.stat.annotation.Stat;
import com.nwnu.kp.utils.AlgorithmUtil;
import com.nwnu.kp.utils.FilesUtil;
import com.nwnu.kp.utils.GeneticUtil;
import com.nwnu.kp.utils.JDBCUtil;
import org.apache.ibatis.jdbc.SQL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@RestController
public class OperateRoute {
    @ResponseBody
    @RequestMapping("/fileupload")
    public JSONObject Files(@RequestParam("file") MultipartFile UploadFile) throws IOException,SQLException {
        JSONObject JObj=new JSONObject();
        String FoldPath="D:/data/";
        File Fold=new File(FoldPath);
        if (!Fold.exists()) {
            Fold.mkdirs();
        }
        String FilePath=FoldPath+UploadFile.getOriginalFilename().trim();
        File DataFile=new File(FilePath);
        if (DataFile.exists()) {
            DataFile.delete();
        }
        try {
            UploadFile.transferTo(DataFile);
        } catch (IOException e) {
            System.out.println("Failed to upload");
        }
        FilesUtil ReadFile=new FilesUtil();
        boolean Flag=ReadFile.ReadFile(FilePath);
        ReadFile.WriteDB(DataFile);
        if (Flag) {
            JObj.put("data","0");
        } else {
            JObj.put("data","-1");
        }
        return JObj;
    }

    @RequestMapping("/selectFile")
    public String SelectFile() throws SQLException {
        JSONObject JObj=new JSONObject();
        Connection Conn= JDBCUtil.getConnection();
        Statement Stat=Conn.createStatement();
        String Query="SELECT DISTINCT `filename` FROM `knapsack`";
        ResultSet Res=Stat.executeQuery(Query);
        if (Res.next()) {
            JObj.put("code",0);
            JSONArray JArray=new JSONArray();
            do {
                JArray.put(new JSONObject().put("filename", Res.getString("filename")));
            } while (Res.next());
            JObj.put("data",JArray);
            JObj.put("count",JArray.length());
        } else {
            JObj.put("code",-1);
        }
        JDBCUtil.release(Res,Stat,Conn);
        JDBCUtil.recordJournal("选择文件","查询操作");
        return JObj.toString();
    }

    @RequestMapping("/selectGroup/{FileName}")
    public String SelectGroup(@PathVariable String FileName) throws SQLException {
        JSONObject JObj=new JSONObject();
        Connection Conn= JDBCUtil.getConnection();
        Statement Stat=Conn.createStatement();
        String Query="SELECT * FROM `knapsack` WHERE `filename`='"+FileName+"'";
        ResultSet Res=Stat.executeQuery(Query);
        JSONArray JArray=new JSONArray();
        if (Res.next()) {
            JSONObject Group=new JSONObject();
            Group.put("m",Res.getString("m"));
            Group.put("n",Res.getString("n"));
            Group.put("weight",Res.getString("weight"));
            Group.put("value",Res.getString("value"));
            JArray.put(Group);
        }
        JObj.put("data",JArray);
        JObj.put("code",0);
        JObj.put("msg","");
        JObj.put("count",JArray.length());
        JDBCUtil.release(Res,Stat,Conn);
        JDBCUtil.recordJournal("选择文件","查询操作");
        return JObj.toString();
    }

    @RequestMapping("/scotPicture/{FileName}")
    public String SelectAndDraw(@PathVariable String FileName) throws SQLException {
        JSONArray JObj=new JSONArray();
        Connection Conn=JDBCUtil.getConnection();
        Statement Stat=Conn.createStatement();
        String Query="SELECT * FROM `knapsack` WHERE `filename`='"+FileName+"'";
        ResultSet Res=Stat.executeQuery(Query);
        if (Res.next()) {
            String[] WeightList=Res.getString("weight").split(" ");
            String[] ValueList=Res.getString("value").split(" ");
            for (int i=0;i<WeightList.length;i++) {
                JSONObject Group = new JSONObject();
                Group.put("weight", WeightList[i]);
                Group.put("value", ValueList[i]);
                JObj.put(Group);
            }
        }
        JDBCUtil.release(Res,Stat,Conn);
        JDBCUtil.recordJournal("选择以绘图","查询操作");
        return JObj.toString();
    }

    @RequestMapping("/selectSort/{FileName}")
    public String SelectSort(@PathVariable String FileName) throws SQLException {
        JSONObject JObj=new JSONObject();
        Connection Conn=JDBCUtil.getConnection();
        Statement Stat=Conn.createStatement();
        String Query="SELECT * FROM `knapsack` WHERE `filename`='"+FileName+"'";
        ResultSet Res=Stat.executeQuery(Query);
        JSONArray JArray=new JSONArray();
        if (Res.next()) {
            String[] WeightList=Res.getString("weight").split(" ");
            String[] ValueList=Res.getString("value").split(" ");
            for (int i=0;i<WeightList.length;i++) {
                JSONObject Group = new JSONObject();
                double Rate=Double.parseDouble(ValueList[i])/Double.parseDouble(WeightList[i]);
                Group.put("weight", WeightList[i]);
                Group.put("value", ValueList[i]);
                Group.put("rate",Rate);
                JArray.put(Group);
            }
            JObj.put("count",WeightList.length);
        }
        JObj.put("data",JArray);
        JObj.put("code",0);
        JObj.put("msg","");
        JDBCUtil.release(Res,Stat,Conn);
        JDBCUtil.recordJournal("选择以排序","查询操作");
        return JObj.toString();
    }

    @RequestMapping("/dynamic/{FileName}")
    public String DP(@PathVariable String FileName) throws SQLException {
        JSONObject JObj=new JSONObject();
        Connection Conn=JDBCUtil.getConnection();
        Statement Stat=Conn.createStatement();
        String Query="SELECT * FROM `knapsack` WHERE `filename`='"+FileName+"'";
        ResultSet Res=Stat.executeQuery(Query);
        AlgorithmUtil AUtil=new AlgorithmUtil();
        JSONArray JArray=new JSONArray();
        if (Res.next()) {
            JSONObject Group=new JSONObject();
            String[] WeightList=Res.getString("weight").split(" ");
            String[] ValueList=Res.getString("value").split(" ");
            int m=Integer.valueOf(Res.getString("m"));
            int n=Integer.valueOf(Res.getString("n"));
            AUtil.SaveData(m,n,WeightList,ValueList);
            long StartTime=System.nanoTime();
            int Ans=AUtil.DP();
            long EndTime=System.nanoTime();
            double RunTime=(EndTime-StartTime)/1000000000.0;
            String Path=AUtil.getSPath();
            Group.put("time",RunTime);
            Group.put("filename",FileName);
            Group.put("maxvalue",Ans);
            Group.put("anspath",Path);
            JArray.put(Group);
        }
        JObj.put("data",JArray);
        JObj.put("code",0);
        JObj.put("msg","");
        JDBCUtil.release(Res,Stat,Conn);
        JDBCUtil.recordJournal("动态规划","查询操作");
        return JObj.toString();
    }

    @RequestMapping("/dfs/{FileName}")
    public String DFS(@PathVariable String FileName) throws SQLException {
        JSONObject JObj=new JSONObject();
        Connection Conn=JDBCUtil.getConnection();
        Statement Stat=Conn.createStatement();
        String Query="SELECT * FROM `knapsack` WHERE `filename`='"+FileName+"'";
        ResultSet Res=Stat.executeQuery(Query);
        AlgorithmUtil AUtil=new AlgorithmUtil();
        JSONArray JArray=new JSONArray();
        if (Res.next()) {
            JSONObject Group=new JSONObject();
            String[] WeightList=Res.getString("weight").split(" ");
            String[] ValueList=Res.getString("value").split(" ");
            int m=Integer.valueOf(Res.getString("m"));
            int n=Integer.valueOf(Res.getString("n"));
            AUtil.SaveData(m,n,WeightList,ValueList);
            long StartTime=System.nanoTime();
            int Ans=AUtil.BackTrackAns();
            long EndTime=System.nanoTime();
            double RunTime=(EndTime-StartTime)/1000000000.0;
            String Path=AUtil.getSPath();
            Group.put("time",RunTime);
            Group.put("filename",FileName);
            Group.put("maxvalue",Ans);
            Group.put("anspath",Path);
            JArray.put(Group);
        }
        JObj.put("data",JArray);
        JObj.put("code",0);
        JObj.put("msg","");
        JDBCUtil.release(Res,Stat,Conn);
        JDBCUtil.recordJournal("回溯","查询操作");
        return JObj.toString();
    }

    @RequestMapping("/greedy/{FileName}")
    public String Greedy(@PathVariable String FileName) throws SQLException {
        JSONObject JObj=new JSONObject();
        Connection Conn=JDBCUtil.getConnection();
        Statement Stat=Conn.createStatement();
        String Query="SELECT * FROM `knapsack` WHERE `filename`='"+FileName+"'";
        ResultSet Res=Stat.executeQuery(Query);
        AlgorithmUtil AUtil=new AlgorithmUtil();
        JSONArray JArray=new JSONArray();
        if (Res.next()) {
            JSONObject Group=new JSONObject();
            String[] WeightList=Res.getString("weight").split(" ");
            String[] ValueList=Res.getString("value").split(" ");
            int m=Integer.valueOf(Res.getString("m"));
            int n=Integer.valueOf(Res.getString("n"));
            AUtil.SaveData(m,n,WeightList,ValueList);
            long StartTime=System.nanoTime();
            int Ans=AUtil.Greedy();
            long EndTime=System.nanoTime();
            double RunTime=(EndTime-StartTime)/1000000000.0;
            String Path=AUtil.getSPath();
            Group.put("time",RunTime);
            Group.put("filename",FileName);
            Group.put("maxvalue",Ans);
            Group.put("anspath",Path);
            JArray.put(Group);
        }
        JObj.put("data",JArray);
        JObj.put("code",0);
        JObj.put("msg","");
        JDBCUtil.release(Res,Stat,Conn);
        JDBCUtil.recordJournal("回溯","查询操作");
        return JObj.toString();
    }

    @RequestMapping("/genetic/{FileName}")
    public String Gene(@PathVariable String FileName) throws SQLException {
        JSONObject JObj=new JSONObject();
        Connection Conn=JDBCUtil.getConnection();
        Statement Stat=Conn.createStatement();
        String Query="SELECT * FROM `knapsack` WHERE `filename`='"+FileName+"'";
        ResultSet Res=Stat.executeQuery(Query);
        JSONArray JArray=new JSONArray();
        if (Res.next()) {
            JSONObject Group=new JSONObject();
            String[] WeightList=Res.getString("weight").split(" ");
            String[] ValueList=Res.getString("value").split(" ");
            int m=Integer.valueOf(Res.getString("m"));
            int n=Integer.valueOf(Res.getString("n"));
            GeneticUtil AUtil=new GeneticUtil(100, Integer.valueOf(Res.getString("n")), 3000, 0.8f, 0.9f);
            AUtil.SaveData(m,n,WeightList,ValueList);
            long StartTime=System.nanoTime();
            int Ans=AUtil.Genetic();
            long EndTime=System.nanoTime();
            double RunTime=(EndTime-StartTime)/1000000000.0;
            String Path=AUtil.getSPath();
            Group.put("time",RunTime);
            Group.put("filename",FileName);
            Group.put("maxvalue",Ans);
            Group.put("anspath",Path);
            JArray.put(Group);
        }
        JObj.put("data",JArray);
        JObj.put("code",0);
        JObj.put("msg","");
        JDBCUtil.release(Res,Stat,Conn);
        JDBCUtil.recordJournal("基因","查询操作");
        return JObj.toString();
    }

    @RequestMapping("/journalShow")
    public String journalShow() throws SQLException {
        JSONObject JObj = new JSONObject();
        Connection Conn = JDBCUtil.getConnection();
        Statement Stat = Conn.createStatement();
        String Query = "SELECT * FROM `journal`";
        ResultSet Res = Stat.executeQuery(Query);
        JSONArray JArray = new JSONArray();
        while (Res.next()) {
            JSONObject Group = new JSONObject();
            Group.put("operation", Res.getString("operation"));
            Group.put("command", Res.getString("command"));
            Group.put("time", Res.getString("time"));
            JArray.put(Group);
        }
        JObj.put("data", JArray);
        JObj.put("count", JArray.length());
        JObj.put("code", 0);
        JObj.put("msg", "");
        JDBCUtil.release(Res, Stat, Conn);
        JDBCUtil.recordJournal("打印日志数据", "查询操作");
        return JObj.toString();
    }
}


