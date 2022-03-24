package com.nwnu.kp.controller;
import com.nwnu.kp.utils.JDBCUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import java.sql.SQLException;

@Controller
public class PageRoute {
    @RequestMapping("/index")
    public String Index() throws SQLException {
        JDBCUtil.recordJournal("进入页面","getIndex");
        return "index";
    }

    @RequestMapping("/getfile")
    public String getFile() throws SQLException {
        JDBCUtil.recordJournal("进入页面","getFile");
        return "getfile";
    }

    @RequestMapping("/getshow")
    public String getShow() throws SQLException {
        JDBCUtil.recordJournal("进入页面", "getShow");
        return "getshow";
    }

    @RequestMapping("/getscot")
    public String getScot() throws SQLException {
        JDBCUtil.recordJournal("进入页面", "getScot");
        return "getscot";
    }

    @RequestMapping("/getsort")
    public String getSort() throws SQLException {
        JDBCUtil.recordJournal("进入页面", "getsort");
        return "getsort";
    }

    @RequestMapping("/getdp")
    public String getDynamic() throws SQLException {
        JDBCUtil.recordJournal("进入页面", "getdp");
        return "getdp";
    }

    @RequestMapping("/getbacktrack")
    public String getBacktrack() throws SQLException {
        JDBCUtil.recordJournal("进入页面", "getBacktrack");
        return "getbacktrack";
    }

    @RequestMapping("/getgenetic")
    public String getGenetic() throws SQLException {
        JDBCUtil.recordJournal("进入页面", "getGenetic");
        return "getgenetic";
    }

    @RequestMapping("/getjournal")
    public String getJournal() throws SQLException {
        JDBCUtil.recordJournal("进入页面", "getJournal");
        return "getjournal";
    }
}
