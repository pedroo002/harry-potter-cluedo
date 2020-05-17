package neptun.jxy1vz.cluedo.ui.fragment.incrimination.incrimination_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentIncriminationDetailsBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class IncriminationDetailsFragment(private val suspect: Suspect, private val listener: DialogDismiss) : Fragment(), ViewModelListener,
    IncriminationDetailsViewModel.DetailsFragmentListener {

    private lateinit var fragmentIncriminationDetailsBinding: FragmentIncriminationDetailsBinding
    private var needToTakeNotes = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentIncriminationDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_incrimination_details, container, false)
        fragmentIncriminationDetailsBinding.incriminationDetailsViewModel = IncriminationDetailsViewModel(fragmentIncriminationDetailsBinding, context!!, suspect, this, this)
        return fragmentIncriminationDetailsBinding.root
    }

    override fun onFinish() {
        listener.onIncriminationDetailsDismiss(needToTakeNotes)
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }

    override fun deliverInformation(needToTakeNotes: Boolean) {
        this.needToTakeNotes = needToTakeNotes
    }
}