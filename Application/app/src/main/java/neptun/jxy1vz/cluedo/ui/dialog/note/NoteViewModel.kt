package neptun.jxy1vz.cluedo.ui.dialog.note

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.isVisible
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.dialog_note.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogNoteBinding
import neptun.jxy1vz.cluedo.domain.model.Note
import neptun.jxy1vz.cluedo.domain.model.Player

class NoteViewModel(context: Context, player: Player, private val bind: DialogNoteBinding) :
    BaseObservable() {

    private var properClick = false
    private lateinit var guidelineTop: Guideline
    private lateinit var guidelineLeft: Guideline
    private lateinit var guidelineBottom: Guideline
    private lateinit var guidelineRight: Guideline

    private val noteList = ArrayList<Note>()

    private val nameRes = mutableListOf(
        R.drawable.gw,
        R.drawable.hp,
        R.drawable.hg,
        R.drawable.rw,
        R.drawable.ll,
        R.drawable.nl
    )

    private var ownName: Int

    private val names = ArrayList<ImageView>()
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
        bind.guidelineColumn1,
        bind.guidelineColumn2,
        bind.guidelineColumn3,
        bind.guidelineColumn4,
        bind.guidelineColumn5,
        bind.guidelineRight
    )

    init {
        val nameList = context.resources.getStringArray(R.array.characters)
        ownName = nameRes[nameList.indexOf(player.card.name)]
        nameRes.remove(ownName)

        bind.svNotepad.noteLayout.ivNotepad.setOnTouchListener { v, event ->
            properClick = false

            if (event?.action == MotionEvent.ACTION_DOWN) {
                val x = event.x
                val y = event.y

                if (x >= cols.first().x) {
                    var guideLineLeft: Guideline? = null
                    var guideLineRight: Guideline? = null
                    for (i in 0 until cols.lastIndex) {
                        if (cols[i].x <= x && cols[i+1].x >= x) {
                            guideLineLeft = cols[i]
                            guideLineRight = cols[i+1]
                            break
                        }
                    }

                    var guideLineTop: Guideline? = null
                    var guideLineBottom: Guideline? = null
                    if (y >= rowsSuspects[1].y && y <= rowsSuspects.last().y) {
                        for (i in 0 until rowsSuspects.lastIndex) {
                            if (rowsSuspects[i].y <= y && rowsSuspects[i+1].y >= y) {
                                guideLineTop = rowsSuspects[i]
                                guideLineBottom = rowsSuspects[i+1]
                                break
                            }
                        }
                    }
                    else if (y >= rowsTools[1].y && y <= rowsTools.last().y) {
                        for (i in 0 until rowsTools.lastIndex) {
                            if (rowsTools[i].y <= y && rowsTools[i+1].y >= y) {
                                guideLineTop = rowsTools[i]
                                guideLineBottom = rowsTools[i+1]
                                break
                            }
                        }
                    }
                    else if (y >= rowsVenues[1].y && y <= rowsVenues.last().y) {
                        for (i in 0 until rowsVenues.lastIndex) {
                            if (rowsVenues[i].y <= y && rowsVenues[i+1].y >= y) {
                                guideLineTop = rowsVenues[i]
                                guideLineBottom = rowsVenues[i+1]
                                break
                            }
                        }
                    }

                    if (guideLineLeft != null && guideLineRight != null && guideLineTop != null && guideLineBottom != null) {
                        val col = cols.indexOf(guideLineLeft)
                        val row = when {
                            rowsSuspects.contains(guideLineTop) -> rowsSuspects.indexOf(guideLineTop) - 1
                            rowsTools.contains(guideLineTop) -> rowsTools.indexOf(guideLineTop) - 1 + 6
                            else -> rowsVenues.indexOf(guideLineTop) - 1 + 12
                        }

                        guidelineTop = guideLineTop
                        guidelineBottom = guideLineBottom
                        guidelineLeft = guideLineLeft
                        guidelineRight = guideLineRight
                        properClick = true

                        for (note in noteList) {
                            if (note.row == row && note.col == col)
                                properClick = false
                        }
                    }
                }
            }
            return@setOnTouchListener false
        }
        bind.svNotepad.noteLayout.ivNotepad.setOnLongClickListener {
            if (properClick)
                showOptionsAbove(this@NoteViewModel.guidelineLeft, this@NoteViewModel.guidelineRight, this@NoteViewModel.guidelineTop, this@NoteViewModel.guidelineBottom)

            return@setOnLongClickListener true
        }

        for (res in nameRes) {
            val background = ImageView(bind.svNotepad.noteLayout.context)
            background.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)
            background.setImageResource(R.drawable.name_background)
            background.visibility = ImageView.GONE
            bind.svNotepad.noteLayout.addView(background)
            backgrounds.add(background)

            val name = ImageView(bind.svNotepad.noteLayout.context)
            name.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)
            name.setImageResource(res)
            name.visibility = ImageView.GONE
            bind.svNotepad.noteLayout.addView(name)

            names.add(name)
        }
    }

    private fun showOptionsAbove(left: Guideline, right: Guideline, top: Guideline, bottom: Guideline) {
        for (i in nameRes.indices) {
            val rowList = when {
                rowsSuspects.contains(top) -> rowsSuspects
                rowsTools.contains(top) -> rowsTools
                else -> rowsVenues
            }

            setLayoutConstraintHorizontal(backgrounds[i], cols[i].id, cols[i+1].id)
            setLayoutConstraintVertical(backgrounds[i], rowList[rowList.indexOf(top)-1].id, rowList[rowList.indexOf(bottom)-1].id)
            backgrounds[i].visibility = ImageView.VISIBLE
            backgrounds[i].bringToFront()

            setLayoutConstraintHorizontal(names[i], cols[i].id, cols[i+1].id)
            setLayoutConstraintVertical(names[i], rowList[rowList.indexOf(top)-1].id, rowList[rowList.indexOf(bottom)-1].id)
            names[i].visibility = ImageView.VISIBLE
            names[i].bringToFront()

            names[i].setOnClickListener {
                if (it.isVisible) {
                    noteInCell(it as ImageView, left, right, top, bottom)
                    for (name in names) {
                        name.visibility = ImageView.GONE
                    }
                    for (bg in backgrounds) {
                        bg.visibility = ImageView.GONE
                    }
                }
            }
        }
    }

    private fun noteInCell(name: ImageView, left: Guideline, right: Guideline, top: Guideline, bottom: Guideline) {
        val newName = ImageView(bind.svNotepad.noteLayout.context)
        newName.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)
        newName.setImageResource(nameRes[names.indexOf(name)])
        newName.visibility = ImageView.VISIBLE
        setLayoutConstraintHorizontal(newName, left.id, right.id)
        setLayoutConstraintVertical(newName, top.id, bottom.id)
        bind.svNotepad.noteLayout.addView(newName)

        val row: Int = when {
            rowsSuspects.contains(top) -> rowsSuspects.indexOf(top) - 1
            rowsTools.contains(top) -> rowsTools.indexOf(top) - 1 + 6
            else -> rowsVenues.indexOf(top) - 1 + 12
        }
        val note = Note(row, cols.indexOf(left), nameRes[names.indexOf(name)])
        noteList.add(note)

        newName.setOnLongClickListener {
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

    private fun setLayoutConstraintHorizontal(view: View, start: Int, end: Int) {
        val layoutParams: ConstraintLayout.LayoutParams =
            view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.startToStart = start
        layoutParams.endToEnd = end
        view.layoutParams = layoutParams
    }
}