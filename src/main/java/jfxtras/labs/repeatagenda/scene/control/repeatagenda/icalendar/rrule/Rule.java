package jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.rrule;

import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Interface for a rule that applies a modification to a Stream of start date/times, such
 * as BYxxx rules, in a recurring event (RRULE).
 * 
 * @author David Bal
 *
 */
public interface Rule
{
    /** 
     * New stream of date/times made after applying rule that either filters out some date/times
     * or adds additional date/times.
     * 
     * */
    Stream<LocalDateTime> stream(Stream<LocalDateTime> inStream, LocalDateTime startDateTime);

    /** Deep copy all fields from source to destination */
    static void copy(Rule source, Rule destination)
    {
        source.copyTo(destination);
    }

    void copyTo(Rule destination);
    
    

}
