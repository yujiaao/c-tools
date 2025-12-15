package com.bixuebihui.jsp;


//此页存放查询结果的分页信息

public class PageInformation {
    private int totalitem = 0;            //符合条件的所有记录数
    private int pagesize = 0;                //每页显示的记录数
    private int pagecount = 0;            //显示所有记录需要的全部页数
    private int cp = 0;
    private int prePage = 0;                //前页
    private int currentPage = 0;            //当前页页码
    private int nextPage = 0;                //下一页页码


    public PageInformation() {
        //default constructor
    }

    /**
     * 页面信息对象 2006-6-24
     *
     * @param totalitem   所有符合条件的记录总数
     * @param pagesize    每页显示记录的数目
     * @param currentpage 当前的页码
     */
    public PageInformation(int totalitem, int pagesize, int currentpage) {
        this.totalitem = totalitem;
        this.pagesize = pagesize;
        this.cp = currentpage;
        this.currentPage = currentpage;
        calculate();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void calculatePage() {
        this.cp = this.currentPage;
        calculate();
    }

    private void calculate() {

        if (this.totalitem % this.pagesize > 0)                            //确定总共的分页数目
        {
            this.pagecount = totalitem / pagesize + 1;                //取大不取整
        } else {
            this.pagecount = totalitem / pagesize;
        }

        if (this.cp > this.pagecount)                    //确保当前页不超过页面总数的上届
        {
            this.cp = this.pagecount;
        } else {
            if (this.cp <= 0) {
                this.cp = 1;
            }
        }

        if (this.cp > 1) {
            this.prePage = this.cp - 1;
        }

        if (this.cp < this.pagecount) {
            this.nextPage = this.cp + 1;
        } else {
            this.nextPage = this.cp;
        }
    }


    /**
     * 返回数据库查询操作的每页记录的上、下限的rownum值
     * !!此法需要在已经明确赋值后进行调用
     *
     * @return 以数组的方式返回rownum的上、下限 rst[0]-上限 rst[1]-下限
     */
    public int[] getHeadAndFoot() {
        int[] rst = {0, 0};
        if (this.totalitem > 0) {
            rst[0] = this.pagesize * this.prePage;            //ROWNUM head
            rst[1] = rst[0] + this.pagesize;                //ROWNUM foot
        }
        return rst;
    }


    /**
     * 返回Web页面的翻页角标 example   |<< |< 1 2 3 4 5 6 7 8 9 10 ..>| >>|
     *
     * @param pageswidth  显示的翻页角标的数目 - 在页脚显示可直接点选的页面数量(example为10)
     * @param relativeurl 服务器的相对url及相关参数
     * @return HTML代码
     */
    public String getPageFooter(String relativeurl, int pageswidth) {
        int curpagelocation;                //当前页在页面翻页脚标的位置
        int thepagewidth = pageswidth;        //当前要显示的可见页数
        int headpage = 0, skipnumber;

        StringBuffer sbuf;
        String preleap, nextleap, pre_btn, next_btn;            //|<<, >>|, <, >
        String baseurl = UrlUtil.addOrReplaceParam(relativeurl, "total", this.totalitem + "");
        baseurl = UrlUtil.addOrReplaceParam(baseurl, "pagesize", this.pagesize + "");        //基本url信息

        if (this.totalitem > 0) {
            headpage = ((this.currentPage - 1) / pageswidth) * pageswidth;

            if (this.currentPage > pageswidth) {
                curpagelocation = this.currentPage - headpage;        //通过余数求得当前页面在翻页脚标的位置
            } else {
                curpagelocation = this.currentPage;
            }


            if (curpagelocation == pageswidth)        //说明是处于最后一页位置 10
            {
                skipnumber = pageswidth;
            } else                                    //说明该页处于第一的位置1 或该页处于中间位置 2,3,...,9
            {
                skipnumber = curpagelocation;
            }

            sbuf = new StringBuffer();

            //=========================Deal PageLeap |<< & >>|======================
            if (headpage == 0)            //当前页处于第一段中 如pageswidth=20  currentPage为1-20
            {
                preleap = "<A href='#' class=''>｜&lt;&lt;</A>&nbsp;";
                if (this.pagecount <= pageswidth)                //页码总数不超过显示宽度
                {
                    nextleap = "&nbsp;<A href='#' class=''>&gt;&gt;｜</a>";
                } else {
                    nextleap = "&nbsp;<A href='" + UrlUtil.addOrReplaceParam(baseurl, "cp", (headpage + pageswidth + 1) + "") + "' class=''>&gt;&gt;｜</A>";
                }
            } else if ((headpage + pageswidth) >= this.pagecount)            //当前页处于最后段中 如pageswidth=20 总共有36页  currentPage为21-36
            {
                preleap = "<A href='" + UrlUtil.addOrReplaceParam(baseurl, "cp", headpage + "") + "' class=''>｜&lt;&lt;</A>&nbsp;";        //返回上一段的最后一页
                nextleap = "&nbsp;<A href='#' class=''>&gt;&gt;｜</A>";
            } else                                    //属于中间阶段的分页块
            {
                preleap = "<A href='" + UrlUtil.addOrReplaceParam(baseurl, "cp", headpage + "") + "' class=''>｜&lt;&lt;</A>&nbsp;";
                nextleap = "&nbsp;<A href='" + UrlUtil.addOrReplaceParam(baseurl, "cp", (headpage + pageswidth + 1) + "") + "' class=''>&gt;&gt;｜</A>";
            }

            //*************************Deal PrePage&NextPage |< & >|*******************************
            if (this.currentPage > 1)            //当前页不是所有页中的第一页
            {
                //pre_btn="<A href='"+baseurl+(this.currentPage-1)+"' class=''>|&lt;</A>";
                pre_btn = "<A href='" + UrlUtil.addOrReplaceParam(baseurl, "cp", this.prePage + "") + "' class=''>｜&lt;</A>";
            } else {
                pre_btn = "<A href='#' class=''>｜&gt;</A>";
            }

            if (this.currentPage < this.pagecount)                //当前页不是所有页中的最后一页
            {
                next_btn = "<A href='" + UrlUtil.addOrReplaceParam(baseurl, "cp", this.nextPage + "") + "' class=''>&gt;｜</A>";
            } else {
                next_btn = "<A href='#' class=''>&gt;｜</A>";
            }

            //----------获取循环长度----------
            if ((headpage + pageswidth) >= this.pagecount) {
                thepagewidth = this.pagecount - headpage;                        //获得本段的所有显示的页码数
            }

            sbuf.append(preleap + pre_btn + "&nbsp;&nbsp;");

            for (int i = 1; i <= thepagewidth; i++) {
                if (i == skipnumber) {
                    sbuf.append("<B>" + (headpage + i) + "</B>&nbsp;");
                } else                        //此处不再写连接代码，避免重复点击
                {
                    sbuf.append("<A href='" + UrlUtil.addOrReplaceParam(baseurl, "cp", (headpage + i) + "") + "' class=''>" + (headpage + i) + "</A>&nbsp;");
                }
            }
            sbuf.append("&nbsp;" + next_btn + nextleap + "&nbsp;&nbsp;" + this.currentPage + "/" + this.pagecount);


            return sbuf.toString();
        } else {
            return "";
        }
    }

    /**
     * 生成JavaScript脚本的翻页代码
     *
     * @param classname  样式名称
     * @param pageswidth 当前要显示的可见页数
     * @return HTML代码
     */
    public String getJsPageFooter(String classname, int pageswidth) {
        int curpagelocation;                //当前页在页面翻页脚标的位置
        int thepagewidth = pageswidth;        //当前要显示的可见页数
        int headpage = 0, skipnumber;

        StringBuffer sbuf;
        String preleap, nextleap, pre_btn, next_btn;            //|<<, >>|, <, >
        String baseurl = "total=" + this.totalitem + "&pagesize=" + this.pagesize + "&cp=";        //基本url信息

        if (this.totalitem > 0) {
            headpage = ((this.currentPage - 1) / pageswidth) * pageswidth;

            if (this.currentPage > pageswidth) {
                curpagelocation = this.currentPage - headpage;        //通过余数求得当前页面在翻页脚标的位置
            } else {
                curpagelocation = this.currentPage;
            }

            if (curpagelocation == pageswidth)        //说明是处于最后一页位置 10
            {
                skipnumber = pageswidth;
            } else                                    //说明该页处于第一的位置1 或该页处于中间位置 2,3,...,9
            {
                skipnumber = curpagelocation;
            }

            sbuf = new StringBuffer();

            //=========================Deal PageLeap |<< & >>|======================
            if (headpage == 0)            //当前页处于第一段中 如pageswidth=20  currentPage为1-20
            {
                preleap = "<A href='#' class=''>|&lt;&lt;</A>&nbsp;";
                if (this.pagecount <= pageswidth)                //页码总数不超过显示宽度
                {
                    nextleap = "&nbsp;<A href='#' class=''>&gt;&gt;|</a>";
                } else {
                    nextleap = "&nbsp;<A onClick=\"gotoPage('" + baseurl + (headpage + pageswidth + 1) + "');\" class='' style=\"cursor: hand\">&gt;&gt;|</A>";
                }
            } else if ((headpage + pageswidth) >= this.pagecount)            //当前页处于最后段中 如pageswidth=20 总共有36页  currentPage为21-36
            {
                preleap = "<A onClick=\"gotoPage('" + baseurl + headpage + "');\" class='' style=\"cursor: hand\">|&lt;&lt;</A>&nbsp;";        //返回上一段的最后一页
                nextleap = "&nbsp;<A href='#' class=''>&gt;&gt;|</A>";
            } else                                    //属于中间阶段的分页块
            {
                preleap = "<A onClick=\"gotoPage('" + baseurl + headpage + "');\"' class='' style=\"cursor: hand\">|&lt;&lt;</A>&nbsp;";
                nextleap = "&nbsp;<A onClick=\"gotoPage('" + baseurl + (headpage + pageswidth + 1) + "');\" class='' style=\"cursor: hand\">&gt;&gt;|</A>";
            }

            //*************************Deal PrePage&NextPage |< & >|*******************************
            if (this.currentPage > 1)            //当前页不是所有页中的第一页
            {
                pre_btn = "<A onClick=\"gotoPage('" + baseurl + this.prePage + "');\" class='' style=\"cursor: hand\">|&lt;</A>";
            } else {
                pre_btn = "<A href='#' class=''>|&lt;</A>";
            }

            if (this.currentPage < this.pagecount)                //当前页不是所有页中的最后一页
            {
                next_btn = "<A onClick=\"gotoPage('" + baseurl + this.nextPage + "');\" class='' style=\"cursor: hand\">&gt;|</A>";
            } else {
                next_btn = "<A href='#' class=''>&gt;|</A>";
            }

            //----------获取循环长度----------
            if ((headpage + pageswidth) >= this.pagecount) {
                thepagewidth = this.pagecount - headpage;                        //获得本段的所有显示的页码数
            }

            sbuf.append(preleap + pre_btn + "&nbsp;&nbsp;");

            for (int i = 1; i <= thepagewidth; i++) {
                if (i == skipnumber) {
                    sbuf.append("<B>" + (headpage + i) + "</B>&nbsp;");
                } else                        //此处不再写连接代码，避免重复点击
                {
                    sbuf.append("<A onClick=\"gotoPage('" + baseurl + (headpage + i) + "');\" class='' style=\"cursor: hand\">" + (headpage + i) + "</A>&nbsp;");
                }
            }
            sbuf.append("&nbsp;" + next_btn + nextleap + "&nbsp;&nbsp;" + this.currentPage + "/" + this.pagecount);


            return sbuf.toString();
        } else {
            return "";
        }
    }


    //------------------------------对象操作基本Get Set操作-----------------------------------

    public int getCurrentPage() {
        return currentPage;
    }


    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }


    public int getNextPage() {
        return nextPage;
    }


    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }


    public int getPagecount() {
        return pagecount;
    }


    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }


    public int getPagesize() {
        return pagesize;
    }


    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }


    public int getPrePage() {
        return prePage;
    }


    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }


    public int getTotalitem() {
        return totalitem;
    }


    public void setTotalitem(int totalitem) {
        this.totalitem = totalitem;
        if (totalitem > 0) {
            this.cp = this.currentPage;
            calculate();
        }
    }


}
