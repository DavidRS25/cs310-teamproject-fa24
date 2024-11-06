/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24.dao;
import edu.jsu.mcis.cs310.tas_fa24.Punch;
import edu.jsu.mcis.cs310.tas_fa24.Badge;
import edu.jsu.mcis.cs310.tas_fa24.EventType;
import edu.jsu.mcis.cs310.tas_fa24.Department;
import edu.jsu.mcis.cs310.tas_fa24.Employee;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.time.ZoneOffset;
import java.util.Optional;


public class PunchDAO {
    private static final String CREATE_PUNCH = "INSERT INTO event (terminalid, badgeid, timestamp, eventtypeid) VALUES (?, ?, ?, ?)";
    private static final String FIND_PUNCH = "SELECT * FROM event WHERE id = ?";
    
    private final DAOFactory daoFactory;
    
    public PunchDAO(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    public Punch find (int id){
        Punch punch = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            Connection conn = daoFactory.getConnection();
            
            if (conn.isValid(0)){
                ps = conn.prepareStatement(FIND_PUNCH);
                ps.setInt(1, id);
                
                rs = ps.executeQuery();
                
                if (rs.next()){
                    int terminalId = rs.getInt("terminalid");
                    String badgeId = rs.getString("badgeid");
                    LocalDateTime originalTimestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                    int eventTypeId = rs.getInt("eventtypeid");
                    
                    EventType eventType = switch (eventTypeId){
                        case 0 -> EventType.CLOCK_OUT;
                        case 1 -> EventType.CLOCK_IN;
                        case 2 -> EventType.TIME_OUT;
                        default -> throw new IllegalArgumentException("Unkown event type ID: " + eventTypeId);
                    };
                    
                    Badge badge = daoFactory.getBadgeDAO().find(badgeId);
                    punch = new Punch(id, terminalId, badge, originalTimestamp, eventType);
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (rs != null){
                try {
                    rs.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
            
            if (ps != null){
                try {
                    ps.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
        
        return punch;
    }
    
    public int create(Punch newPunch){
        int generatedId = 0;
        
        try {
            Connection conn = daoFactory.getConnection();
            EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
            Badge badge = newPunch.getBadge();
            Employee employee = employeeDAO.find(badge);
            
            if (newPunch.getTerminalid() != 0 && employee != null){
                int departmentTerminalid = employee.getDepartment().getTerminalid();
                if (newPunch.getTerminalid() != departmentTerminalid){
                    return generatedId;
                }
            }
            
            try (PreparedStatement ps = conn.prepareStatement(CREATE_PUNCH, Statement.RETURN_GENERATED_KEYS)){
                ps.setInt(1, newPunch.getTerminalid());
                ps.setString(2, newPunch.getBadge().getId());
                ps.setTimestamp(3, Timestamp.valueOf(newPunch.getOriginaltimestamp()));
                ps.setInt(4, newPunch.getPunchtype().ordinal());
                
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0){
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()){
                        if (generatedKeys.next()){
                            generatedId = generatedKeys.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }
}
