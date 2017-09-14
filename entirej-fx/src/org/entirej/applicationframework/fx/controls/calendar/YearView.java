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

import java.text.DateFormatSymbols;
import java.util.Calendar;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;


final class YearView extends DatePane
{

    private static final String CSS_CALENDAR_YEAR_VIEW    = "calendar-year-view";
    private static final String CSS_CALENDAR_MONTH_BUTTON = "calendar-month-button";

    public YearView(final CalendarView calendarView)
    {
        super(calendarView);

        getStyleClass().add(CSS_CALENDAR_YEAR_VIEW);

        
        calendarView.localeProperty().addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                updateContent();
            }
        });
    }

   
    @Override
    protected void buildContent()
    {

      
        int numberOfMonths = calendarView.getCalendar().getMaximum(Calendar.MONTH) + 1;

        int numberOfColumns = 3;

        for (int i = 0; i < numberOfMonths; i++)
        {
            final int j = i;
            Button button = new Button();
            button.getStyleClass().add(CSS_CALENDAR_MONTH_BUTTON);

          
            button.setMaxWidth(Double.MAX_VALUE);
            button.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(button, Priority.ALWAYS);
            GridPane.setHgrow(button, Priority.ALWAYS);

            button.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent actionEvent)
                {
                    if (calendarView.currentlyViewing.get() == Calendar.YEAR)
                    {
                        calendarView.getCalendar().set(Calendar.MONTH, j);
                        calendarView.currentlyViewing.set(Calendar.MONTH);
                        calendarView.calendarDate.set(calendarView.getCalendar().getTime());
                    }
                }
            });
            int rowIndex = i % numberOfColumns;
            int colIndex = (i - rowIndex) / numberOfColumns;
            add(button, rowIndex, colIndex);
        }
    }

   
    @Override
    protected void updateContent()
    {
        DateFormatSymbols symbols = new DateFormatSymbols(calendarView.localeProperty().get());
        String[] monthNames = symbols.getShortMonths();
        for (int i = 1; i < monthNames.length; i++)
        {
            Button button = (Button) getChildren().get(i - 1);
            button.setText(monthNames[i - 1]);
        }
        title.set(getDateFormat("yyyy").format(calendarView.getCalendar().getTime()));
    }
}
