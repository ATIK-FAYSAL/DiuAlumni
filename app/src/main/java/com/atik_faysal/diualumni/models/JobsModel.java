package com.atik_faysal.diualumni.models;


public class JobsModel
{
    private String userName,jobTitle,jobDes,mType,education,deadLine,date,company,stdId,jobId;
    private String requirement,type,category,salary,phone,email,experience,city,vacancy,comUrl,comAddress;
    private boolean flag,appliedJob;

    public JobsModel(String name,String stdId,String mtype,
                     String id,String title,String des,String education,String exp,String city,
                     String req,String type,String category,String salary,String vacancy,
                     String com,String comUrl,String comAddress,String phone,String email,String date,String deadLine,boolean flag,boolean temp)
    {
        this.userName = name;
        this.jobId = id;
        this.mType = mtype;
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
        this.vacancy = vacancy;
        this.comAddress = comAddress;
        this.comUrl = comUrl;
        this.experience = exp;
        this.flag = flag;
        this.city = city;
        this.appliedJob = temp;
    }

    public boolean isAppliedJob() {
        return appliedJob;
    }

    public void setAppliedJob(boolean appliedJob) {
        this.appliedJob = appliedJob;
    }

    public String getVacancy() {
        return vacancy;
    }

    public void setVacancy(String vacancy) {
        this.vacancy = vacancy;
    }

    public String getComUrl() {
        return comUrl;
    }

    public void setComUrl(String comUrl) {
        this.comUrl = comUrl;
    }

    public String getComAddress() {
        return comAddress;
    }

    public void setComAddress(String comAddress) {
        this.comAddress = comAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public void setFlag(boolean flag) {
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
