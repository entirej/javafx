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

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


final class MainStackPane extends StackPane
{

    private AnimatedStackPane monthView;
    private AnimatedStackPane yearView;
    private AnimatedStackPane decadesView;
    private CalendarView      calendarView;

    public MainStackPane(final CalendarView calendarView)
    {

        this.calendarView = calendarView;
        monthView = new AnimatedStackPane(new MonthView(calendarView), new MonthView(calendarView));
        yearView = new AnimatedStackPane(new YearView(calendarView), new YearView(calendarView));
        decadesView = new AnimatedStackPane(new DecadesView(calendarView), new DecadesView(calendarView));

        getChildren().addAll(monthView, yearView, decadesView);

        calendarView.title.bind(monthView.actualPane.titleProperty());

        yearView.setVisible(false);
        decadesView.setVisible(false);

        
        calendarView.currentlyViewing.addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber)
            {

                calendarView.title.unbind();
                switch (oldNumber.intValue())
                {
                    case Calendar.MONTH:
                        switch (newNumber.intValue())
                        {
                       
                            case Calendar.YEAR:
                                calendarView.title.bind(yearView.actualPane.titleProperty());
                                showOrHide(yearView, true);
                                break;
                        }
                        break;
                    case Calendar.YEAR:
                        switch (newNumber.intValue())
                        {
                        
                            case Calendar.MONTH:
                                showOrHide(yearView, false);
                                monthView.requestFocus();
                                calendarView.title.bind(monthView.actualPane.titleProperty());
                                break;
                          
                            case Calendar.ERA:
                                showOrHide(decadesView, true);
                                calendarView.title.bind(decadesView.actualPane.titleProperty());
                                break;
                        }
                        break;
                    case Calendar.ERA:
                        switch (newNumber.intValue())
                        {
                       
                            case Calendar.YEAR:
                                showOrHide(decadesView, false);
                                calendarView.title.bind(yearView.actualPane.titleProperty());
                                break;

                        }
                        break;
                }
            }
        });
    }

   
    private void showOrHide(final AnimatedStackPane stackPane, final boolean show)
    {
        stackPane.setVisible(true);

        calendarView.ongoingTransitions.set(calendarView.ongoingTransitions.get() + 1);
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), stackPane);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), stackPane);

        setClip(new Rectangle(stackPane.getBoundsInLocal().getWidth(), stackPane.getBoundsInLocal().getHeight()));

        if (show)
        {
            translateTransition.setFromY(-getBoundsInLocal().getHeight());
            translateTransition.setToY(0);
            fadeTransition.setToValue(1);
            fadeTransition.setFromValue(0);

        }
        else
        {
            translateTransition.setToY(-getBoundsInLocal().getHeight());
            translateTransition.setFromY(0);
            fadeTransition.setToValue(0);
            fadeTransition.setFromValue(1);
        }

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().add(translateTransition);
        parallelTransition.getChildren().add(fadeTransition);
        parallelTransition.play();
        parallelTransition.setOnFinished(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {

                if (!show)
                {
                    calendarView.mainNavigationPane.titleButton.requestFocus();
                    stackPane.setVisible(false);
                }
                calendarView.ongoingTransitions.set(calendarView.ongoingTransitions.get() - 1);
                if (calendarView.ongoingTransitions.get() == 0)
                {
                    setClip(null);
                }
            }
        });
    }
}
