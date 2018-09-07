package com.atik_faysal.diualumni.models;

public class UrlsModel
{
     private String url,uId,stdId;

     public UrlsModel(String stdId,String id,String url)
     {
          this.url = url;
          this.uId = id;
          this.stdId = stdId;
     }


     public String getUrl() {
          return url;
     }

     public void setUrl(String url) {
          this.url = url;
     }

     public String getuId() {
          return uId;
     }

     public void setuId(String uId) {
          this.uId = uId;
     }

     public String getStdId() {
          return stdId;
     }

     public void setStdId(String stdId) {
          this.stdId = stdId;
     }
}
