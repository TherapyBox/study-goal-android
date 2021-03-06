package com.studygoal.jisc.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "CourseAttendant")
public class CourseAttendant extends Model {
    @Column(name = "student_id")
    public String student_id;
    @Column(name = "uid")
    public String id;
    @Column(name = "institution_id")
    public String institution_id;
    @Column(name = "dob")
    public String dob;
    @Column(name = "race_code")
    public String race_code;
    @Column(name = "sex_code")
    public String sex_code;
    @Column(name = "age")
    public String age;
    @Column(name = "learning_difficulty_code")
    public String learning_difficulty_code;
    @Column(name = "disability_code")
    public String disability_code;
    @Column(name = "country_code")
    public String country_code;
    @Column(name = "accommodation_code")
    public String accommodation_code;
    @Column(name = "parents_qualification")
    public String parents_qualification;
    @Column(name = "overseas_code")
    public String overseas_code;
    @Column(name = "first_name")
    public String first_name;
    @Column(name = "last_name")
    public String last_name;
    @Column(name = "address_line_1")
    public String address_line_1;
    @Column(name = "address_line_2")
    public String address_line_2;
    @Column(name = "address_line_3")
    public String address_line_3;
    @Column(name = "address_line_4")
    public String address_line_4;
    @Column(name = "postal_code")
    public String postal_code;
    @Column(name = "email")
    public String email;
    @Column(name = "home_phone")
    public String home_phone;
    @Column(name = "mobile_phone")
    public String mobile_phone;
    @Column(name = "photo")
    public String photo;

    public CourseAttendant(){
        super();

        student_id = "";
        id="";
        institution_id="";
        dob="";
        race_code="";
        sex_code="";
        age="";
        learning_difficulty_code="";
        accommodation_code="";
        parents_qualification="";
        disability_code="";
        country_code="";
        overseas_code="";
        first_name="";
        last_name="";
        address_line_1="";
        address_line_2="";
        address_line_3="";
        address_line_4="";
        postal_code="";
        email="";
        home_phone="";
        mobile_phone="";
        photo="";
    }
}
