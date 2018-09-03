package com.atik_faysal.diualumni.models;

import com.atik_faysal.diualumni.main.AlumniMembers;

public class AlumniModel
{
     private String name,phone,email,gender,batch,stdId,imageName;

     public AlumniModel(String name,String id,String gender,String batch,String phone,String email,String imageName)
     {
          this.batch = batch;
          this.email = email;
          this.gender = gender;
          this.name = name;
          this.phone = phone;
          this.stdId = id;
          this.imageName = imageName;
     }

     public String getImageName() {
          return imageName;
     }

     public void setImageName(String imageName) {
          this.imageName = imageName;
     }

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

     public String getPhone() {
          return phone;
     }

     public void setPhone(String phone) {
          this.phone = phone;
     }

     public String getEmail() {
          return email;
     }

     public void setEmail(String email) {
          this.email = email;
     }

     public String getGender() {
          return gender;
     }

     public void setGender(String gender) {
          this.gender = gender;
     }

     public String getBatch() {
          return batch;
     }

     public void setBatch(String batch) {
          this.batch = batch;
     }

     public String getStdId() {
          return stdId;
     }

     public void setStdId(String stdId) {
          this.stdId = stdId;
     }
}
