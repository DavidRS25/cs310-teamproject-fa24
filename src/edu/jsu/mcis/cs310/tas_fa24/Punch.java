/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.time.Duration;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;

public class Punch {
    
    private Integer id;
    private int terminalId;
    private Badge badge;
    private LocalDateTime originalTimestamp;
    private LocalDateTime adjustedTimestamp;
    private EventType punchType;
    public static PunchAdjustmentType adjustmentType;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy HH:mm:ss");
    
    //constructor for new punches
    public Punch(int terminalId, Badge badge, EventType punchType){
        this.terminalId = terminalId;
        this.badge = badge;
        this.punchType = punchType;
        this.originalTimestamp = LocalDateTime.now();
        this.adjustedTimestamp = null;
        this.adjustmentType = null;
        //this.id = null;
    }
    
    public Punch(int id, int terminalId, Badge badge, LocalDateTime originalTimestamp, EventType punchType){
        this.id = id;
        this.terminalId = terminalId;
        this.badge = badge;
        this.originalTimestamp = originalTimestamp;
        this.punchType = punchType;
        this.adjustedTimestamp = null;
        this.adjustmentType = null;
    }
    
    
    
    public Integer getId(){
        return id;
    }
    
    public int getTerminalid(){
        return terminalId;
    }
    
    public Badge getBadge(){
        return badge;
    }
    
    public LocalDateTime getOriginaltimestamp(){
        return originalTimestamp;
    }
    
    public LocalDateTime getAdjustedTimestamp(){
        return adjustedTimestamp;
    }
    
    public EventType getPunchtype(){
        return punchType;
    }
    
    public void setAdjustedTimestamp (LocalDateTime adjustedTimestamp){
        this.adjustedTimestamp = adjustedTimestamp;
    }
    
    public void adjust (Shift shift){
        LocalDateTime punchTime = this.originalTimestamp;
        LocalTime shiftStart = shift.getStarted();
        LocalTime shiftStop = shift.getStopTime();
        LocalTime lunchStart = shift.getLunchStart();
        LocalTime lunchStop = shift.getLunchStop();
        
        int roundInterval = shift.getRoundedInterval();
        int gracePeriod = shift.getGracePeriod();
        int dockPenalty = shift.getDockPenalty();

        // Determine if punch is a clock-in or clock-out type
        if (punchType == punchType.CLOCK_IN) {
            if (punchTime.toLocalTime().isBefore(shiftStart.minusMinutes(gracePeriod))) {
                // Early clock-in, rounded to shift start time
                adjustedTimestamp = punchTime.with(shiftStart);
                adjustmentType = "Shift Start";
            } else if (punchTime.toLocalTime().isBefore(shiftStart.plusMinutes(gracePeriod))) {
                // Within grace period, round to shift start
                adjustedTimestamp = punchTime.with(shiftStart);
                adjustmentType = "Shift Start";
            } else if (punchTime.toLocalTime().isBefore(shiftStart.plusMinutes(dockPenalty))) {
                // Within dock penalty, apply dock adjustment
                adjustedTimestamp = punchTime.with(shiftStart.plusMinutes(dockPenalty));
                adjustmentType = "Shift Dock";
            } else if (punchTime.toLocalTime().isBefore(lunchStart)) {
                // Round clock-in based on interval before lunch
                adjustedTimestamp = roundToInterval(punchTime, roundInterval);
                adjustmentType = "Interval Round";
            } else if (punchTime.toLocalTime().equals(lunchStart)) {
                adjustedTimestamp = punchTime.with(lunchStart);
                adjustmentType = "Lunch Start";
            }
        } else if (punchType == PunchType.CLOCK_OUT) {
            if (punchTime.toLocalTime().equals(lunchStop)) {
                adjustedTimestamp = punchTime.with(lunchStop);
                adjustmentType = "Lunch Stop";
            } else if (punchTime.toLocalTime().isAfter(shiftStop)) {
                // Round clock-out to interval after shift end
                adjustedTimestamp = roundToInterval(punchTime, roundInterval);
                adjustmentType = "Interval Round";
            } else if (punchTime.toLocalTime().isAfter(shiftStop.minusMinutes(gracePeriod))) {
                // Within grace period for clock-out, round to shift end
                adjustedTimestamp = punchTime.with(shiftStop);
                adjustmentType = "Shift Stop";
            } else {
                adjustedTimestamp = punchTime;
                adjustmentType = "None";
            }
        } else {
            // No adjustment for time-out punches
            adjustedTimestamp = originalTimestamp;
            adjustmentType = "None";
        }
    }
    
    private LocalDateTime roundToInterval(LocalDateTime timestamp, int interval) {
        long minutes = timestamp.getMinute();
        long roundedMinutes = (minutes / interval) * interval;
        return timestamp.truncatedTo(ChronoUnit.HOURS).plusMinutes(roundedMinutes);
    }
    
    
    
    public String printOriginal() {
        
        String formattedTimestamp = originalTimestamp.format(FORMATTER);
        
        //out put was M[on] instead of M[ON]
        formattedTimestamp = formattedTimestamp.toUpperCase(); //this fixes the error
        
        return "#" + badge.getId() + " " + punchType + ": " + formattedTimestamp;
        
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy HH:mm:ss");
        //String formattedTimestamp = originalTimestamp.format(formatter);
        //return "#" + badge.getId() + " " + punchType + ": " + formattedTimestamp;
    }
    
    public String printAdjusted() {
        if (adjustedTimestamp != null) {
            String formattedAdjustedTimestamp = adjustedTimestamp.format(FORMATTER);
            return "#" + badge.getId() + " " + punchType + ": " + formattedAdjustedTimestamp + " (" + adjustmentType + ")";
        } else {
            return "Adjusted timestamp not set";
        }
    }
    
     private String formatPunchType() {
        switch (punchType) {
            case CLOCK_IN:
                return "CLOCK IN";
            case CLOCK_OUT:
                return "CLOCK OUT";
            case TIME_OUT:
                return "TIME OUT";
            default:
                return "UNKNOWN";
        }
    }
    
     public String toString() {
        return printOriginal();
    }
    
}
