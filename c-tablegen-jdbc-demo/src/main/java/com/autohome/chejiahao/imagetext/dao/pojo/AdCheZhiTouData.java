package com.autohome.chejiahao.imagetext.dao.pojo;

/*
 *  AdCheZhiTouData
 *
 * Notice! Automatically generated file!
 * Do not edit the pojo and dal packages,use `maven tablegen:gen`!
 * Code Generator originally by J.A.Carter
 * Modified by Xing Wanxiang 2008-2022
 * email: xwx@live.cn
 */


import java.sql.*;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.apache.commons.text.StringEscapeUtils;


import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.processing.Generated;

@Generated("com.github.yujiaao:tablegen")
@Schema(description ="-")
public class AdCheZhiTouData  implements Serializable {


    
    /**
    * Id
    */
    
    protected Integer  id;

    
    /**
    * CurrentDate 当前日期 
    */
    @NotNull
    protected Timestamp  currentDate;

    
    /**
    * AdId 广告位编号 
    */
    @NotNull
    protected Integer  adId;

    
    /**
    * ShowCount 曝光量 
    */
    @NotNull
    protected Long  showCount;

    
    /**
    * ClickCount 点击量 
    */
    @NotNull
    protected Long  clickCount;

    
    /**
    * InCome 总收入 
    */
    
    protected Double  inCome;

    
    /**
    * CreateTime
    */
    @NotNull
    protected Timestamp  createTime;

    
    /**
    * RealInCome * NotNull, but has default value :((0))

    */
    
    protected Double  realInCome;

    
    /**
    * RV
    */
    @NotNull
    protected byte[]  rV;

    
    /**
    * Reason 变动原因 
    */
    @Size(max=30)
    protected String  reason;

    
    /**
    * AdjustPercent
    */
    
    protected Double  adjustPercent;

    
    /**
    * AdjustValue 变动金额 
    */
    
    protected Double  adjustValue;




    public void setId(Integer id)
    {
      this.id = id;
    }
    public Integer getId()
    {
      return this.id;
    }

    public void setCurrentDate(Timestamp currentDate)
    {
      this.currentDate = currentDate;
    }
    public Timestamp getCurrentDate()
    {
      return this.currentDate;
    }

    public void setAdId(Integer adId)
    {
      this.adId = adId;
    }
    public Integer getAdId()
    {
      return this.adId;
    }

    public void setShowCount(Long showCount)
    {
      this.showCount = showCount;
    }
    public Long getShowCount()
    {
      return this.showCount;
    }

    public void setClickCount(Long clickCount)
    {
      this.clickCount = clickCount;
    }
    public Long getClickCount()
    {
      return this.clickCount;
    }

    public void setInCome(Double inCome)
    {
      this.inCome = inCome;
    }
    public Double getInCome()
    {
      return this.inCome;
    }

    public void setCreateTime(Timestamp createTime)
    {
      this.createTime = createTime;
    }
    public Timestamp getCreateTime()
    {
      return this.createTime;
    }

    public void setRealInCome(Double realInCome)
    {
      this.realInCome = realInCome;
    }
    public Double getRealInCome()
    {
      return this.realInCome;
    }

    public void setRV(byte[] rV)
    {
      this.rV = rV;
    }
    public byte[] getRV()
    {
      return this.rV;
    }

    public void setReason(String reason)
    {
      this.reason = reason;
    }
    public String getReason()
    {
      return this.reason;
    }

    public void setAdjustPercent(Double adjustPercent)
    {
      this.adjustPercent = adjustPercent;
    }
    public Double getAdjustPercent()
    {
      return this.adjustPercent;
    }

    public void setAdjustValue(Double adjustValue)
    {
      this.adjustValue = adjustValue;
    }
    public Double getAdjustValue()
    {
      return this.adjustValue;
    }


    
    public AdCheZhiTouData()
    {
     
            id=0;
             
            currentDate=new Timestamp(System.currentTimeMillis());
             
            adId=0;
             
            showCount=0L;
             
            clickCount=0L;
             
            inCome=0.0;
             
            createTime=new Timestamp(System.currentTimeMillis());
             
            realInCome=0.0;
             
            rV=new byte[]{0};
             
            reason="";
             
            adjustPercent=0.0;
             
            adjustValue=0.0;
            
    }

    public String toXml()
    {
        StringBuilder s= new StringBuilder();
        String ln = System.getProperty("line.separator");
        s.append("<AdCheZhiTouData ");

        s.append(" id=\"").append(this.getId()).append("\"");
        
        s.append(" currentDate=\"").append(this.getCurrentDate()).append("\"");
        
        s.append(" adId=\"").append(this.getAdId()).append("\"");
        
        s.append(" showCount=\"").append(this.getShowCount()).append("\"");
        
        s.append(" clickCount=\"").append(this.getClickCount()).append("\"");
        
        s.append(" inCome=\"").append(this.getInCome()).append("\"");
        
        s.append(" createTime=\"").append(this.getCreateTime()).append("\"");
        
        s.append(" realInCome=\"").append(this.getRealInCome()).append("\"");
        
        s.append(" rV=\"").append(this.getRV()).append("\"");
        
        s.append(" reason=\"").append(StringEscapeUtils.escapeXml11(this.getReason())).append("\"");
        s.append(" adjustPercent=\"").append(this.getAdjustPercent()).append("\"");
        
        s.append(" adjustValue=\"").append(this.getAdjustValue()).append("\"");
        s.append(" />");
        s.append(ln);
        return s.toString();
    }
}
