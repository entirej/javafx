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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.GridPane;


abstract class DatePane extends GridPane
{

   
    protected DatePane(final CalendarView calendarView)
    {
        this.calendarView = calendarView;

        
        calendarView.calendarDate.addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                updateContent();
            }
        });

       
        calendarView.calendarProperty().addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                getChildren().clear();
                buildContent();
                updateContent();
            }
        });

        buildContent();
        updateContent();
    }

   
    protected CalendarView calendarView;

   
    protected void setDate(Date date)
    {
        calendarView.getCalendar().setTime(date);
        updateContent();
        
        calendarView.getCalendar().setTime(calendarView.calendarDate.get());
    }

    
    protected abstract void buildContent();

    
    protected abstract void updateContent();

    protected StringProperty title = new SimpleStringProperty();

    
    public ReadOnlyStringProperty titleProperty()
    {
        return title;
    }

   
    protected DateFormat getDateFormat(String format)
    {
        DateFormat dateFormat = new SimpleDateFormat(format, calendarView.localeProperty().get());
        dateFormat.setCalendar(calendarView.getCalendar());
        return dateFormat;
    }
}
