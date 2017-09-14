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

import java.util.Calendar;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;


final class MainNavigationPane extends HBox
{

    private static final String CSS_CALENDAR_NAVIGATION_ARROW  = "calendar-navigation-arrow";
    private static final String CSS_CALENDAR_NAVIGATION_BUTTON = "calendar-navigation-button";
    private static final String CSS_CALENDAR_NAVIGATION_TITLE  = "calendar-navigation-title";
    private static final String CSS_CALENDAR_HEADER            = "calendar-header";

    private CalendarView        calendarView;
    Button                      titleButton;

    public MainNavigationPane(final CalendarView calendarView)
    {

        this.calendarView = calendarView;

        titleButton = new Button();
        titleButton.getStyleClass().add(CSS_CALENDAR_NAVIGATION_TITLE);
        titleButton.textProperty().bind(calendarView.title);

        titleButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                switch (calendarView.currentlyViewing.get())
                {
                    case Calendar.MONTH:
                        calendarView.currentlyViewing.set(Calendar.YEAR);
                        break;
                    case Calendar.YEAR:
                        calendarView.currentlyViewing.set(Calendar.ERA);
                }
            }
        });
        titleButton.disableProperty().bind(new BooleanBinding()
        {
            {
                super.bind(calendarView.ongoingTransitions, calendarView.currentlyViewing);
            }

            @Override
            protected boolean computeValue()
            {
                return calendarView.currentlyViewing.get() == Calendar.ERA || calendarView.ongoingTransitions.get() > 0;
            }
        });
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(titleButton);
        buttonBox.setAlignment(Pos.CENTER);

        HBox.setHgrow(buttonBox, Priority.ALWAYS);

        getChildren().add(getNavigationButton(-1));
        getChildren().add(buttonBox);
        getChildren().add(getNavigationButton(1));

        getStyleClass().add(CSS_CALENDAR_HEADER);
    }

   
    private Button getNavigationButton(final int direction)
    {

        Button button = new Button();

        button.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                Calendar calendar = calendarView.getCalendar();
                switch (calendarView.currentlyViewing.get())
                {
                    case Calendar.MONTH:
                        calendar.add(Calendar.MONTH, 1 * direction);
                        break;
                    case Calendar.YEAR:
                        calendar.add(Calendar.YEAR, 1 * direction);
                        break;
                    case Calendar.ERA:
                        calendar.add(Calendar.YEAR, 20 * direction);
                        break;
                }

                calendarView.calendarDate.set(calendar.getTime());
            }
        });

      
        Region rectangle = new Region();
        rectangle.setMaxWidth(Control.USE_PREF_SIZE);
        rectangle.setMaxHeight(Control.USE_PREF_SIZE);
        rectangle.setRotate(direction < 0 ? 90 : 270);
        rectangle.getStyleClass().add(CSS_CALENDAR_NAVIGATION_ARROW);
      
        button.setGraphic(rectangle);
        button.getStyleClass().add(CSS_CALENDAR_NAVIGATION_BUTTON);
        return button;
    }

}
