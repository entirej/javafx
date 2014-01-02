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

package org.entirej.applicationframework.fx.notifications;

import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Based on hansolo's Enzo project Notification.java implementation.
 */
public class EJFXNotifierDialog
{

    private final String title;
    private final String message;
    private final Image  image;

    EJFXNotifierDialog(final String title, final String message)
    {
        this(title, message, null);
    }

    EJFXNotifierDialog(final String message, final Image image)
    {
        this("", message, image);
    }

    EJFXNotifierDialog(final String title, final String message, final Image image)
    {
        this.title = title;
        this.message = message;
        this.image = image;
    }

    public static void notify(String title, String message, Image image, int width, int height, boolean autoHide)
    {
        Notifier.INSTANCE.notify(title, message, image, width, height, autoHide);
    }

    public enum Notifier
    {
        INSTANCE;

        private static final double   ICON_WIDTH    = 24;
        private static final double   ICON_HEIGHT   = 24;
        private static double         offsetX       = 5;
        private static double         offsetY       = 25;
        private static double         spacingY      = 5;
        private static Pos            popupLocation = Pos.BOTTOM_RIGHT;
        private static Stage          stageRef      = null;
        private Duration              popupLifetime;
        private Stage                 stage;
        private Scene                 scene;
        private ObservableList<Popup> popups;

        private Notifier()
        {
            init();
            initGraphics();
        }

        private void init()
        {
            popupLifetime = Duration.millis(5000);
            popups = FXCollections.observableArrayList();
        }

        private void initGraphics()
        {
            scene = new Scene(new Region());
            scene.setFill(null);
            scene.getStylesheets().add(getClass().getResource("notifier.css").toExternalForm());

            stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
        }

        static void setPopupLocation(final Stage stage_ref, final Pos popup_location)
        {
            if (null != stage_ref)
            {
                INSTANCE.stage.initOwner(stage_ref);
                Notifier.stageRef = stage_ref;
            }
            Notifier.popupLocation = popup_location;
        }

        public static void setNotificationOwner(final Stage owner)
        {
            INSTANCE.stage.initOwner(owner);
        }

        public static void setOffsetX(final double offset_x)
        {
            Notifier.offsetX = offset_x;
        }

        public static void setOffsetY(final double offset_y)
        {
            Notifier.offsetY = offset_y;
        }

        public static void setSpacingY(final double spacing_y)
        {
            Notifier.spacingY = spacing_y;
        }

        void stop()
        {
            popups.clear();
            stage.close();
        }

        Duration getPopupLifetime()
        {
            return popupLifetime;
        }

        void setPopupLifetime(final Duration popup_lifetime)
        {
            popupLifetime = Duration.millis(clamp(2000, 20000, popup_lifetime.toMillis()));
        }

        private void notify(final EJFXNotifierDialog notification, boolean autoHide, int width, int height)
        {
            preOrder(width, height);
            showPopup(notification, autoHide, width, height);
        }

        void notify(final String title, final String message, final Image image, int width, int height, boolean autoHide)
        {
            notify(new EJFXNotifierDialog(title, message, image), autoHide, width, height);
        }

        private double clamp(final double min, final double max, final double value)
        {
            if (value < min)
                return min;
            if (value > max)
                return max;
            return value;
        }

        private void preOrder(int width, int height)
        {
            if (popups.isEmpty())
                return;
            for (int i = 0; i < popups.size(); i++)
            {
                switch (popupLocation)
                {
                    case TOP_LEFT:
                    case TOP_CENTER:
                    case TOP_RIGHT:
                        popups.get(i).setY(popups.get(i).getY() + height + spacingY);
                        break;
                    default:
                        popups.get(i).setY(popups.get(i).getY() - height - spacingY);
                }
            }
        }

        private void showPopup(final EJFXNotifierDialog notification, boolean autoHide, int width, int height)
        {

            HBox hBox = new HBox(2);
            Label title = new Label(notification.title);
            
            title.getStyleClass().add("title");
            ImageView close = new ImageView(EJFXImageRetriever.get(EJFXImageRetriever.IMG_CLOSE));
            
            HBox.setHgrow(title, Priority.ALWAYS);
            HBox.setHgrow(close, Priority.NEVER);
            hBox.getChildren().addAll(title,close);
            title.setPrefWidth(width);
            hBox.setPrefWidth(width);
            
            
            ImageView icon = new ImageView(notification.image);
            icon.setFitWidth(ICON_WIDTH);
            icon.setFitHeight(ICON_HEIGHT);
            
            

            Label message = new Label(notification.message, icon);
            message.getStyleClass().add("message");

            VBox popupLayout = new VBox();
            popupLayout.setSpacing(10);
            popupLayout.setPadding(new Insets(10, 10, 10, 10));
            popupLayout.getChildren().addAll(hBox, message);

            StackPane popupContent = new StackPane();
            popupContent.setPrefSize(width, height);
            popupContent.getStyleClass().add("notification");
            popupContent.getChildren().addAll(popupLayout);

            final Popup POPUP = new Popup();
            POPUP.setX(getX(width, height));
            POPUP.setY(getY(width, height));
            POPUP.getContent().add(popupContent);

            popups.add(POPUP);

            KeyValue fadeOutBegin = new KeyValue(POPUP.opacityProperty(), 1.0);
            KeyValue fadeOutEnd = new KeyValue(POPUP.opacityProperty(), 0.0);

            KeyFrame kfBegin = new KeyFrame(Duration.ZERO, fadeOutBegin);
            KeyFrame kfEnd = new KeyFrame(Duration.millis(500), fadeOutEnd);

            if (stage.isShowing())
            {
                stage.toFront();
            }
            else
            {
                stage.show();
            }

            POPUP.show(stage);
            close.setOnMouseReleased(new EventHandler<MouseEvent>()
            {

                @Override
                public void handle(MouseEvent arg0)
                {
                    POPUP.hide();
                    popups.remove(POPUP);

                }
            });
            if (autoHide)
            {
                Timeline timeline = new Timeline(kfBegin, kfEnd);
                timeline.setDelay(popupLifetime);
                timeline.setOnFinished(new EventHandler<ActionEvent>()
                {

                    @Override
                    public void handle(ActionEvent arg0)
                    {
                        POPUP.hide();
                        popups.remove(POPUP);

                    }
                });
                timeline.play();
            }
        }

        private double getX(int width, int height)
        {
            if (null == stageRef)
                return calcX(0.0, Screen.getPrimary().getBounds().getWidth(), width, height);

            return calcX(stageRef.getX(), stageRef.getWidth(), width, height);
        }

        private double getY(int width, int height)
        {
            if (null == stageRef)
                return calcY(0.0, Screen.getPrimary().getBounds().getHeight(), width, height);

            return calcY(stageRef.getY(), stageRef.getHeight(), width, height);
        }

        private double calcX(final double left, final double total_width, int width, int height)
        {
            switch (popupLocation)
            {
                case TOP_LEFT:
                case CENTER_LEFT:
                case BOTTOM_LEFT:
                    return left + offsetX;
                case TOP_CENTER:
                case CENTER:
                case BOTTOM_CENTER:
                    return left + (total_width - width) * 0.5 - offsetX;
                case TOP_RIGHT:
                case CENTER_RIGHT:
                case BOTTOM_RIGHT:
                    return left + total_width - width - offsetX;
                default:
                    return 0.0;
            }
        }

        private double calcY(final double top, final double total_height, int width, int height)
        {
            switch (popupLocation)
            {
                case TOP_LEFT:
                case TOP_CENTER:
                case TOP_RIGHT:
                    return top + offsetY;
                case CENTER_LEFT:
                case CENTER:
                case CENTER_RIGHT:
                    return top + (total_height - height) / 2 - offsetY;
                case BOTTOM_LEFT:
                case BOTTOM_CENTER:
                case BOTTOM_RIGHT:
                    return top + total_height - height - offsetY;
                default:
                    return 0.0;
            }
        }
    }
}
