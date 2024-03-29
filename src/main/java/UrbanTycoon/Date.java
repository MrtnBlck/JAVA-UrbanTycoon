
package UrbanTycoon;

class Date {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    /**
     * initialize date with given parameters
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute 
     */
    public Date(int year, int month, int day, int hour, int minute) {
        if (year >= 0) {
            this.year = year;
        } else {
            throw new IllegalArgumentException("Invalid year format!" + year);
        }
        
        if (1 <= month && month <= 12) {
            this.month = month;
        } else {
           throw new IllegalArgumentException("Invalid month format!" + month);     
        }
        
        if (1 <= day && day <= maxDaysInMonth(month)) {
            this.day = day;
        } else {
            throw new IllegalArgumentException("Invalid day format!" + day);
        }
        if(0 <= hour && hour <= 23) {
            this.hour = hour;
        } else {
            throw new IllegalArgumentException("Invalid hour format!" + hour);
        }
        
        if (0 <= minute && minute < 60) {
            this.minute = minute;
        } else {
           throw new IllegalArgumentException("Invalid minute format!"+ minute);
        }
    }
    public static Date parseDate(String s){
        if(s.equals("null")) return null;
        else return new Date(s);
    }
    
    public Date(String str){
        year = Integer.parseInt(str.substring(0,4));
        month = Integer.parseInt(str.substring(6,8));
        day = Integer.parseInt(str.substring(10,12));
        hour = Integer.parseInt(str.substring(14,16));
        minute = Integer.parseInt(str.substring(17));
    }

    public Date(Date date) {
        this.year = date.year;
        this.month = date.month;
        this.day = date.day;
        this.hour = date.hour;
        this.minute = date.minute;
    }
    
    

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
    
    /**
     * adds n minutes to the time
     * handles minute overflow
     * @param n how many minutes add to current time
     */
    public void nMinutesElapsed(int n) {
        int currentMinute = minute + n;
        if (currentMinute >= 60) {
            nHoursElapsed(1);
            minute = 0;
            nMinutesElapsed(currentMinute - 60);
        } else {
            minute += n;
        }
    }
    
    /**
     * adds n hours to the time
     * handles hour overflow
     * @param n how many hours add to current time
     */
    public void nHoursElapsed(int n) {
        int currentHour = hour + n;
        if (currentHour >= 24) {
            nDaysElapsed(1);
            hour = 0;
            nHoursElapsed(currentHour - 24);
        } else {
            hour += n;
        }
    }
    
    /**
     * adds n days to the time
     * handles day overflow and knows how many days in a month
     * @param n how many days add to current time
     */
    public void nDaysElapsed(int n) {
        if(n<0) throw new IllegalArgumentException(" "+ n);
        int currentDay = day + n;
        if (currentDay > maxDaysInMonth(month)) {
            nMonthsElapsed(1);
            day = 1;
            nDaysElapsed(currentDay - maxDaysInMonth((month+10)%12+1)-1);
        } else {
            day += n;
        }
      
    }
    
    /**
     * adds n months to the time
     * handles month overflow
     * @param n how many months add to current time
     */
    public void nMonthsElapsed(int n) {
        int currentMonth = month + n;
        if (currentMonth > 12) {
            year++;
            month = 1;
            nMonthsElapsed(currentMonth - 13);
        } else {
            month += n;
        }
    }
    
    /**
     * 
     * @param month
     * @return how many days in a month
     */
    private int maxDaysInMonth(int month) {
        int maxDaysInMonth;
        switch (month) {
            case 1 -> maxDaysInMonth = 31;
            case 2 -> maxDaysInMonth = 28;
            case 3 -> maxDaysInMonth = 31;
            case 4 -> maxDaysInMonth = 30;
            case 5 -> maxDaysInMonth = 31;
            case 6 -> maxDaysInMonth = 30;
            case 7 -> maxDaysInMonth = 31;
            case 8 -> maxDaysInMonth = 31;
            case 9 -> maxDaysInMonth = 30;
            case 10 -> maxDaysInMonth = 31;
            case 11 -> maxDaysInMonth = 30;
            case 12 -> maxDaysInMonth = 31;
            default -> throw new IllegalArgumentException("Invalid month format!"+ month);
        }
        
        return maxDaysInMonth;
    }
    
    /**
     * 
     * @param other
     * @return how many days passed between this and other
     */
    public int howManyDaysPassed(Date other){
        if(year == other.getYear() && month == other.getMonth())
            return day-other.getDay();
        else if(year == other.getYear()){
            int i=month - other.getMonth();
            int days = day - other.getDay();
            while(i>0){
                days += maxDaysInMonth(month-i);
                i--;
            }
            return days;
        }
        else {
            int years = year - other.getYear();
            int days = day-other.getDay();
            if(years>1) days += (years - 1) * 365;
            int months = month - other.getMonth() + 12;
            while(months>0){
                days += maxDaysInMonth((((month-months)+11)%12)+1);
                months--;
            }
            return days;
        }
    }
    
    /**
     * 
     * @param endDate
     * @return how many hours elapsed between this and endDate 
     */
    public long hoursElapsed(Date endDate) {
        long startDateInHours = yearsInHour(this.year) + monthsInHour(this.month) + daysInHour(this.day) + this.hour;
        long endDateInHours = yearsInHour(endDate.year) + monthsInHour(endDate.month) + daysInHour(endDate.day) + endDate.hour;
        return Math.abs(endDateInHours - startDateInHours);
    }
    
    /**
     * 
     * @param years
     * @return years in hour
     */
    private long yearsInHour(int years) {
        return (long)years*365*24;
    }
    
    /**
     * 
     * @param months
     * @return months in hour
     */
    private int monthsInHour(int months) {
        int sum = 0;
        for (int i = 1; i < months; i++) {
            sum += maxDaysInMonth(i);
        }
        return daysInHour(sum);
    }
    
    /**
     * 
     * @param days
     * @return days in hour
     */
    private int daysInHour(int days)  {
        return days*24;
    } 
    
    @Override
    public String toString(){
        return year + ". " + (month<10?"0":"") + month + ". " + (day<10?"0":"") + day + ". " + (hour<10?"0":"") + hour + (minute<10?":0":":") + minute;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.year;
        hash = 67 * hash + this.month;
        hash = 67 * hash + this.day;
        hash = 67 * hash + this.hour;
        hash = 67 * hash + this.minute;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Date other = (Date) obj;
        if (this.year != other.year) {
            return false;
        }
        if (this.month != other.month) {
            return false;
        }
        if (this.day != other.day) {
            return false;
        }
        if (this.hour != other.hour) {
            return false;
        }
        return this.minute == other.minute;
    }
}
