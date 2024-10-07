/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

import java.time.LocalDateTime;

/**
 *
 * @author devi
 */
public class Employee {
    private int id;
    private String firstname, middlename, lastname;
    private LocalDateTime active;
    private Badge badge;
    private Department department;
    private Shift shift;
    private EmployeeType employeetype;
    
    // Constructor
    public Employee(int id, String firstname, String middlename, String lastname,
            LocalDateTime active, Badge badge, Department department, Shift shift,
            EmployeeType employeetype){
        this.id = id;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.active = active;
        this.badge = badge;
        this.department = department;
        this.shift = shift;
        this.employeetype = employeetype;
    }
    
    // Getters
    public int getID(){
        return id;
    }
    public String getFirstName(){
        return firstname;
    }
    public String getMiddleName(){
        return middlename;
    }
    public String getLastName(){
        return lastname;
    }
    public LocalDateTime getActive(){
        return active;
    }
    public Badge getBadge(){
        return badge;
    }
    public Department getDepartment(){
        return department;
    }
    public Shift getShift(){
        return shift;
    }
    
    @Override
    public String toString(){
        return String.format("#%d (%s, %s, %s): %s [%s], %s %s",
                id, lastname, firstname, middlename,
                badge.getDescription(), badge.getId(), employeetype.toString(),
                active.toString());
    }
    
}
