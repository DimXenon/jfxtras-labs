package jfxtras.labs.icalendaragenda;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
                AgendaRenderVComponentsTest.class
              , AgendaRenderAppointmentsTest.class
              , AgendaChangeTest.class
              , AgendaEditPopupTest.class
              , AgendaStringConverstionTests.class
              })
public class AllTests {

}
