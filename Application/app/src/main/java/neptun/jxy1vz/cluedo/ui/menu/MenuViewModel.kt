package neptun.jxy1vz.cluedo.ui.menu

import android.content.Context
import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
import neptun.jxy1vz.cluedo.ui.menu.character_selector.CharacterSelectorDialog

class MenuViewModel(private val context: Context, private val fragmentManager: FragmentManager) : BaseObservable() {
    fun openCharacterSelector() {
        CharacterSelectorDialog().show(fragmentManager, "DIALOG_CHARACTER")
    }
}