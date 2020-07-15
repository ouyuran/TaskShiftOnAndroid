package com.citrix.taskshiftonandroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Item {
    String taskid;
    String storySummary;
    int jiraLogo;
    int storyType;

    Item(String taskid, String storySummary, int jiraLogo, int storyType) {
        this.taskid = taskid;
        this.storySummary = storySummary;
        this.jiraLogo = jiraLogo;
        this.storyType = storyType;
    }

    public String toString() {
        return this.taskid + "//" + storySummary + "//" + String.valueOf(jiraLogo) + "//" + String.valueOf(storyType) + "//";
    }

    public static Item toItem(String s) {
        String id = s.substring(0, s.indexOf("//"));
        s = s.substring(s.indexOf("//") + 2);
        String summary = s.substring(0, s.indexOf("//"));
        s = s.substring(s.indexOf("//") + 2);
        int Logo = Integer.parseInt(s.substring(0, s.indexOf("//")));
        s = s.substring(s.indexOf("//") + 2);
        int type = Integer.parseInt(s.substring(0, s.indexOf("//")));
        return new Item(id, summary, Logo, type);
    }
}




