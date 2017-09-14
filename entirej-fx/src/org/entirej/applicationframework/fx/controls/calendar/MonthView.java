/*******************************************************************************
 * Copyright 2013 CRESOFT AG
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
 *     CRESOFT AG - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.fx.controls.calendar;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;


final class MonthView extends DatePane
{

    private static final String CSS_CALENDAR_MONTH_VIEW        = "calendar-month-view";
    private static final String CSS_CALENDAR_DAY_CURRENT_MONTH = "calendar-cell-current-month";
    private static final String CSS_CALENDAR_DAY_OTHER_MONTH   = "calendar-cell-other-month";
    private static final String CSS_CALENDAR_TODAY             = "calendar-cell-today";
    private static final String CSS_CALENDAR_WEEKDAYS          = "calendar-weekdays";
    private static final String CSS_CALENDAR_WEEK_NUMBER       = "calendar-week-number";

   
    private int                 numberOfDaysPerWeek;

  
    public MonthView(final CalendarView calendarView)
    {
        super(calendarView);

        getStyleClass().add(CSS_CALENDAR_MONTH_VIEW);

        
        calendarView.localeProperty().addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                updateContent();
            }
        });

       
        calendarView.getDisabledWeekdays().addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                updateDays();
            }
        });

        
        calendarView.getDisabledDates().addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                updateDays();
            }
        });

       
        calendarView.showWeeksProperty().addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                getChildren().clear();
                buildContent();
                updateContent();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildContent()
    {
        Calendar calendar = calendarView.calendarProperty().get();

       
        numberOfDaysPerWeek = calendar.getMaximum(Calendar.DAY_OF_WEEK);

        
        int maxNumberOfDaysInMonth = calendar.getMaximum(Calendar.DAY_OF_MONTH);

      
        int numberOfRows = (int) Math.ceil((maxNumberOfDaysInMonth - 1) / (double) numberOfDaysPerWeek) + 1;

      
        getChildren().clear();

        int colOffset = calendarView.getShowWeeks() ? 1 : 0;

        if (calendarView.getShowWeeks())
        {
            Label empty = new Label();
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.getStyleClass().add(CSS_CALENDAR_WEEKDAYS);
            add(empty, 0, 0);
        }
       
        for (int i = 0; i < numberOfDaysPerWeek; i++)
        {
            Label label = new Label();
            label.getStyleClass().add(CSS_CALENDAR_WEEKDAYS);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setAlignment(Pos.CENTER);
            add(label, i + colOffset, 0);
        }

       
        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++)
        {

            if (calendarView.getShowWeeks())
            {
                Label label = new Label();
                label.setMaxWidth(Double.MAX_VALUE);
                label.setMaxHeight(Double.MAX_VALUE);
                label.getStyleClass().add(CSS_CALENDAR_WEEK_NUMBER);
                add(label, 0, rowIndex + 1);
            }

           
            for (int colIndex = 0; colIndex < numberOfDaysPerWeek; colIndex++)
            {
                final Button button = new Button();
                button.setMaxWidth(Double.MAX_VALUE);
                button.setMaxHeight(Double.MAX_VALUE);

                GridPane.setVgrow(button, Priority.ALWAYS);
                GridPane.setHgrow(button, Priority.ALWAYS);
                button.setOnAction(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent actionEvent)
                    {

                        calendarView.selectedDate.set((Date) button.getUserData());
                    }
                });
              
                add(button, colIndex + colOffset, rowIndex + 1);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateContent()
    {
        updateDays();
        updateWeekNames();
    }

    /**
     * Updates the days.
     */
    private void updateDays()
    {
        Calendar calendar = calendarView.getCalendar();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, calendarView.localeProperty().get());

        dateFormat.setCalendar(calendarView.getCalendar());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

       
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int month = calendar.get(Calendar.MONTH);

        Date tmp = calendar.getTime();

        calendar.setTime(new Date());
        int todayDay = calendar.get(Calendar.DATE);
        int todayMonth = calendar.get(Calendar.MONTH);
        int todayYear = calendar.get(Calendar.YEAR);
        calendar.setTime(tmp);

       
        while (calendar.getFirstDayOfWeek() != calendar.get(Calendar.DAY_OF_WEEK))
        {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

       
        for (int i = numberOfDaysPerWeek + (calendarView.getShowWeeks() ? 1 : 0); i < getChildren().size(); i++)
        {
            final Date currentDate = calendar.getTime();
            if (i % (numberOfDaysPerWeek + 1) == 0 && (calendarView.getShowWeeks()))
            {
                Label label = (Label) getChildren().get(i);
                label.setText(Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR)));
            }
            else
            {
                Button control = (Button) getChildren().get(i);

                control.setText(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
                control.setTooltip(new Tooltip(dateFormat.format(currentDate)));

                boolean disabled = calendarView.getDisabledWeekdays().contains(calendar.get(Calendar.DAY_OF_WEEK));

                for (Date disabledDate : calendarView.getDisabledDates())
                {
                    Calendar clone = (Calendar) calendar.clone();
                    clone.setTime(disabledDate);
                    if (calendar.get(Calendar.YEAR) == clone.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == clone.get(Calendar.MONTH)
                            && calendar.get(Calendar.DAY_OF_MONTH) == clone.get(Calendar.DAY_OF_MONTH))
                    {
                        disabled = true;
                    }
                }

                control.setDisable(disabled);

                control.getStyleClass().remove(CSS_CALENDAR_DAY_CURRENT_MONTH);
                control.getStyleClass().remove(CSS_CALENDAR_DAY_OTHER_MONTH);
                control.getStyleClass().remove(CSS_CALENDAR_TODAY);

                if (calendar.get(Calendar.MONTH) == month)
                {
                    control.getStyleClass().add(CSS_CALENDAR_DAY_CURRENT_MONTH);
                }
                else
                {
                    control.getStyleClass().add(CSS_CALENDAR_DAY_OTHER_MONTH);
                }

                if (calendar.get(Calendar.YEAR) == todayYear && calendar.get(Calendar.MONTH) == todayMonth && calendar.get(Calendar.DATE) == todayDay)
                {
                    control.getStyleClass().add(CSS_CALENDAR_TODAY);
                }

                control.setUserData(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
            }
        }

     
        calendar.setTime(calendarView.calendarDate.get());
    }

    
    private void updateWeekNames()
    {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(calendarView.localeProperty().get());
        String[] weekDays = dateFormatSymbols.getShortWeekdays();

       
        for (int i = 1; i < weekDays.length; i++)
        {
            
            String shortWeekDay = weekDays[i].substring(0, 2);

            
            int j = i - calendarView.getCalendar().getFirstDayOfWeek();
            if (j < 0)
            {
                j += weekDays.length - 1;
            }

            Label label = (Label) getChildren().get(j + (calendarView.getShowWeeks() ? 1 : 0));

            label.setText(shortWeekDay);
        }
        title.set(getDateFormat("MMMM yyyy").format(calendarView.getCalendar().getTime()));
    }
}
