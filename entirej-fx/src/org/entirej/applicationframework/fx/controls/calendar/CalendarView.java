/*******************************************************************************
 * Copyright 2013 Mojave Innovations GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Mojave Innovations GmbH - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.fx.controls.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class CalendarView extends VBox
{

    private static final String CSS_CALENDAR_FOOTER       = "calendar-footer";
    private static final String CSS_CALENDAR              = "calendar";
    private static final String CSS_CALENDAR_TODAY_BUTTON = "calendar-today-button";

  
    public CalendarView()
    {
        this(Locale.getDefault());
    }

   
    public CalendarView(final Locale locale)
    {
        this(locale, Calendar.getInstance(locale));

        
        this.locale.addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                calendar.set(Calendar.getInstance(localeProperty().get()));
            }
        });
    }

   
    public CalendarView(final Locale locale, final Calendar calendar)
    {

        this.locale.set(locale);
        this.calendar.set(calendar);

        getStyleClass().add(CSS_CALENDAR);

        setMaxWidth(Control.USE_PREF_SIZE);

        currentlyViewing.set(Calendar.MONTH);

        calendarDate.addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                calendar.setTime(calendarDate.get());
            }
        });
        this.calendarDate.set(new Date());
        currentDate.addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                Date date = new Date();
                if (currentDate.get() != null)
                {
                    date = currentDate.get();
                }
                calendarDate.set(date);
            }
        });
        MainStackPane mainStackPane = new MainStackPane(this);
        VBox.setVgrow(mainStackPane, Priority.ALWAYS);
        mainNavigationPane = new MainNavigationPane(this);

        todayButtonBox = new HBox();
        todayButtonBox.getStyleClass().add(CSS_CALENDAR_FOOTER);

        Button todayButton = new Button();
        todayButton.textProperty().bind(todayButtonText);
        todayButton.getStyleClass().add(CSS_CALENDAR_TODAY_BUTTON);
        todayButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                Calendar calendar = calendarProperty().get();
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                selectedDate.set(calendar.getTime());
            }
        });
        todayButtonBox.setAlignment(Pos.CENTER);
        todayButtonBox.getChildren().add(todayButton);

        getChildren().addAll(mainNavigationPane, mainStackPane);

        showTodayButton.addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                if (showTodayButton.get())
                {
                    getChildren().add(todayButtonBox);
                }
                else
                {
                    getChildren().remove(todayButtonBox);
                }
            }
        });
        showTodayButton.set(true);

    }

    private HBox todayButtonBox;

    
    public ObjectProperty<Locale> localeProperty()
    {
        return locale;
    }

    private ObjectProperty<Locale> locale = new SimpleObjectProperty<Locale>();

    public Locale getLocale()
    {
        return locale.get();
    }

    public void setLocale(Locale locale)
    {
        this.locale.set(locale);
    }

   
    public ObjectProperty<Calendar> calendarProperty()
    {
        return calendar;
    }

    private ObjectProperty<Calendar> calendar = new SimpleObjectProperty<Calendar>();

    public Calendar getCalendar()
    {
        return calendar.get();
    }

    public void setCalendar(Calendar calendar)
    {
        this.calendar.set(calendar);
    }

   
    public ObservableList<Integer> getDisabledWeekdays()
    {
        return disabledWeekdays;
    }

    private ObservableList<Integer> disabledWeekdays = FXCollections.observableArrayList();

    
    public ObservableList<Date> getDisabledDates()
    {
        return disabledDates;
    }

    private ObservableList<Date> disabledDates = FXCollections.observableArrayList();

 
    public ReadOnlyObjectProperty<Date> selectedDateProperty()
    {
        return selectedDate;
    }

    private ObjectProperty<Date> currentDate = new SimpleObjectProperty<Date>();

    public ObjectProperty<Date> currentDateProperty()
    {
        return currentDate;
    }

   
    public BooleanProperty showTodayButtonProperty()
    {
        return showTodayButton;
    }

    private BooleanProperty showTodayButton = new SimpleBooleanProperty();

    public boolean getShowTodayButton()
    {
        return showTodayButton.get();
    }

    public void setShowTodayButton(boolean showTodayButton)
    {
        this.showTodayButton.set(showTodayButton);
    }

    
    public StringProperty todayButtonTextProperty()
    {
        return todayButtonText;
    }

    private StringProperty todayButtonText = new SimpleStringProperty("Today");

    public String getTodayButtonText()
    {
        return todayButtonText.get();
    }

    public void setTodayButtonText(String todayButtonText)
    {
        this.todayButtonText.set(todayButtonText);
    }

 
    public BooleanProperty showWeeksProperty()
    {
        return showWeeks;
    }

    private BooleanProperty showWeeks = new SimpleBooleanProperty(false);

    public boolean getShowWeeks()
    {
        return showWeeks.get();
    }

    public void setShowWeeks(boolean showWeeks)
    {
        this.showWeeks.set(showWeeks);
    }

    
    MainNavigationPane   mainNavigationPane;
    
    IntegerProperty      ongoingTransitions = new SimpleIntegerProperty(0);
    ObjectProperty<Date> selectedDate       = new SimpleObjectProperty<Date>();
    ObjectProperty<Date> calendarDate       = new SimpleObjectProperty<Date>();
    IntegerProperty      currentlyViewing   = new SimpleIntegerProperty();
    StringProperty       title              = new SimpleStringProperty();
}
