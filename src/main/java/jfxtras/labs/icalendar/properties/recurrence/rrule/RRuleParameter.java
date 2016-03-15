package jfxtras.labs.icalendar.properties.recurrence.rrule;

import java.util.HashMap;
import java.util.Map;

import jfxtras.labs.icalendar.DateTimeUtilities;
import jfxtras.labs.icalendar.properties.recurrence.rrule.freq.FrequencyUtilities;
import jfxtras.labs.icalendar.properties.recurrence.rrule.freq.FrequencyUtilities.FrequencyParameter;

/**
 * RRule properties with the following data and methods:
 * 
 * @author David Bal
 *
 */
public enum RRuleParameter
{
    FREQUENCY ("FREQ") { // FREQUENCY needs to be first
        @Override
        public void setValue(RRule rrule, String value)
        {
            if (rrule.getFrequency() == null)
            {
                rrule.setFrequency( FrequencyParameter.propertyFromName(value).newInstance() );                
            } else
            {
                throw new IllegalArgumentException(toString() + " can only appear once in calendar component");
            }
        }

        @Override
        public String toParameterString(RRule rrule)
        {
            return toString() + "=" + rrule.getFrequency().toString();
        }

        @Override
        public void copyProperty(RRule source, RRule destination)
        {
            FrequencyUtilities.copy(source.getFrequency(), destination.getFrequency());
        }
    },
    INTERVAL ("INTERVAL") {
        @Override
        public void setValue(RRule rrule, String value)
        {
            if (rrule.getFrequency() != null)
            {
                if (rrule.getFrequency().getInterval() == 1)
                {
                    rrule.getFrequency().setInterval(Integer.parseInt(value));
                } else
                {
                    throw new IllegalArgumentException(toString() + " can only appear once in calendar component");
                }
            } else
            {
                throw new RuntimeException(FREQUENCY + "must be set before " + this + " can be set");
            }
        }

        @Override
        public String toParameterString(RRule rrule)
        {
            Integer interval = rrule.getFrequency().getInterval();
            return (interval > 1) ? toString() + "=" + interval.toString(): null; // 1 is default interval, therefore only output interval > 1
        }

        @Override
        public void copyProperty(RRule source, RRule destination)
        {
            // TODO Auto-generated method stub
            
        }
    },
    COUNT ("COUNT") {
        @Override
        public void setValue(RRule rrule, String value)
        {
            if (rrule.getCount() == null)
            {
                if (rrule.getUntil() == null)
                {
                    rrule.setCount(Integer.parseInt(value));                    
                } else
                {
                    throw new IllegalArgumentException(toString() + " can't be set while " + UNTIL.toString() + " has a value");                                        
                }
            } else
            {
                throw new IllegalArgumentException(toString() + " can only appear once in calendar component");
            }
        }

        @Override
        public String toParameterString(RRule rrule)
        {
            return (rrule.getCount() == null) ? null : toString() + "=" + rrule.getCount();
        }

        @Override
        public void copyProperty(RRule source, RRule destination)
        {
            // TODO Auto-generated method stub
            
        }
    },
    UNTIL ("UNTIL") {
        @Override
        public void setValue(RRule rrule, String value)
        {
            if (rrule.getUntil() == null)
            {
                if (rrule.getCount() == 0)
                {
                    rrule.setUntil(DateTimeUtilities.parse(value));                    
                } else
                {
                    throw new IllegalArgumentException(toString() + " can't be set while " + COUNT.toString() + " has a value");                                        
                }
            } else
            {
                throw new IllegalArgumentException(toString() + " can only appear once in calendar component");                    
            }
        }

        @Override
        public String toParameterString(RRule rrule)
        {
            return (rrule.getUntil() == null) ? null : toString() + "=" + DateTimeUtilities.ZONED_DATE_TIME_UTC_FORMATTER.format(rrule.getUntil());
        }

        @Override
        public void copyProperty(RRule source, RRule destination)
        {
            // TODO Auto-generated method stub
            
        }
    },
    WEEK_START ("WKST") { // TODO - THIS PROPERTY MAY BE BEST HANDLED BY LOCALE - NOT PROCESSED NOW
        // TODO - SUPPOSE TO COME AFTER BYRULES
        @Override
        public void setValue(RRule rrule, String value)
        {
            throw new RuntimeException("not supported");
        }

        @Override
        public String toParameterString(RRule rrule)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void copyProperty(RRule source, RRule destination)
        {
            // TODO Auto-generated method stub
            
        }
    };
        
    // Map to match up name to enum
    private static Map<String, RRuleParameter> propertyFromNameMap = makePropertiesFromNameMap();
    private static Map<String, RRuleParameter> makePropertiesFromNameMap()
    {
        Map<String, RRuleParameter> map = new HashMap<>();
        RRuleParameter[] values = RRuleParameter.values();
        for (int i=0; i<values.length; i++)
        {
            map.put(values[i].toString(), values[i]);
        }
        return map;
    }
    /** get enum from name */
    public static RRuleParameter propertyFromName(String propertyName)
    {
        return propertyFromNameMap.get(propertyName.toUpperCase());
    }
    
    private String name;
//    private int sortOrder;
    
    RRuleParameter(String name)
    {
        this.name = name;
//        this.sortOrder = sortOrder;
    }
    
    /** Returns the iCalendar property name (e.g. LANGUAGE) */
    @Override public String toString() { return name; }
    
//    public int sortOrder() { return sortOrder; }
    
    /** sets parameter value */
    public abstract void setValue(RRule rrule, String value);
    
    /** makes content line (RFC 5545 3.1) from a RRuleProperty property  */
    public abstract String toParameterString(RRule rrule);
    
    /** Copies property value from source to destination */
    public abstract void copyProperty(RRule source, RRule destination);
        
}
