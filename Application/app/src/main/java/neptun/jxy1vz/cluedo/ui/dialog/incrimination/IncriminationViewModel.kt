package neptun.jxy1vz.cluedo.ui.dialog.incrimination

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogIncriminationBinding
import neptun.jxy1vz.cluedo.model.helper.roomList
import neptun.jxy1vz.cluedo.model.helper.suspectTokens
import neptun.jxy1vz.cluedo.model.helper.toolTokens

class IncriminationViewModel(private val bind: DialogIncriminationBinding, private val context: Context, private val roomId: Int) : BaseObservable(),
    AdapterView.OnItemSelectedListener {

    private var title = ""

    fun getTitle(): String {
        return title
    }

    private var tool = ""
    private var suspect = ""

    fun finalize() {

    }

    init {
        title = "Helyszín: " + roomList[roomId].name

        bind.spinnerTool.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, context.resources.getStringArray(R.array.tools))
        bind.spinnerSuspect.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, context.resources.getStringArray(R.array.suspects))

        bind.spinnerTool.onItemSelectedListener = this
        bind.spinnerSuspect.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent) {
            bind.spinnerTool -> {
                bind.ivTool.setImageResource(toolTokens[position])
                tool = bind.spinnerTool.selectedItem.toString()
            }
            bind.spinnerSuspect -> {
                bind.ivSuspect.setImageResource(suspectTokens[position])
                suspect = bind.spinnerSuspect.selectedItem.toString()
            }
        }
    }
}