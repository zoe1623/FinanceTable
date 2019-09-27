package com.zoe.financetable.bean;

import java.util.List;

public class FinanceReportData {
    public List<String> name_list;//标签名字
    public List<List<String>> target_name;//table name
    public List<ListBean> list;//table content

    public static class ListBean {
        public String show_time;
        public List<List<FinanceReportContent>> data;

    }

    public static class FinanceReportContent {
        public String price;
    }

}
