package jfxtras.labs.repeatagenda.scene.control.repeatagenda;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import javafx.util.Pair;
import jfxtras.labs.icalendar.DateTimeType;
import jfxtras.labs.icalendar.ICalendarUtilities;
import jfxtras.labs.icalendar.VComponent;
import jfxtras.labs.icalendar.VComponentProperty;
import jfxtras.labs.icalendar.VEvent;
import jfxtras.labs.icalendar.VEventProperty;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.Agenda.AppointmentGroup;

/**
 * Concrete class as an example of a VEvent.
 * This class creates and edits appointments for display in Agenda.
 * 
 * 
 * @author David Bal
 *
 */
public class VEventImpl extends VEvent<Appointment, VEventImpl>
{
    // TODO - THESE CALLBACKS MAY BE OBSOLETE - IF AppointmentImplTemporal IS IN AGENDA
    // USE REFLECTION FROM APPOINTMENT CLASS INSTEAD?
    /*
     *  STATIC CALLBACKS FOR MAKING NEW APPOINTMENTS
     *  One for each of the four date and date-time options in iCalendar:
     *  Date
     *  Date with local time
     *  Date with UTC time
     *  Date with local time and time zone instance
     *  see iCalendar RFC 5545, page 32-33
     *  
     *  Only date-time properties are set in the callbacks.  The other properties
     *  are set in makeInstances method.
     */
    @Deprecated
    private final static Callback<StartEndRange, Appointment> TEMPORAL_INSTANCE = (p) ->
    {
        return new Agenda.AppointmentImplTemporal()
                .withStartTemporal(p.getDateTimeStart())
                .withEndTemporal(p.getDateTimeEnd());
    };
    
    /** For DATE type (whole-day Appointments) */
    @Deprecated
    private final static Callback<StartEndRange, Appointment> NEW_DATE_INSTANCE = (p) ->
    {
        LocalDateTime s = LocalDate.from(p.getDateTimeStart()).atStartOfDay();
        LocalDateTime e = LocalDate.from(p.getDateTimeEnd()).atStartOfDay();
        return new Agenda.AppointmentImplTemporal()
                .withStartTemporal(s)
                .withEndTemporal(e);
//        return new Agenda.AppointmentImplLocal()
//                .withStartLocalDateTime(s)
//                .withEndLocalDateTime(e);
    };

    /** For DATE_WITH_LOCAL_TIME */
    @Deprecated
    private final static Callback<StartEndRange, Appointment> NEW_DATE_WITH_LOCAL_TIME_INSTANCE = (p) ->
    {
        return new Agenda.AppointmentImplTemporal()
                .withStartTemporal(p.getDateTimeStart())
                .withEndTemporal(p.getDateTimeEnd());
//        return new Agenda.AppointmentImplLocal()
//                .withStartLocalDateTime(LocalDateTime.from(p.getDateTimeStart()))
//                .withEndLocalDateTime(LocalDateTime.from(p.getDateTimeEnd()));
    };

    /** For DATE_WITH_UTC_TIME */
    @Deprecated
    private final static Callback<StartEndRange, Appointment> NEW_DATE_WITH_UTC_TIME_INSTANCE = (p) ->
    {
        final ZonedDateTime s;
        if (p.getDateTimeStart() instanceof ZonedDateTime)
        {
            s = ((ZonedDateTime) p.getDateTimeStart()).withZoneSameInstant(ZoneId.of("Z"));
        } else
        {
            s = LocalDateTime.from(p.getDateTimeStart()).atZone(ZoneId.of("Z"));
        }

        final ZonedDateTime e;
        if (p.getDateTimeEnd() instanceof ZonedDateTime)
        {
            e = ((ZonedDateTime) p.getDateTimeEnd()).withZoneSameInstant(ZoneId.of("Z"));
        } else
        {
            e = LocalDateTime.from(p.getDateTimeEnd()).atZone(ZoneId.of("Z"));
        }
        return new Agenda.AppointmentImplTemporal()
                .withStartTemporal(s)
                .withEndTemporal(e);
//        return new Agenda.AppointmentImplZoned()
//                .withStartZonedDateTime(s)
//                .withEndZonedDateTime(e);
    };
    
    /** For DATE_WITH_LOCAL_TIME_AND_TIME_ZONE */
    @Deprecated
    private final static Callback<StartEndRange, Appointment> NEW_DATE_WITH_LOCAL_TIME_AND_TIME_ZONE_INSTANCE = (p) ->
    {
        return new Agenda.AppointmentImplTemporal()
                .withStartTemporal(p.getDateTimeStart())
                .withEndTemporal(p.getDateTimeEnd());
//        return new Agenda.AppointmentImplZoned()
//                .withStartZonedDateTime((ZonedDateTime) p.getDateTimeStart())
//                .withEndZonedDateTime((ZonedDateTime) p.getDateTimeEnd());
    };

    // Map to match up DateTimeType to Callback;
    @Deprecated
    private static final Map<DateTimeType, Callback<StartEndRange, Appointment>> DATE_TIME_MAKE_INSTANCE_MAP = defaultDateTimeInstanceMap();
    @Deprecated
    private static Map<DateTimeType, Callback<StartEndRange, Appointment>> defaultDateTimeInstanceMap()
    {
        Map<DateTimeType, Callback<StartEndRange, Appointment>> map = new HashMap<>();
        map.put(DateTimeType.DATE, NEW_DATE_INSTANCE);
        map.put(DateTimeType.DATE_WITH_LOCAL_TIME, NEW_DATE_WITH_LOCAL_TIME_INSTANCE);
        map.put(DateTimeType.DATE_WITH_UTC_TIME, NEW_DATE_WITH_UTC_TIME_INSTANCE);
        map.put(DateTimeType.DATE_WITH_LOCAL_TIME_AND_TIME_ZONE, NEW_DATE_WITH_LOCAL_TIME_AND_TIME_ZONE_INSTANCE);
        return map;
    }

    
    public ObjectProperty<AppointmentGroup> appointmentGroupProperty() { return appointmentGroup; }
    private ObjectProperty<AppointmentGroup> appointmentGroup = new SimpleObjectProperty<AppointmentGroup>(this, "CATEGORIES");
    public void setAppointmentGroup(AppointmentGroup appointmentGroup) { this.appointmentGroup.set(appointmentGroup); }
    public AppointmentGroup getAppointmentGroup() { return appointmentGroup.get(); }
    public VEventImpl withAppointmentGroup(AppointmentGroup appointmentGroup) { setAppointmentGroup(appointmentGroup); return this; }

    /** appointmentGroups from Agenda.  It is used to synch categories to appointmentGroup, 
     * which is needed by the makeAppointment method 
     * @see #makeInstances() */
    public List<AppointmentGroup> getAppointmentGroups() { return appointmentGroups; }
    final private List<AppointmentGroup> appointmentGroups;

    /* below listeners ensures appointmentGroup description and categories match.  
     * added to categoriesProperty and appointmentGroups by the constructor.
     * appointmentGroups must be set
     */
    private final InvalidationListener categoriesListener = obs ->
    {
        if (! getAppointmentGroups().isEmpty() && getCategories() != null)
        {
            Optional<AppointmentGroup> myGroup = getAppointmentGroups()
                    .stream()
//                    .peek(a -> System.out.println(a.getDescription()))
                    .filter(g -> g.getDescription().equals(getCategories()))
                    .findFirst();
            if (myGroup.isPresent()) setAppointmentGroup(myGroup.get());                
        }
    };
    private final ChangeListener<? super AppointmentGroup> appointmentGroupListener = 
            (obs, oldValue, newValue) -> setCategories(newValue.getDescription());
        
    /*
     * CONSTRUCTORS
     */
    /** Copy constructor */
    public VEventImpl(VEventImpl vevent)
    {
        super(vevent);
        this.appointmentGroups = vevent.getAppointmentGroups();
        categoriesProperty().addListener(categoriesListener);
        appointmentGroup.addListener(appointmentGroupListener);
        copy(vevent, this);
    }
    
    public VEventImpl(List<AppointmentGroup> appointmentGroups)
    {
        super();
        this.appointmentGroups = appointmentGroups;
        categoriesProperty().addListener(categoriesListener);
        appointmentGroup.addListener(appointmentGroupListener);
        if (getAppointmentGroup() == null)
        {
            setAppointmentGroup(getAppointmentGroups().get(0)); // set default appointment group if none set
        }
    }
    
    /**
     * makes new VEventImpl by copying properties from appointment.
     * stores start and end date/times as ZonedDateTime in default time zone
     * 
     * @param appointment - from Agenda
     */
    public VEventImpl(Appointment appointment, ObservableList<AppointmentGroup> appointmentGroups)
    {
        this(appointmentGroups);
        setCategories(appointment.getAppointmentGroup().getDescription());
        setDateTimeStart(appointment.getStartTemporal());
        setDateTimeEnd(appointment.getEndTemporal());
        // Convert appointment LocalDateTime to ZonedDateTime as default Temporal class
//        setDateTimeStart(appointment.getStartLocalDateTime().atZone(ZoneId.systemDefault()));
//        setDateTimeEnd(appointment.getEndLocalDateTime().atZone(ZoneId.systemDefault()));
        setDateTimeStamp(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Z")));
        setDateTimeCreated(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Z")));
        setDescription(appointment.getDescription());
        setLocation(appointment.getLocation());
        setSummary(appointment.getSummary());
        setUniqueIdentifier(getUidGeneratorCallback().call(null));
        instances().add(appointment);
        if (! errorString().equals("")) throw new IllegalArgumentException(errorString());
    }

    /** Deep copy all fields from source to destination 
     * */
    private static void copy(VEventImpl source, VEventImpl destination)
    {
        destination.setAppointmentGroup(source.getAppointmentGroup());
    }
    
    /** Deep copy all fields from source to destination */
    @Override
    public void copyTo(VComponent<Appointment> destination)
    {
        super.copyTo(destination);
        copy(this, (VEventImpl) destination);
    }
        
    /** Make new VEventImpl and populate properties by parsing a string of line-separated
     * content lines
     *  */
    public static VEventImpl parse(String string, List<AppointmentGroup> appointmentGroups)
    {
        VEventImpl vEvent = new VEventImpl(appointmentGroups);
        Iterator<Pair<String, String>> i = ICalendarUtilities.ComponentStringToPropertyNameAndValueList(string).iterator();
        while (i.hasNext())
        {
            Pair<String, String> propertyValuePair = i.next();
            
            // parse each property-value pair by all associated property enums
            // TODO - CONSIDER MAKING A VEventImpl ENUM
            VEventProperty.parse(vEvent, propertyValuePair);
            VComponentProperty.parse(vEvent, propertyValuePair);
        }
        return vEvent;
    }

    /**
     * Tests equality between two VEventImpl objects.  Treats v1 as expected.  Produces a JUnit-like
     * output if objects are not equal.
     * 
     * @param v1 - expected VEventImpl
     * @param v2 - actual VEventImpl
     * @param verbose - true = display list of unequal properties, false no display output
     * @return - equality result
     */
    public static boolean isEqualTo(VEventImpl v1, VEventImpl v2, boolean verbose)
    {
        // VEventImpl properties
        boolean appointmentGroupEquals = (v1.getAppointmentGroup() == null) ? (v2.getAppointmentGroup() == null) : v1.getAppointmentGroup().equals(v2.getAppointmentGroup());
        if (! appointmentGroupEquals && verbose) { System.out.println("Appointment Group:" + " not equal:" + v1.getAppointmentGroup() + " " + v2.getAppointmentGroup()); }
        boolean vEventResult = VEventProperty.isEqualTo(v1, v2, verbose);
        return vEventResult && appointmentGroupEquals;
    }
    
    /**
     * Tests equality between two VEventImpl objects.  Treats v1 as expected.  Produces a JUnit-like
     * output if objects are not equal.
     * 
     * @param v1 - expected VEventImpl
     * @param v2 - actual VEventImpl
     * @param verbose - true = display list of unequal properties, false no display output
     * @return - equality result
     */
    public static boolean isEqualTo(VEventImpl v1, VEventImpl v2)
    {
        return isEqualTo(v1, v2, true);
    }
    
            
    /**
     * Returns appointments for Agenda that should exist between dateTimeRangeStart and dateTimeRangeEnd
     * based on VEvent properties.  Uses dateTimeRange previously set in VEvent.
     * @param <Appointment>
     * 
     * @return created appointments
     */
    @Override
    public List<Appointment> makeInstances()
    {
        if ((getStartRange() == null) || (getEndRange() == null)) throw new RuntimeException("Can't make instances without setting date/time range first");
        List<Appointment> madeAppointments = new ArrayList<>();
        Stream<Temporal> removedTooEarly = stream(getStartRange()).filter(d -> ! VComponent.isBefore(d, getStartRange())); // inclusive
        Stream<Temporal> removedTooLate = ICalendarUtilities.takeWhile(removedTooEarly, a -> VComponent.isBefore(a, getEndRange())); // exclusive
        removedTooLate.forEach(temporalStart ->
        {
            TemporalAmount duration = endType().getDuration(this);
            Temporal temporalEnd = temporalStart.plus(duration);
            Appointment appt = new Agenda.AppointmentImplTemporal()
                    .withStartTemporal(temporalStart)
                    .withEndTemporal(temporalEnd)
                    .withDescription(getDescription())
                    .withSummary(getSummary())
                    .withLocation(getLocation())
                    .withWholeDay(isWholeDay())
                    .withAppointmentGroup(getAppointmentGroup());
            madeAppointments.add(appt);   // add appointments to return argument
            instances().add(appt); // add appointments to this object's collection
        });
        return madeAppointments;
    }
    
    /**
     * Returns appointments for Agenda that should exist between dateTimeRangeStart and dateTimeRangeEnd
     * based on VEvent properties.  Uses dateTimeRange previously set in VEvent.
     * @param <Appointment>
     * 
     * @param startRange
     * @param endRange
     * @return created appointments
     */
    @Override
    public List<Appointment> makeInstances(Temporal startRange, Temporal endRange)
    {
        if (VComponent.isAfter(startRange, endRange)) throw new DateTimeException("endRange must be after startRange");
        setEndRange(endRange);
        setStartRange(startRange);
        return makeInstances();
    }
    
}
