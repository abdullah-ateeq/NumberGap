package com.example.numbergap.util

import android.view.HapticFeedbackConstants
import android.view.View

object HapticUtil {
    fun performLightHaptic(view: View?, enabled: Boolean) {
        if (!enabled || view == null) return
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    fun performErrorHaptic(view: View?, enabled: Boolean) {
        if (!enabled || view == null) return
        view.performHapticFeedback(
            HapticFeedbackConstants.REJECT
        )
    }

    fun performSuccessHaptic(view: View?, enabled: Boolean) {
        if (!enabled || view == null) return
        view.performHapticFeedback(
            HapticFeedbackConstants.CONFIRM
        )
    }
}
