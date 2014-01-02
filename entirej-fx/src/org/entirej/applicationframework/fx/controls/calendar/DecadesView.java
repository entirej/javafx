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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;


final class DecadesView extends DatePane
{

    private static final String CSS_CALENDAR_DECADES_VIEW = "calendar-decades-view";

    private final static int    NUMBER_OF_DECADES         = 2;

    public DecadesView(final CalendarView calendarView)
    {
        super(calendarView);
        getStyleClass().add(CSS_CALENDAR_DECADES_VIEW);
    }

    
    @Override
    protected void buildContent()
    {

        final Calendar calendar = calendarView.getCalendar();

        for (int i = 0; i < NUMBER_OF_DECADES * 10; i++)
        {

            final Button button = new Button();
            button.setMaxWidth(Double.MAX_VALUE);
            button.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(button, Priority.ALWAYS);
            GridPane.setHgrow(button, Priority.ALWAYS);

            button.getStyleClass().add("calendar-year-button");
            button.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent actionEvent)
                {
                    if (calendarView.currentlyViewing.get() == Calendar.ERA)
                    {
                        calendar.set(Calendar.YEAR, (Integer) button.getUserData());
                        calendarView.currentlyViewing.set(Calendar.YEAR);
                        calendarView.calendarDate.set(calendar.getTime());
                    }
                }
            }

            );
            int rowIndex = i % 5;
            int colIndex = (i - rowIndex) / 5;

            add(button, rowIndex, colIndex);
        }
    }

   
    @Override
    protected void updateContent()
    {
        final Calendar calendar = calendarView.getCalendar();

        int year = calendar.get(Calendar.YEAR);
        int a = year % 10;
        if (a < 5)
        {
            a += 10;
        }
        int startYear = year - a;
        for (int i = 0; i < 10 * NUMBER_OF_DECADES; i++)
        {
            final int y = i + startYear;
            Button button = (Button) getChildren().get(i);
            button.setText(Integer.toString(y));
            button.setUserData(y);
        }

        title.set(String.format("%s - %s", startYear, startYear + 10 * NUMBER_OF_DECADES - 1));
    }
}
