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
package org.entirej.applicationframework.fx.utils;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;

public enum EJFXVisualAttributeUtils
{
    INSTANCE;

    private static final String PREFIX = "va";

    public String toCSS(EJCoreVisualAttributeProperties va)
    {
        return new StringBuilder().append(PREFIX).append("-").append(va.getName().replaceAll(" ", "-")).toString();

    }

    public String buildVACSS(EJCoreProperties properties)
    {

        StringBuilder builder = new StringBuilder();

        Collection<EJCoreVisualAttributeProperties> visualAttributes = properties.getVisualAttributesContainer().getVisualAttributes();
        for (EJCoreVisualAttributeProperties va : visualAttributes)
        {
            if (va.getName() != null && va.getName().length() > 0)
            {
                builder.append(".").append(toCSS(va)).append("\n{\n");
                // body
                if (valid(va.getFontName()))
                {
                    builder.append("-fx-font-family:").append(va.getFontName()).append(";\n");
                }
                if (va.getFontSize() > 0)
                {
                    int fontSize = va.getFontSize();
                    if(va.isFontSizeAsPercentage()&& fontSize!=100)
                    {
                        fontSize = fontSize* (fontSize/100);
                    }
                    builder.append("-fx-font-size:").append(fontSize).append(";\n");
                }

                switch (va.getFontStyle())
                {
                    case Italic:
                        builder.append("-fx-font-style:").append("italic").append(";\n");
                        break;
                    case Underline:
                        builder.append("-fx-underline").append("true").append(";\n");
                        break;
                    default:
                        break;
                }

                switch (va.getFontWeight())
                {
                    case Bold:
                        builder.append("-fx-font-weight:").append("bold").append(";\n");
                        break;

                    default:
                        break;
                }

                Color bgColor = va.getBackgroundColor();
                if (bgColor != null)
                {
                    String hexString = toHex(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());
                    builder.append("-fx-va-background: ");
                    builder.append(hexString);
                    builder.append(";\n");
                }
                Color fgColor = va.getForegroundColor();
                if (fgColor != null)
                {
                    String hexString = toHex(fgColor.getRed(), fgColor.getGreen(), fgColor.getBlue());
                    builder.append("-fx-text-fill: ");
                    builder.append(hexString);
                    builder.append(";\n");
                }

                // body-end
                builder.append("}\n");

            }

        }

        String css = builder.toString();
        // System.out.println(css);
        if (valid(css))
        {
            try
            {
                File tempFile = File.createTempFile("ej-fx", ".css");
                try (BufferedWriter out = new BufferedWriter(new FileWriter(tempFile)))
                {
                    out.write(css);
                    out.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                tempFile.deleteOnExit();
                return tempFile.toURI().toURL().toString();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String toHex(int r, int g, int b)
    {
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
    }

    private static String toBrowserHexValue(int number)
    {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2)
        {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

    boolean valid(String s)
    {
        return s != null && !s.trim().isEmpty() && !s.equals(EJCoreVisualAttributeProperties.UNSPECIFIED);
    }
}
