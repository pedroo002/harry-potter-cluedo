package neptun.jxy1vz.cluedo.domain.util

import android.widget.NumberPicker

fun setNumPicker(numPicker: NumberPicker, min: Int, max: Int, color: Int) {
    numPicker.minValue = min
    numPicker.maxValue = max
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        numPicker.textColor = color
    }
}

fun debugPrint(message: String) {
    println("==================== <DEBUG> ====================\n$message\n==================== </DEBUG> ====================")
}