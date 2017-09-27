package com.studygoal.jisc.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "ToDoTasks")
public class ToDoTasks extends Model {

    public ToDoTasks() {
        super();
    }

    @Column(name = "task_id")
    public String taskId;

    @Column(name = "student_id")
    public String studentId;

    @Column(name = "module")
    public String module;

    @Column(name = "description")
    public String description;

    @Column(name = "reason")
    public String reason;

    @Column(name = "time_required")
    public String timeRequired;

    @Column(name = "end_date")
    public String endDate;

    @Column(name = "status")
    public String status;

    @Column(name = "from_tutor")
    public String fromTutor;

    @Column(name = "is_accepted")
    public String isAccepted;

    @Column(name = "reason_for_ignoring")
    public String reasonForIgnoring;

    @Column(name = "created")
    public String created;

    @Column(name = "modified")
    public String modified;
}
