/*
 * Licensed Materials - Property of IBM
 * 5725-B69 5655-Y17 5655-Y31 5724-X98 5724-Y15 5655-V82 
 * Copyright IBM Corp. 1987, 2018. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights: 
 * Use, duplication or disclosure restricted by GSA ADP Schedule 
 * Contract with IBM Corp.
 */

package sample;

import java.text.MessageFormat;
import java.util.ResourceBundle;


class MessageFormatter {
    
    private static final String MESSAGE_BUNDLE = "sample.messages"; // No_i18n
    
    private static ResourceBundle bundle;

    String getMessage(String key, Object... arguments) {
        return MessageFormat.format(getBundle().getString(key), arguments);
    }

    private ResourceBundle getBundle() {
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(MESSAGE_BUNDLE);
        }
        return bundle;
    }
}
