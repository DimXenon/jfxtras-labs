package jfxtras.labs.icalendaragenda.trial;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfxtras.labs.icalendaragenda.trial.controller.CalendarController;

public class ICalendarAgendaTrial1 extends Application
{
    private static LocalDate firstDayOfWeekLocalDate = getFirstDayOfWeekLocalDate();
    private static LocalDate getFirstDayOfWeekLocalDate()
    { // copied from AgendaWeekSkin
        Locale.setDefault(Locale.US);
        Locale myLocale = Locale.getDefault();
        WeekFields lWeekFields = WeekFields.of(myLocale);
        int lFirstDayOfWeek = lWeekFields.getFirstDayOfWeek().getValue();
        LocalDate lDisplayedDateTime = LocalDate.now();
        int lCurrentDayOfWeek = lDisplayedDateTime.getDayOfWeek().getValue();

        if (lFirstDayOfWeek <= lCurrentDayOfWeek) {
            lDisplayedDateTime = lDisplayedDateTime.plusDays(-lCurrentDayOfWeek + lFirstDayOfWeek);
        }
        else {
            lDisplayedDateTime = lDisplayedDateTime.plusDays(-lCurrentDayOfWeek - (7-lFirstDayOfWeek));
        }
        return lDisplayedDateTime;
    }
    
    public static void main(String[] args) {
        launch(args);       
    }

	@Override
	public void start(Stage primaryStage) throws IOException, TransformerException, ParserConfigurationException, SAXException
	{       
        // ROOT PANE
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(ICalendarAgendaTrial1.class.getResource("view/Calendar.fxml"));
        BorderPane root = mainLoader.load();
        CalendarController controller = mainLoader.getController();
        controller.setupData(firstDayOfWeekLocalDate, firstDayOfWeekLocalDate.plusDays(7));
        
        Scene scene = new Scene(root, 1366, 768);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ICalendar Agenda Demo");
        primaryStage.show();
        
    }
}
