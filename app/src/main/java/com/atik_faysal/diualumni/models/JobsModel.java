package com.atik_faysal.diualumni.models;


public class JobsModel
{
     private String userName,jobTitle,jobDes,education,deadLine,date,company,stdId,jobId;
     private String requirement,type,category,salary,phone,email,experience;
     private boolean flag;

     public JobsModel(String name,String stdId,
                      String id,String title,String des,String education,String exp,
                      String req,String type,String category,String salary,
                      String com,String phone,String email,String date,String deadLine,boolean flag)
     {
          this.userName = name;
          this.jobId = id;
          this.jobTitle = title;
          this.deadLine = deadLine;
          this.jobDes = des;
          this.company = com;
          this.date = date;
          this.stdId = stdId;
          this.education = education;
          this.requirement = req;
          this.type = type;
          this.phone = phone;
          this.email = email;
          this.category = category;
          this.salary = salary;
          this.experience = exp;
          this.flag = flag;
     }

     public boolean isFlag() {
          return flag;
     }

     public String getExperience() {
          return experience;
     }

     public String getRequirement() {
          return requirement;
     }

     public String getType() {
          return type;
     }

     public String getCategory() {
          return category;
     }

     public String getSalary() {
          return salary;
     }

     public String getPhone() {
          return phone;
     }

     public String getEmail() {
          return email;
     }

     public String getEducation() {
          return education;
     }

     public String getStdId() {
          return stdId;
     }

     public String getUserName() {
          return userName;
     }

     public String getJobId() {
          return jobId;
     }

     public String getJobTitle() {
          return jobTitle;
     }

     public String getJobDes() {
          return jobDes;
     }

     public String getDeadLine() {
          return deadLine;
     }

     public String getCompany() {
          return company;
     }

     public String getDate() {
          return date;
     }
}
