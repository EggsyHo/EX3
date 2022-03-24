package com.nwnu.kp.controller;
import com.alibaba.druid.support.spring.stat.annotation.Stat;
import com.nwnu.kp.utils.FilesUtil;
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
        System.out.println(JObj.toString());
        return JObj.toString();
    }
}


