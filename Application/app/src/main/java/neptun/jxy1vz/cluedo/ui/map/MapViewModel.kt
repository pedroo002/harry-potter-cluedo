package neptun.jxy1vz.cluedo.ui.map

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.otaliastudios.zoom.ZoomLayout
import kotlinx.android.synthetic.main.activity_map.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.domain.handler.*
import neptun.jxy1vz.cluedo.domain.model.*
import neptun.jxy1vz.cluedo.domain.model.helper.GameModels
import neptun.jxy1vz.cluedo.ui.dialog.note.NoteDialog
import kotlin.math.abs

class MapViewModel(
    gm: GameModels,
    listener: MapActivityListener,
    context: Context,
    playerId: Int,
    pairs: List<Pair<Player, ImageView>>,
    root: ZoomLayout,
    fragmentManager: FragmentManager
) : BaseObservable() {

    companion object {
        const val ROWS = 24
        const val COLS = 24

        var cameraHandler = CameraHandler(this)
        var cardHandler = CardHandler(this)
        var dialogHandler = DialogHandler(this)
        var gameSequenceHandler = GameSequenceHandler(this)
        var interactionHandler = InteractionHandler(this)
        var mapHandler = MapHandler(this)
        var playerHandler = PlayerHandler(this)
        var stateMachineHandler = StateMachineHandler(this)
        var uiHandler = UIHandler(this)

        var mPlayerId: Int? = null
        var mContext: Context? = null

        lateinit var mapRoot: ZoomLayout
        lateinit var gameModels: GameModels
        lateinit var playerImagePairs: List<Pair<Player, ImageView>>

        lateinit var fm: FragmentManager

        lateinit var activityListener: MapActivityListener

        var otherPlayerStepsOnStar: Boolean = false
        var playerInTurnAffected = false
        lateinit var player: Player
        lateinit var mapGraph: Graph<Position>
        var selectionList: ArrayList<ImageView> = ArrayList()
        lateinit var diceList: List<ImageView>

        lateinit var anim: Animation

        var slytherinState = 0
        var ravenclawState = 0
        var gryffindorState = 0
        var hufflepuffState = 0

        var isGameRunning = false
        var playerInTurn: Int = 0
        var userFinishedHisTurn = false
        var userHasToIncriminate = false
        var userHasToStepOrIncriminate = false
        var userCanStep = false

        var isGameAbleToContinue = true
        var playerInTurnDied = false

        var pause = false
        var savedPlayerId = -1
        var savedDiceValue = 0
        var savedHouse: StateMachineHandler.HogwartsHouse? = null

        lateinit var unusedMysteryCards: ArrayList<MysteryCard>

        fun onDestroy() {
            otherPlayerStepsOnStar = false
            playerInTurnAffected = false
            isGameRunning = false
            userFinishedHisTurn = false
            userHasToStepOrIncriminate = false
            userHasToIncriminate = false
            userCanStep = false
            pause = false
            isGameAbleToContinue = true
            playerInTurnDied = false
            savedDiceValue = 0
            savedPlayerId = -1
            savedHouse = null
            slytherinState = 0
            ravenclawState = 0
            gryffindorState = 0
            hufflepuffState = 0
        }

        fun insertFragment(fragment: Fragment) {
            mapRoot.postOnAnimation {
                val layoutParams = ConstraintLayout.LayoutParams(mContext!!.resources.displayMetrics.widthPixels, mContext!!.resources.displayMetrics.heightPixels)
                mapRoot.dialogFrame.layoutParams = layoutParams
                mapRoot.dialogFrame.x = abs(mapRoot.panX)
                mapRoot.dialogFrame.y = abs(mapRoot.panY)
                fm.beginTransaction().replace(R.id.dialogFrame, fragment).commit()
                mapRoot.dialogFrame.bringToFront()
                mapRoot.setScrollEnabled(false)
            }
        }
    }

    init {
        mPlayerId = playerId
        mContext = context
        mapRoot = root
        gameModels = gm
        playerImagePairs = pairs
        fm = fragmentManager
        activityListener = listener
        player = playerHandler.getPlayerById(mPlayerId!!)
        anim = AnimationUtils.loadAnimation(mContext!!, R.anim.shake)
        playerInTurn = mPlayerId!!

        var idx = gameModels.playerList.indexOf(player)
        idx++
        if (idx == gameModels.playerList.size)
            idx = 0
        playerInTurn = gameModels.playerList[idx].id

        val dice1 = ImageView(mapRoot.mapLayout.context)
        dice1.layoutParams = ConstraintLayout.LayoutParams(100, 100)
        dice1.setImageResource(R.drawable.dice1)
        dice1.visibility = ImageView.GONE

        val dice2 = ImageView(mapRoot.mapLayout.context)
        dice2.layoutParams = ConstraintLayout.LayoutParams(100, 100)
        dice2.setImageResource(R.drawable.helper_card)
        dice2.visibility = ImageView.GONE

        val dice3 = ImageView(mapRoot.mapLayout.context)
        dice3.layoutParams = ConstraintLayout.LayoutParams(100, 100)
        dice3.setImageResource(R.drawable.dice2)
        dice3.visibility = ImageView.GONE

        diceList = listOf(dice1, dice2, dice3)
        for (dice in diceList) {
            uiHandler.setLayoutConstraintStart(dice, gameModels.cols[0])
            uiHandler.setLayoutConstraintTop(dice, gameModels.rows[0])
            mapRoot.mapLayout.addView(dice)
        }

        anim.setAnimationListener(uiHandler)

        mapRoot.mapLayout.setOnClickListener {
            cameraHandler.moveCameraToPlayer(playerInTurn)
        }

        val initHp = when (gameModels.playerList.size) {
            3 -> 60
            4 -> 70
            else -> 80
        }

        for (p in gameModels.playerList) {
            p.hp = initHp
        }

        NoteDialog(player, dialogHandler).show(fm, "DIALOG_NOTE")

        stateMachineHandler.setState(playerId, StateMachineHandler.HogwartsHouse.SLYTHERIN)
        stateMachineHandler.setState(playerId, StateMachineHandler.HogwartsHouse.RAVENCLAW)
        stateMachineHandler.setState(playerId, StateMachineHandler.HogwartsHouse.GRYFFINDOR)
        stateMachineHandler.setState(playerId, StateMachineHandler.HogwartsHouse.HUFFLEPUFF)

        for (pair in playerImagePairs) {
            uiHandler.setLayoutConstraintStart(pair.second, gameModels.cols[pair.first.pos.col])
            uiHandler.setLayoutConstraintTop(pair.second, gameModels.rows[pair.first.pos.row])
        }

        mapGraph = Graph()

        for (x in 0..COLS) {
            for (y in 0..ROWS) {
                val current = Position(y, x)
                if (mapHandler.stepInRoom(current) == -1) {
                    if (y > 0 && mapHandler.stepInRoom(Position(y - 1, x)) == -1)
                        mapGraph.addEdge(current, Position(y - 1, x))
                    if (y < 24 && mapHandler.stepInRoom(Position(y + 1, x)) == -1)
                        mapGraph.addEdge(current, Position(y + 1, x))
                    if (x > 0 && mapHandler.stepInRoom(Position(y, x - 1)) == -1)
                        mapGraph.addEdge(current, Position(y, x - 1))
                    if (x < 24 && mapHandler.stepInRoom(Position(y, x + 1)) == -1)
                        mapGraph.addEdge(current, Position(y, x + 1))

                }
            }
        }

        for (door: Door in gameModels.doorList) {
            for (i in 0..4) {
                mapGraph.addEdge(Position(door.room.top, door.room.left + i), door.position)
                mapGraph.addEdge(door.position, Position(door.room.top, door.room.left + i))
            }
        }

        for (p in gameModels.playerList) {
            for (card in p.mysteryCards) {
                p.getConclusion(card.name, p.id)
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            unusedMysteryCards = ArrayList()
            unusedMysteryCards.addAll(gameModels.db.getUnusedMysteryCards())
        }
    }

    fun showOptions(playerId: Int) {
        interactionHandler.showOptions(playerId)
    }
}