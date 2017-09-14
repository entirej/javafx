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
import java.util.Date;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


final class AnimatedStackPane extends StackPane
{

    DatePane                    animatePane;
    DatePane                    actualPane;

    private final static Double SLIDE_ANIMATION_DURATION = 0.7;

    public AnimatedStackPane(final DatePane actualPane, final DatePane animatePane)
    {

        
        this.animatePane = animatePane;

       
        animatePane.setVisible(false);

      
        this.actualPane = actualPane;

        getChildren().add(animatePane);
        getChildren().add(actualPane);
        getStyleClass().add("calendar-daypane");

       
        actualPane.calendarView.calendarDate.addListener(new ChangeListener<Date>()
        {
            @Override
            public void changed(ObservableValue<? extends Date> observableValue, Date oldDate, Date newDate)
            {

                Calendar calendar = actualPane.calendarView.getCalendar();

                calendar.setTime(oldDate);
                int oldYear = calendar.get(Calendar.YEAR);
                int oldMonth = calendar.get(Calendar.MONTH);

                calendar.setTime(newDate);
                int newYear = calendar.get(Calendar.YEAR);
                int newMonth = calendar.get(Calendar.MONTH);

               
                if (getWidth() > 0 && actualPane.calendarView.ongoingTransitions.get() == 0)
                {
                    if (newYear > oldYear || newYear == oldYear && newMonth > oldMonth)
                    {
                        slideLeftRight(-1, oldDate);
                    }
                    else if (newYear < oldYear || newYear == oldYear && newMonth < oldMonth)
                    {
                        slideLeftRight(1, oldDate);
                    }
                }
            }
        });
    }

    private ParallelTransition slideTransition;

    private void slideLeftRight(int direction, Date oldDate)
    {

        // Stop any previous animation.
        if (slideTransition != null)
        {
            slideTransition.stop();
        }

        TranslateTransition transition1 = new TranslateTransition(Duration.seconds(SLIDE_ANIMATION_DURATION), animatePane);
        TranslateTransition transition2 = new TranslateTransition(Duration.seconds(SLIDE_ANIMATION_DURATION), actualPane);

        
        animatePane.setVisible(true);

        
        animatePane.setDate(oldDate);

       
        setClip(new Rectangle(animatePane.getBoundsInLocal().getWidth(), animatePane.getBoundsInLocal().getHeight()));

       
        transition1.setFromX(-direction * 1);
       
        transition1.setToX(getLayoutBounds().getWidth() * direction + -direction * 1);

        
        transition2.setFromX(-getBoundsInParent().getWidth() * direction);

       
        transition2.setToX(0);

        slideTransition = new ParallelTransition();
        slideTransition.getChildren().addAll(transition1, transition2);
        slideTransition.setInterpolator(Interpolator.EASE_OUT);

        slideTransition.play();
        slideTransition.setOnFinished(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
               
                animatePane.setVisible(false);
              
                setClip(null);
            }
        });
    }
}
