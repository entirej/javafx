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
package org.entirej.applicationframework.fx.application;

import java.util.ArrayList;

import javafx.event.Event;
import javafx.event.EventHandler;

import org.entirej.applicationframework.fx.notifications.EJFXNotifierDialog;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.applicationframework.fx.utils.FXDialogs;
import org.entirej.applicationframework.fx.utils.FXDialogs.Builder;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.controllers.EJInternalQuestion;
import org.entirej.framework.core.data.controllers.EJQuestion;
import org.entirej.framework.core.enumerations.EJQuestionButton;
import org.entirej.framework.core.interfaces.EJMessenger;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJFXMessenger implements EJMessenger
{
    private final EJFXApplicationManager manager;
    private final MessagingContex        contex;
    final Logger                         logger = LoggerFactory.getLogger(EJFXMessenger.class);

    public EJFXMessenger(EJFXApplicationManager manager)
    {
        this.manager = manager;

        contex = new MessagingContex();
    }

    public void handleMessage(EJMessage message)
    {
        switch (message.getLevel())
        {
            case DEBUG:
                logger.debug(message.getMessage());
                break;
            case HINT:
                EJFXNotifierDialog.notify("Hint", message.getMessage(), EJFXImageRetriever.get(EJFXImageRetriever.IMG_INFO), contex.hintWidth,
                        contex.hintHeight, contex.hintNotificationAutoHide);
            case MESSAGE:

                switch (contex.infoType)
                {
                    case BOTH:
                        FXDialogs.showInfo("Message", message.getMessage(), manager.getPrimaryStage());
                        EJFXNotifierDialog.notify("Message", message.getMessage(), EJFXImageRetriever.get(EJFXImageRetriever.IMG_INFO), contex.infoWidth,
                                contex.infoHeight, contex.infoNotificationAutoHide);
                        break;
                    case DIALOG:
                        FXDialogs.showInfo("Message", message.getMessage(), manager.getPrimaryStage());
                        break;
                    case NOTFICATION:
                        EJFXNotifierDialog.notify("Message", message.getMessage(), EJFXImageRetriever.get(EJFXImageRetriever.IMG_INFO), contex.infoWidth,
                                contex.infoHeight, contex.infoNotificationAutoHide);
                        break;
                }
                break;
            case WARNING:

                switch (contex.warningType)
                {
                    case BOTH:
                        FXDialogs.showWarning("Warning", message.getMessage(), manager.getPrimaryStage());
                        EJFXNotifierDialog.notify("Warning", message.getMessage(), EJFXImageRetriever.get(EJFXImageRetriever.IMG_WARNING), contex.warnWidth,
                                contex.warnHeight, contex.warnNotificationAutoHide);
                        break;
                    case DIALOG:
                        FXDialogs.showWarning("Warning", message.getMessage(), manager.getPrimaryStage());
                        break;
                    case NOTFICATION:
                        EJFXNotifierDialog.notify("Warning", message.getMessage(), EJFXImageRetriever.get(EJFXImageRetriever.IMG_WARNING), contex.warnWidth,
                                contex.warnHeight, contex.warnNotificationAutoHide);
                        break;

                }

                break;
            case ERROR:
                switch (contex.errorType)
                {
                    case BOTH:
                        FXDialogs.showError("Error", message.getMessage(), manager.getPrimaryStage());
                        EJFXNotifierDialog.notify("Error", message.getMessage(), EJFXImageRetriever.get(EJFXImageRetriever.IMG_ERROR), contex.errorWidth,
                                contex.errorHeight, contex.errorNotificationAutoHide);
                        break;
                    case DIALOG:
                        FXDialogs.showError("Error", message.getMessage(), manager.getPrimaryStage());
                        break;
                    case NOTFICATION:
                        EJFXNotifierDialog.notify("Error", message.getMessage(), EJFXImageRetriever.get(EJFXImageRetriever.IMG_ERROR), contex.errorWidth,
                                contex.errorHeight, contex.errorNotificationAutoHide);
                        break;

                }

                break;
            default:
                System.out.println(message.getMessage());
        }
    }

    /**
     * Asks the given question and after the user has made an answer, the answer
     * will be sent to the corresponding <code>IActionProcessor</code>
     * 
     * @param question
     *            The question to be asked
     */
    public void askInternalQuestion(EJInternalQuestion question)
    {
        askQuestion(question);
        question.getForm().internalQuestionAnswered(question);
    }

    /**
     * Asks the given question and after the user has made an answer, the answer
     * will be sent to the corresponding <code>IActionProcessor</code>
     * 
     * @param question
     *            The question to be asked
     */
    public void askQuestion(final EJQuestion question)
    {
        final EJQuestionButton[] optionsButtons = getOptions(question);
        String[] options = new String[optionsButtons.length];

        Builder buildConfirmation = FXDialogs.buildConfirmation(question.getTitle(), question.getMessageText());
        for (int i = 0; i < optionsButtons.length; i++)
        {
            final EJQuestionButton answer = optionsButtons[i];
            buildConfirmation.addConfirmationButton(question.getButtonText(answer), new EventHandler<Event>()
            {

                @Override
                public void handle(Event event)
                {
                    question.setAnswer(answer);
                    question.getActionProcessor().questionAnswered(question);
                }
            });
            options[i] = question.getButtonText(optionsButtons[i]);
        }
        buildConfirmation.setOwner(manager.getPrimaryStage()).build().showAndWait();

    }

    private EJQuestionButton[] getOptions(EJQuestion question)
    {
        ArrayList<EJQuestionButton> options = new ArrayList<EJQuestionButton>();

        String but1Text = question.getButtonText(EJQuestionButton.ONE);
        String but2Text = question.getButtonText(EJQuestionButton.TWO);
        String but3Text = question.getButtonText(EJQuestionButton.THREE);

        if (but1Text != null && but1Text.trim().length() > 0)
        {
            options.add(EJQuestionButton.ONE);
        }
        if (but2Text != null && but2Text.trim().length() > 0)
        {
            options.add(EJQuestionButton.TWO);
        }
        if (but3Text != null && but3Text.trim().length() > 0)
        {
            options.add(EJQuestionButton.THREE);
        }

        return options.toArray(new EJQuestionButton[0]);
    }

    public void handleException(Exception exception, boolean showUserMessage)
    {
        exception.printStackTrace();
        if (exception instanceof EJApplicationException && showUserMessage)
        {
            handleMessage(((EJApplicationException) exception).getFrameworkMessage());
        }
        else if (showUserMessage)
        {
            FXDialogs.showError("Error", exception.getMessage(), manager.getPrimaryStage());

        }
    }

    public void handleException(Exception exception)
    {
        handleException(exception, true);
    }

    private static class MessagingContex
    {

        public static final String APP_MESSAGING                 = "APP_MESSAGING";
        public static final String APP_MSG_ERROR                 = "APP_MSG_ERROR";
        public static final String APP_MSG_HINT                  = "APP_MSG_HINT";
        public static final String APP_MSG_INFO                  = "APP_MSG_INFO";
        public static final String APP_MSG_WARNING               = "APP_MSG_WARNING";

        public static final String APP_MSG_TYPE                  = "APP_MSG_TYPE";
        public static final String APP_MSG_TYPE_DIALOG           = "DIALOG";
        public static final String APP_MSG_TYPE_NOTIFICATION     = "NOTIFICATION";
        public static final String APP_MSG_TYPE_BOTH             = "BOTH";

        public static final String APP_MSG_WIDTH                 = "WIDTH";
        public static final String APP_MSG_HEIGHT                = "HEIGHT";

        public static final String APP_MSG_NOTIFICATION_AUTOHIDE = "APP_MSG_NOTIFICATION_AUTOHIDE";

        enum TYPE
        {
            DIALOG, NOTFICATION, BOTH
        };

        TYPE    errorType                 = TYPE.DIALOG;
        TYPE    infoType                  = TYPE.NOTFICATION;
        TYPE    warningType               = TYPE.DIALOG;

        int     errorWidth                = 400;
        int     errorHeight               = 100;

        int     infoWidth                 = 400;
        int     infoHeight                = 100;

        int     warnWidth                 = 400;
        int     warnHeight                = 100;

        int     hintWidth                 = 400;
        int     hintHeight                = 100;

        boolean errorNotificationAutoHide = false;
        boolean warnNotificationAutoHide  = false;
        boolean infoNotificationAutoHide  = true;
        boolean hintNotificationAutoHide  = true;

        public MessagingContex()
        {
            EJFrameworkExtensionProperties definedProperties = EJCoreProperties.getInstance().getApplicationDefinedProperties();

            if (definedProperties != null)
            {
                EJFrameworkExtensionProperties settings = definedProperties.getPropertyGroup(APP_MESSAGING);
                if (settings != null)
                {
                    EJFrameworkExtensionProperties errorGroup = settings.getPropertyGroup(APP_MSG_ERROR);
                    if (errorGroup != null)
                    {
                        String type = errorGroup.getStringProperty(APP_MSG_TYPE);
                        if (type != null && APP_MSG_TYPE_DIALOG.equals(type))
                        {
                            errorType = TYPE.DIALOG;
                        }
                        else if (type != null && APP_MSG_TYPE_NOTIFICATION.equals(type))
                        {
                            errorType = TYPE.NOTFICATION;
                        }
                        else if (type != null && APP_MSG_TYPE_BOTH.equals(type))
                        {
                            errorType = TYPE.BOTH;
                        }

                        errorNotificationAutoHide = errorGroup.getBooleanProperty(APP_MSG_NOTIFICATION_AUTOHIDE, errorNotificationAutoHide);

                        errorWidth = errorGroup.getIntProperty(APP_MSG_WIDTH, errorWidth);
                        errorHeight = errorGroup.getIntProperty(APP_MSG_HEIGHT, errorHeight);
                    }

                    EJFrameworkExtensionProperties warnGroup = settings.getPropertyGroup(APP_MSG_WARNING);
                    if (warnGroup != null)
                    {
                        String type = warnGroup.getStringProperty(APP_MSG_TYPE);
                        if (type != null && APP_MSG_TYPE_DIALOG.equals(type))
                        {
                            warningType = TYPE.DIALOG;
                        }
                        else if (type != null && APP_MSG_TYPE_NOTIFICATION.equals(type))
                        {
                            warningType = TYPE.NOTFICATION;
                        }
                        else if (type != null && APP_MSG_TYPE_BOTH.equals(type))
                        {
                            warningType = TYPE.BOTH;
                        }

                        warnNotificationAutoHide = warnGroup.getBooleanProperty(APP_MSG_NOTIFICATION_AUTOHIDE, warnNotificationAutoHide);

                        warnWidth = warnGroup.getIntProperty(APP_MSG_WIDTH, warnWidth);
                        warnHeight = warnGroup.getIntProperty(APP_MSG_HEIGHT, warnHeight);
                    }

                    EJFrameworkExtensionProperties infoGroup = settings.getPropertyGroup(APP_MSG_INFO);
                    if (infoGroup != null)
                    {
                        String type = infoGroup.getStringProperty(APP_MSG_TYPE);
                        if (type != null && APP_MSG_TYPE_DIALOG.equals(type))
                        {
                            infoType = TYPE.DIALOG;
                        }
                        else if (type != null && APP_MSG_TYPE_NOTIFICATION.equals(type))
                        {
                            infoType = TYPE.NOTFICATION;
                        }
                        else if (type != null && APP_MSG_TYPE_BOTH.equals(type))
                        {
                            infoType = TYPE.BOTH;
                        }

                        infoNotificationAutoHide = infoGroup.getBooleanProperty(APP_MSG_NOTIFICATION_AUTOHIDE, infoNotificationAutoHide);

                        infoWidth = infoGroup.getIntProperty(APP_MSG_WIDTH, infoWidth);
                        infoHeight = infoGroup.getIntProperty(APP_MSG_HEIGHT, infoHeight);
                    }

                    EJFrameworkExtensionProperties hintGroup = settings.getPropertyGroup(APP_MSG_HINT);
                    if (hintGroup != null)
                    {

                        hintNotificationAutoHide = hintGroup.getBooleanProperty(APP_MSG_NOTIFICATION_AUTOHIDE, hintNotificationAutoHide);

                        hintWidth = hintGroup.getIntProperty(APP_MSG_WIDTH, hintWidth);
                        hintHeight = hintGroup.getIntProperty(APP_MSG_HEIGHT, hintHeight);
                    }
                }
            }
        }
    }

}
