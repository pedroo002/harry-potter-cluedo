package neptun.jxy1vz.cluedo.ui.fragment.note

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.isVisible
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_note.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.database.CluedoDatabase
import neptun.jxy1vz.cluedo.databinding.FragmentNoteBinding
import neptun.jxy1vz.cluedo.domain.model.Note
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.util.Interactor
import neptun.jxy1vz.cluedo.domain.util.toDatabaseModel
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class NoteViewModel(context: Context, player: Player, private val bind: FragmentNoteBinding, private val listener: ViewModelListener) : BaseObservable() {

    private val interactor = Interactor(CluedoDatabase.getInstance(context))
    private val ownCards = player.mysteryCards

    private val suspectNames = context.resources.getStringArray(R.array.suspects)
    private val toolNames = context.resources.getStringArray(R.array.tools)
    private val roomNames = context.resources.getStringArray(R.array.rooms)

    private var guidelineTop: Guideline? = null
    private var guidelineLeft: Guideline? = null
    private var guidelineBottom: Guideline? = null
    private var guidelineRight: Guideline? = null

    private val noteList = ArrayList<Note>()

    private val nameRes = mutableListOf(
        R.drawable.gw,
        R.drawable.hp,
        R.drawable.hg,
        R.drawable.rw,
        R.drawable.ll,
        R.drawable.nl
    )

    private val conclusionTypes = ArrayList<Int>()

    private var ownName: Int

    private val names = ArrayList<ImageView>()
    private val conclusionList = ArrayList<ImageView>()
    private val backgrounds = ArrayList<ImageView>()

    private val rowsVenues = listOf(
        bind.guidelineVenuesTop,
        bind.guidelineRow13,
        bind.guidelineRow14,
        bind.guidelineRow15,
        bind.guidelineRow16,
        bind.guidelineRow17,
        bind.guidelineRow18,
        bind.guidelineRow19,
        bind.guidelineRow20,
        bind.guidelineRow21,
        bind.guidelineVenuesBottom
    )

    private val rowsSuspects = listOf(
        bind.guidelineSuspectsTop,
        bind.guidelineRow1,
        bind.guidelineRow2,
        bind.guidelineRow3,
        bind.guidelineRow4,
        bind.guidelineRow5,
        bind.guidelineRow6,
        bind.guidelineSuspectsBottom
    )

    private val rowsTools = listOf(
        bind.guidelineToolsTop,
        bind.guidelineRow7,
        bind.guidelineRow8,
        bind.guidelineRow9,
        bind.guidelineRow10,
        bind.guidelineRow11,
        bind.guidelineRow12,
        bind.guidelineToolsBottom
    )

    private val cols = listOf(
        bind.guidelineColumn0,
        bind.guidelineColumn1,
        bind.guidelineColumn2,
        bind.guidelineColumn3,
        bind.guidelineColumn4,
        bind.guidelineColumn5,
        bind.guidelineRight
    )

    enum class NoteType {
        NAME,
        CONCLUSION
    }

    private fun addFrames(list: List<Guideline>) {
        for (i in 1 until list.lastIndex) {
            val frame = ImageView(bind.svNotepad.noteLayout.context)
            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            )
            params.setMargins(0, 0, 20, 0)
            addImageToLayout(
                frame,
                R.drawable.frame,
                params,
                list[i],
                cols[1],
                list[i + 1],
                cols[0]
            )
            frame.setOnLongClickListener {
                showOptionsAbove(
                    cols[0],
                    cols[1],
                    list[i],
                    list[i + 1],
                    conclusionTypes,
                    NoteType.CONCLUSION
                )
                return@setOnLongClickListener true
            }
        }
    }

    init {
        GlobalScope.launch(Dispatchers.IO) {
            val dbNotes = interactor.getNotes()
            withContext(Dispatchers.Main) {
                dbNotes?.let {
                    for (note in dbNotes) {
                        var guidelineTop: Guideline
                        var guidelineBottom: Guideline
                        when {
                            note.row <= 6 -> {
                                guidelineTop = rowsSuspects[note.row]
                                guidelineBottom = rowsSuspects[note.row + 1]
                            }
                            note.row in 7..12 -> {
                                guidelineTop = rowsTools[note.row - 6]
                                guidelineBottom = rowsTools[note.row - 6 + 1]
                            }
                            else -> {
                                guidelineTop = rowsVenues[note.row - 12]
                                guidelineBottom = rowsVenues[note.row - 12 + 1]
                            }
                        }
                        val guidelineLeft = cols[note.col]
                        val guidelineRight = cols[note.col + 1]

                        var params: ConstraintLayout.LayoutParams? = null
                        if (note.col == 0) {
                            params = ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
                            )
                            params.setMargins(0, 0, 20, 0)
                        }
                        noteInCell(
                            note.res,
                            guidelineLeft,
                            guidelineRight,
                            guidelineTop,
                            guidelineBottom,
                            params
                        )
                    }
                }
            }
        }

        val nameList = context.resources.getStringArray(R.array.characters)
        ownName = nameRes[nameList.indexOf(player.card.name)]
        nameRes.remove(ownName)

        conclusionTypes.add(ownName)
        conclusionTypes.add(R.drawable.cross)
        conclusionTypes.add(R.drawable.tick)

        addFrames(rowsSuspects)
        addFrames(rowsTools)
        addFrames(rowsVenues)

        bind.svNotepad.noteLayout.ivNotepad.setOnTouchListener { _, event ->
            if (event?.action == MotionEvent.ACTION_DOWN) {
                val x = event.x
                val y = event.y

                if (x >= cols[1].x) {
                    var guideLineLeft: Guideline? = null
                    var guideLineRight: Guideline? = null
                    for (i in 0 until cols.lastIndex) {
                        if (cols[i].x <= x && cols[i + 1].x >= x) {
                            guideLineLeft = cols[i]
                            guideLineRight = cols[i + 1]
                            break
                        }
                    }

                    var guideLineTop: Guideline? = null
                    var guideLineBottom: Guideline? = null
                    if (y >= rowsSuspects[1].y && y <= rowsSuspects.last().y) {
                        for (i in 0 until rowsSuspects.lastIndex) {
                            if (rowsSuspects[i].y <= y && rowsSuspects[i + 1].y >= y) {
                                guideLineTop = rowsSuspects[i]
                                guideLineBottom = rowsSuspects[i + 1]
                                break
                            }
                        }
                    } else if (y >= rowsTools[1].y && y <= rowsTools.last().y) {
                        for (i in 0 until rowsTools.lastIndex) {
                            if (rowsTools[i].y <= y && rowsTools[i + 1].y >= y) {
                                guideLineTop = rowsTools[i]
                                guideLineBottom = rowsTools[i + 1]
                                break
                            }
                        }
                    } else if (y >= rowsVenues[1].y && y <= rowsVenues.last().y) {
                        for (i in 0 until rowsVenues.lastIndex) {
                            if (rowsVenues[i].y <= y && rowsVenues[i + 1].y >= y) {
                                guideLineTop = rowsVenues[i]
                                guideLineBottom = rowsVenues[i + 1]
                                break
                            }
                        }
                    }

                    guidelineTop = guideLineTop
                    guidelineBottom = guideLineBottom
                    guidelineLeft = guideLineLeft
                    guidelineRight = guideLineRight
                }
                else {
                    guidelineTop = null
                    guidelineBottom = null
                    guidelineLeft = null
                    guidelineRight = null
                }
            }
            return@setOnTouchListener false
        }
        bind.svNotepad.noteLayout.ivNotepad.setOnLongClickListener {
            if (guidelineTop != null && guidelineBottom != null && guidelineLeft != null && guidelineRight != null)
                showOptionsAbove(
                    guidelineLeft!!,
                    guidelineRight!!,
                    guidelineTop!!,
                    guidelineBottom!!,
                    nameRes, NoteType.NAME
                )

            return@setOnLongClickListener true
        }

        for (res in nameRes) {
            val background = ImageView(bind.svNotepad.noteLayout.context)
            background.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            )
            background.setImageResource(R.drawable.name_background)
            background.visibility = ImageView.GONE
            bind.svNotepad.noteLayout.addView(background)
            backgrounds.add(background)

            val name = ImageView(bind.svNotepad.noteLayout.context)
            name.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            )
            name.setImageResource(res)
            name.visibility = ImageView.GONE
            bind.svNotepad.noteLayout.addView(name)
            names.add(name)
        }

        for (img in conclusionTypes) {
            val iv = ImageView(bind.svNotepad.noteLayout.context)
            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            )
            params.setMargins(0, 0, 20, 0)
            iv.layoutParams = params
            iv.setImageResource(img)
            iv.visibility = ImageView.GONE
            bind.svNotepad.noteLayout.addView(iv)
            conclusionList.add(iv)
        }

        for (card in ownCards) {
            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            )
            params.setMargins(0, 0, 20, 0)
            when {
                suspectNames.contains(card.name) -> noteInCell(ownName, cols[0], cols[1], rowsSuspects[suspectNames.indexOf(card.name) + 1], rowsSuspects[suspectNames.indexOf(card.name) + 2], params)
                toolNames.contains(card.name) -> noteInCell(ownName, cols[0], cols[1], rowsTools[toolNames.indexOf(card.name) + 1], rowsTools[toolNames.indexOf(card.name) + 2], params)
                else -> noteInCell(ownName, cols[0], cols[1], rowsVenues[roomNames.indexOf(card.name) + 1], rowsVenues[roomNames.indexOf(card.name) + 2], params)
            }
        }
    }

    private fun showOptionsAbove(
        left: Guideline,
        right: Guideline,
        top: Guideline,
        bottom: Guideline,
        optionList: List<Int>,
        type: NoteType
    ) {
        for (i in optionList.indices) {
            val rowList = when {
                rowsSuspects.contains(top) -> rowsSuspects
                rowsTools.contains(top) -> rowsTools
                else -> rowsVenues
            }

            setLayoutConstraintHorizontal(backgrounds[i], cols[i + 1].id, cols[i + 2].id)
            setLayoutConstraintVertical(
                backgrounds[i],
                rowList[rowList.indexOf(top) - 1].id,
                rowList[rowList.indexOf(bottom) - 1].id
            )
            backgrounds[i].visibility = ImageView.VISIBLE
            backgrounds[i].bringToFront()

            val targetList = when (type) {
                NoteType.NAME -> names
                else -> conclusionList
            }

            setLayoutConstraintHorizontal(targetList[i], cols[i + 1].id, cols[i + 2].id)
            setLayoutConstraintVertical(
                targetList[i],
                rowList[rowList.indexOf(top) - 1].id,
                rowList[rowList.indexOf(bottom) - 1].id
            )
            targetList[i].visibility = ImageView.VISIBLE
            targetList[i].bringToFront()

            targetList[i].setOnClickListener {
                if (it.isVisible) {
                    noteInCell(optionList[i], left, right, top, bottom)
                    for (iv in targetList) {
                        iv.visibility = ImageView.GONE
                    }
                    for (bg in backgrounds) {
                        bg.visibility = ImageView.GONE
                    }
                }
            }
        }
    }

    private fun noteInCell(
        imgRes: Int,
        left: Guideline,
        right: Guideline,
        top: Guideline,
        bottom: Guideline,
        layoutParams: ConstraintLayout.LayoutParams? = null
    ) {
        val row: Int = when {
            rowsSuspects.contains(top) -> rowsSuspects.indexOf(top)
            rowsTools.contains(top) -> rowsTools.indexOf(top) + 6
            else -> rowsVenues.indexOf(top) + 12
        }
        val note = Note(row, cols.indexOf(left), imgRes)

        for (n in noteList) {
            if (note.row == n.row && note.col == n.col)
                return
        }

        noteList.add(note)

        val newNote = ImageView(bind.svNotepad.noteLayout.context)
        val params = layoutParams
            ?: ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            )
        if (cols.indexOf(left) == 0)
            params.marginEnd = 20
        addImageToLayout(newNote, imgRes, params, top, right, bottom, left)

        newNote.setOnLongClickListener {
            bind.svNotepad.noteLayout.removeView(it)
            noteList.remove(note)
            return@setOnLongClickListener true
        }
    }

    private fun setLayoutConstraintVertical(view: View, top: Int, bottom: Int) {
        val layoutParams: ConstraintLayout.LayoutParams =
            view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToTop = top
        layoutParams.bottomToBottom = bottom
        view.layoutParams = layoutParams
    }

    private fun setLayoutConstraintHorizontal(view: View, start: Int?, end: Int) {
        val layoutParams: ConstraintLayout.LayoutParams =
            view.layoutParams as ConstraintLayout.LayoutParams
        start?.let {
            layoutParams.startToStart = start
        }
        layoutParams.endToEnd = end
        view.layoutParams = layoutParams
    }

    private fun addImageToLayout(
        image: ImageView,
        imgRes: Int,
        layoutParams: ConstraintLayout.LayoutParams,
        constraintTop: Guideline,
        constraintRight: Guideline,
        constraintBottom: Guideline,
        constraintLeft: Guideline
    ) {
        image.setImageResource(imgRes)
        image.layoutParams = layoutParams
        image.visibility = ImageView.VISIBLE
        setLayoutConstraintHorizontal(image, constraintLeft.id, constraintRight.id)
        setLayoutConstraintVertical(image, constraintTop.id, constraintBottom.id)
        bind.svNotepad.noteLayout.addView(image)
    }

    private fun saveNotes() {
        GlobalScope.launch(Dispatchers.IO) {
            interactor.eraseNotes()
            interactor.insertIntoNotes(noteList.map { note -> note.toDatabaseModel() })
        }
    }

    fun close() {
        saveNotes()
        listener.onFinish()
    }
}