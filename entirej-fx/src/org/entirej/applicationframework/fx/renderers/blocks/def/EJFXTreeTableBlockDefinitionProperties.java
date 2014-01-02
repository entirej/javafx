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
package org.entirej.applicationframework.fx.renderers.blocks.def;

public interface EJFXTreeTableBlockDefinitionProperties
{

    public static final String HIDE_TREE_BORDER            = "HIDE_TREE_BORDER";

    public static final String PARENT_ITEM                 = "PARENT_ITEM";
    public static final String RELATION_ITEM               = "RELATION_ITEM";
    public static final String NODE_IMAGE_ITEM             = "NODE_IMAGE_ITEM";
    public static final String NODE_EXPAND_LEVEL           = "NODE_EXPAND_LEVEL";

    public static final String DOUBLE_CLICK_ACTION_COMMAND = "DOUBLE_CLICK_ACTION_COMMAND";

    public static final String COLUMN_HEADER_VA            = "COLUMN_HEADER_VA";
    public static final String UNDERLINE_COLUMN_HEADER     = "UNDERLINE_COLUN_HEADER";
    public static final String COLUMN_ALIGNMENT            = "COL_ALLIGN";

    public static final String IS_COLUMN_FIXED             = "IS_COLUMN_FIXED";
    public static final String DISPLAY_WIDTH_PROPERTY      = "DISPLAY_WIDTH";
    public static final String COLUMN_ALLIGN_LEFT          = "LEFT";
    public static final String COLUMN_ALLIGN_CENTER        = "CENTER";
    public static final String COLUMN_ALLIGN_RIGHT         = "RIGHT";
    public static final String VISUAL_ATTRIBUTE_PROPERTY   = "VISUAL_ATTRIBUTE";

    public static final String SHOW_HEADING_PROPERTY       = "SHOW_HEADING";
    public static final String SHOW_VERTICAL_LINES         = "SHOW_HORIZONTAL_LINES";
    public static final String SHOW_HORIZONTAL_LINES       = "SHOW_VERTICAL_LINES";

    public static final String ALLOW_COLUMN_RESIZE         = "ALLOW_COLUMN_RESIZE";
    public static final String ALLOW_COLUMN_REORDER        = "ALLOW_COLUMN_REORDER";
    public static final String ALLOW_ROW_SORTING           = "ALLOW_ROW_SORTING";

    public static final String FILTER                     = "FILTER";
}
