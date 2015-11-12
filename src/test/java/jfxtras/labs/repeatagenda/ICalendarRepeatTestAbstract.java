package jfxtras.labs.repeatagenda;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;

import jfxtras.labs.repeatagenda.scene.control.repeatagenda.rrule.RRule;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.rrule.byxxx.ByDay;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.rrule.byxxx.ByMonth;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.rrule.byxxx.ByMonthDay;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.rrule.byxxx.ByRule;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.rrule.freq.Daily;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.rrule.freq.Frequency;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.rrule.freq.Monthly;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.rrule.freq.Yearly;

public abstract class ICalendarRepeatTestAbstract
{
    
    /** FREQ=MONTHLY, Basic monthly stream, repeats 9th day of every month */
    protected static RRule getMonthlyStream1()
    {
        RRule rule = new RRule()
                .withStartLocalDate(LocalDateTime.of(2015, 11, 9, 10, 0));
        Monthly monthly = new Monthly();
        rule.setFrequency(monthly);
        return rule;
    }

    /** FREQ=MONTHLY;BYMONTHDAY=-2, Monthly stream, negative day of month */
    protected static RRule getMonthlyStream2()
    {
        RRule rule = new RRule()
                .withStartLocalDate(LocalDateTime.of(2015, 11, 29, 10, 0));
        Frequency monthly = new Monthly();
        rule.setFrequency(monthly);
        ByRule by = new ByMonthDay(monthly)
                .withDaysOfMonth(-2);// repeats 2nd to last day
        monthly.addByRule(by);
        return rule;
    }

    /** FREQ=MONTHLY;BYDAY=TU,WE,FR */
    protected static RRule getMonthlyStream3()
    {
        RRule rule = new RRule()
                .withStartLocalDate(LocalDateTime.of(2015, 11, 9, 10, 0));
        Frequency monthly = new Monthly();
        rule.setFrequency(monthly);
        ByRule byRule = new ByDay(monthly, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
        monthly.addByRule(byRule);
        return rule;
    }

    /** FREQ=MONTHLY;BYDAY=-1SA */
    protected static RRule getMonthlyStream4()
    {
        RRule rule = new RRule()
                .withStartLocalDate(LocalDateTime.of(2015, 11, 9, 10, 0));
        Frequency monthly = new Monthly();
        rule.setFrequency(monthly);
        ByRule byRule = new ByDay(monthly, new ByDay.ByDayPair(DayOfWeek.SATURDAY, -1));
        monthly.addByRule(byRule);
        return rule;
    }
    
    /** FREQ=DAILY, Basic daily stream */
    protected static RRule getDailyStream1()
    {
        RRule rule = new RRule()
                .withStartLocalDate(LocalDateTime.of(2015, 11, 9, 10, 0));
        Frequency daily = new Daily();
        rule.setFrequency( daily);
        return rule;
    }

    /** FREQ=DAILY;INVERVAL=3;COUNT=6 */
    protected static RRule getDailyStream2()
    {
        RRule rule = new RRule()
                .withStartLocalDate(LocalDateTime.of(2015, 11, 9, 10, 0))
                .withCount(6);
        Frequency daily = new Daily()
                .withInterval(3);
        rule.setFrequency(daily);
        return rule;
    }

    /** FREQ=DAILY;INVERVAL=3;COUNT=10;BYMONTHDAY=9,10,11,12,13,14 */
    protected static RRule getDailyStream3()
    {
        RRule rule = new RRule()
                .withStartLocalDate(LocalDateTime.of(2015, 11, 9, 10, 0))
                .withCount(10);
        Frequency daily = new Daily()
                .withInterval(3);
        rule.setFrequency(daily);
        ByRule byRule = new ByMonthDay(daily)
                .withDaysOfMonth(9,10,11,12,13,14);
        daily.addByRule(byRule);
        return rule;
    }

    /** FREQ=DAILY;INVERVAL=2;BYMONTHDAY=9 */
    protected static RRule getDailyStream4()
    {
        RRule rule = new RRule()
                .withStartLocalDate(LocalDateTime.of(2015, 11, 9, 10, 0));
        Frequency daily = new Daily()
                .withInterval(2);
        rule.setFrequency(daily);
        ByRule byRule = new ByMonthDay(daily); // use default repeat date from startLocalDateTime (9th of month)
        daily.addByRule(byRule);
        return rule;
    }
    
    /** FREQ=DAILY;INVERVAL=2;BYDAY=FR */
    protected static RRule getDailyStream5()
    {
        RRule rule = new RRule()
                .withStartLocalDate(LocalDateTime.of(2015, 11, 9, 10, 0));
        Frequency daily = new Daily()
                .withInterval(2);
        rule.setFrequency(daily);
        ByRule byRule = new ByDay(daily, DayOfWeek.FRIDAY);
        daily.addByRule(byRule);
        return rule;
    }

    /** FREQ=YEARLY; */
    protected static RRule getYearlyStream1()
    {
        RRule rule = new RRule()
                .withStartLocalDate(LocalDateTime.of(2015, 11, 9, 10, 0));
        Frequency yearly = new Yearly();
        rule.setFrequency(yearly);
        return rule;
    }

    /** FREQ=YEARLY;BYDAY=SU; */
    protected static RRule getYearlyStream2()
    {
        RRule rule = new RRule()
                .withStartLocalDate(LocalDateTime.of(2015, 11, 6, 10, 0));
        Frequency yearly = new Yearly();
        rule.setFrequency(yearly);
        ByRule byRule = new ByDay(yearly, DayOfWeek.FRIDAY);
        yearly.addByRule(byRule);
        return rule;
    }
    
    /**Every Thursday, but only during June, July, and August, forever:
     * DTSTART;TZID=America/New_York:19970605T090000
     * RRULE:FREQ=YEARLY;BYDAY=TH;BYMONTH=6,7,8
     * example in RFC 5545 iCalendar, page 129 */
    protected static RRule getYearlyStream3()
    {
        RRule rule = new RRule()
                .withStartLocalDate(LocalDateTime.of(1997, 6, 5, 9, 0));
        Frequency yearly = new Yearly();
        rule.setFrequency(yearly);
        ByRule byRule = new ByDay(yearly, DayOfWeek.THURSDAY);
        yearly.addByRule(byRule);
        ByRule byRule2 = new ByMonth(yearly, Month.JUNE, Month.JULY, Month.AUGUST);
        yearly.addByRule(byRule2);
        return rule;
    }
    
    /** FREQ=YEARLY;BYMONTH=1,2 */
    protected static RRule getYearlyStream4()
    {
        RRule rule = new RRule()
                .withStartLocalDate(LocalDateTime.of(2015, 1, 6, 10, 0));
        Frequency yearly = new Yearly();
        rule.setFrequency(yearly);
        ByRule byRule = new ByMonth(yearly, Month.JANUARY, Month.FEBRUARY);
        yearly.addByRule(byRule);
        return rule;
    }
}
