package neptun.jxy1vz.cluedo.ui.activity.map

import android.content.Context
import android.content.SharedPreferences
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.otaliastudios.zoom.ZoomLayout
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.User
import kotlinx.android.synthetic.main.activity_map.view.*
import kotlinx.coroutines.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.database.CluedoDatabase
import neptun.jxy1vz.cluedo.domain.handler.*
import neptun.jxy1vz.cluedo.domain.model.*
import neptun.jxy1vz.cluedo.domain.model.card.HelperCard
import neptun.jxy1vz.cluedo.domain.model.card.MysteryCard
import neptun.jxy1vz.cluedo.domain.model.helper.GameModels
import neptun.jxy1vz.cluedo.domain.util.removePlayer
import neptun.jxy1vz.cluedo.domain.util.toDomainModel
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.message.card_event.CardEventMessage
import neptun.jxy1vz.cluedo.network.model.message.dice.DiceDataMessage
import neptun.jxy1vz.cluedo.network.model.message.move.MovingData
import neptun.jxy1vz.cluedo.network.model.message.presence.PlayerPresenceMessage
import neptun.jxy1vz.cluedo.network.model.message.suspect.SuspectMessage
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.cluedo.ui.fragment.dice_roller.DiceRollerViewModel
import neptun.jxy1vz.cluedo.ui.fragment.endgame.EndOfGameFragment
import neptun.jxy1vz.cluedo.ui.fragment.incrimination.incrimination_details.IncriminationDetailsFragment
import neptun.jxy1vz.cluedo.ui.fragment.on_back_pressed.OnBackPressedFragment
import neptun.jxy1vz.cluedo.ui.fragment.player_dies.PlayerDiesOrLeavesFragment
import kotlin.math.abs

class MapViewModel(
    gm: GameModels,
    listener: MapActivityListener,
    private val context: Context,
    playerId: Int,
    pairs: List<Pair<Player, ImageView>>,
    root: ZoomLayout,
    fragmentManager: FragmentManager
) : BaseObservable() {

    companion object {
        private lateinit var gamePref: SharedPreferences
        private lateinit var playerPref: SharedPreferences
        private lateinit var playMode: String
        private lateinit var playModes: Array<String>
        lateinit var channelName: String

        lateinit var retrofit: RetrofitInstance

        lateinit var diceData: DiceDataMessage

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

        var dialogOpened = false

        var mPlayerId: Int? = null
        var mContext: Context? = null

        lateinit var mapRoot: ZoomLayout
        lateinit var gameModels: GameModels
        lateinit var playerImagePairs: List<Pair<Player, ImageView>>

        lateinit var fm: FragmentManager

        lateinit var activityListener: MapActivityListener

        var otherPlayerStepsOnStar: Boolean = false
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
        var finishedCardCheck = false

        var pause = false
        var savedPlayerId = -1
        var savedDiceValue = 0
        var savedHouse: StateMachineHandler.HogwartsHouse? = null

        lateinit var unusedMysteryCards: ArrayList<MysteryCard>

        fun onDestroy() {
            otherPlayerStepsOnStar = false
            isGameRunning = false
            userFinishedHisTurn = false
            userHasToStepOrIncriminate = false
            userHasToIncriminate = false
            userCanStep = false
            pause = false
            isGameAbleToContinue = true
            playerInTurnDied = false
            finishedCardCheck = false
            savedDiceValue = 0
            savedPlayerId = -1
            savedHouse = null
            slytherinState = 0
            ravenclawState = 0
            gryffindorState = 0
            hufflepuffState = 0
        }

        fun insertFragment(fragment: Fragment, addToBackStack: Boolean = false) {
            val layoutParams = ConstraintLayout.LayoutParams(mContext!!.resources.displayMetrics.widthPixels, mContext!!.resources.displayMetrics.heightPixels)
            mapRoot.dialogFrame.layoutParams = layoutParams
            mapRoot.dialogFrame.x = abs(mapRoot.panX)
            mapRoot.dialogFrame.y = abs(mapRoot.panY)
            if (addToBackStack)
                fm.beginTransaction().add(R.id.dialogFrame, fragment).addToBackStack(fragment.toString()).commit()
            else
                fm.beginTransaction().replace(R.id.dialogFrame, fragment).commit()
            mapRoot.dialogFrame.bringToFront()

            mapRoot.setVerticalPanEnabled(false)
            mapRoot.setHorizontalPanEnabled(false)
            mapRoot.setScrollEnabled(false)
            mapRoot.setTwoFingersScrollEnabled(false)
            mapRoot.setThreeFingersScrollEnabled(false)
            dialogOpened = true
        }

        fun enableScrolling() {
            mapRoot.setVerticalPanEnabled(true)
            mapRoot.setHorizontalPanEnabled(true)
            mapRoot.setScrollEnabled(true)
            mapRoot.setTwoFingersScrollEnabled(true)
            mapRoot.setThreeFingersScrollEnabled(true)
            dialogOpened = false
        }

        fun isGameModeMulti(): Boolean {
            return playMode == playModes[1]
        }
    }

    init {
        gamePref = context.getSharedPreferences(context.resources.getString(R.string.game_params_pref), Context.MODE_PRIVATE)
        playerPref = context.getSharedPreferences(context.resources.getString(R.string.player_data_pref), Context.MODE_PRIVATE)
        playMode = gamePref.getString(context.resources.getString(R.string.play_mode_key), "")!!
        playModes = context.resources.getStringArray(R.array.playmodes)

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
        diceList.forEach { dice ->
            uiHandler.setLayoutConstraintStart(dice, gameModels.cols[0])
            uiHandler.setLayoutConstraintTop(dice, gameModels.rows[0])
            mapRoot.mapLayout.addView(dice)
        }

        anim.setAnimationListener(uiHandler)

        mapRoot.mapLayout.setOnClickListener {
            if (!dialogOpened)
                cameraHandler.moveCameraToPlayer(playerInTurn)
        }

        val initHp = when (gameModels.playerList.size) {
            3 -> 60
            4 -> 70
            else -> 80
        }

        gameModels.playerList.forEach { p ->
            p.hp = initHp
        }

        stateMachineHandler.setState(playerId, StateMachineHandler.HogwartsHouse.SLYTHERIN)
        stateMachineHandler.setState(playerId, StateMachineHandler.HogwartsHouse.RAVENCLAW)
        stateMachineHandler.setState(playerId, StateMachineHandler.HogwartsHouse.GRYFFINDOR)
        stateMachineHandler.setState(playerId, StateMachineHandler.HogwartsHouse.HUFFLEPUFF)

        playerImagePairs.forEach { pair ->
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

        gameModels.doorList.forEach { door ->
            for (i in 0..4) {
                mapGraph.addEdge(Position(door.room.top, door.room.left + i), door.position)
                mapGraph.addEdge(door.position, Position(door.room.top, door.room.left + i))
            }
        }

        gameModels.playerList.forEach { p ->
            p.mysteryCards.forEach { card ->
                p.getConclusion(card.name, p.id)
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            unusedMysteryCards = ArrayList()
            unusedMysteryCards.addAll(gameModels.db.getUnusedMysteryCards())

            if (!isGameModeMulti())
                cardHandler.handOutHelperCards()
            else {
                retrofit = RetrofitInstance.getInstance(context)

                val channelId = playerPref.getString(context.resources.getString(R.string.channel_id_key), "")!!
                val apiChannelName = retrofit.cluedo.getChannel(channelId)!!.channelName
                channelName = "presence-${apiChannelName}"
                subscribeToEvents()
                dialogHandler.subscribeToEvent()

                playerInTurn = gameModels.playerList[0].id
                retrofit.cluedo.notifyMapLoaded(apiChannelName)
            }
        }
    }

    fun showOptions(playerId: Int) {
        interactionHandler.showOptions(playerId)
    }

    fun onBackPressed() {
        val fragment = OnBackPressedFragment.newInstance(dialogHandler)
        insertFragment(fragment, true)
    }

    private fun subscribeToEvents() {
        PusherInstance.getInstance().getPresenceChannel(channelName).apply {
            bind("map-loaded", object :
                PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    if (!isGameRunning && mPlayerId == playerInTurn) {
                        startCardHandOut()
                    }
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })

            bind("incrimination", object :
                PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    val messageJson = retrofit.moshi.adapter(SuspectMessage::class.java).fromJson(message!!)!!
                    val suspect = messageJson.toDomainModel()
                    if (suspect.playerId != mPlayerId)
                        openIncriminationDetails(suspect)
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })

            bind("accusation", object :
                PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    val messageJson = retrofit.moshi.adapter(SuspectMessage::class.java).fromJson(message!!)!!
                    val suspect = messageJson.toDomainModel()
                    if (suspect.playerId != mPlayerId)
                        openAccusationResult(suspect)
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })

            bind("player-moves", object :
                PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    val messageJson = retrofit.moshi.adapter(MovingData::class.java).fromJson(message!!)!!
                    if (messageJson.playerId != mPlayerId)
                        processMovingEvent(messageJson)
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })

            bind("card-drawing", object :
                PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    val messageJson = retrofit.moshi.adapter(CardEventMessage::class.java).fromJson(message!!)!!
                    processCardEvent(messageJson.playerId, messageJson.cardName)
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })

            bind("player-leaves", object :
                PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    val messageJson = retrofit.moshi.adapter(PlayerPresenceMessage::class.java).fromJson(message!!)!!
                    val player = gameModels.playerList.find { p -> p.card.name == messageJson.message.playerName }!!
                    navigateToPlayerAndDelete(player)
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })

            bind("dice-event", object :
                PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    val messageJson = retrofit.moshi.adapter(DiceDataMessage::class.java).fromJson(message!!)!!
                    processDiceEvent(messageJson)
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })
        }
    }

    private fun startCardHandOut() {
        GlobalScope.launch(Dispatchers.Main) {
            cardHandler.handOutHelperCardMulti(mPlayerId!!)
        }
    }

    private fun processCardEvent(playerId: Int, cardName: String) {
        if (playerId == mPlayerId)
            return
        GlobalScope.launch(Dispatchers.IO) {
            val card = gameModels.db.getCardByName(cardName)
            if (card is HelperCard) {
                cardHandler.showCard(playerId, card, DiceRollerViewModel.CardType.HELPER)
                if (!isGameRunning) {
                    delay(500)
                    val idx = gameModels.playerList.indexOf(gameModels.playerList.find { p -> p.id == playerId })
                    val nextIdx = idx + 1
                    if (nextIdx > gameModels.playerList.lastIndex)
                        return@launch
                    if (gameModels.playerList[nextIdx].id == mPlayerId) {
                        withContext(Dispatchers.Main) {
                            cardHandler.handOutHelperCardMulti(mPlayerId!!)
                        }
                    }
                }
            }
            else {
                cardHandler.showCard(playerId, card!!, DiceRollerViewModel.CardType.DARK)
            }
        }
    }

    private fun navigateToPlayerAndDelete(player: Player) {
        GlobalScope.launch(Dispatchers.Main) {
            cameraHandler.moveCameraToPlayer(player.id)
            delay(1000)
            removePlayer(player)

            var title = ""
            withContext(Dispatchers.IO) {
                val playerName = CluedoDatabase.getInstance(context).playerDao().getPlayers()!!
                    .find { p -> p.characterName == player.card.name }!!.playerName
                title = "$playerName ${context.resources.getString(R.string.left_the_game)}"
            }

            val fragment = PlayerDiesOrLeavesFragment.newInstance(player, PlayerDiesOrLeavesFragment.ExitScenario.LEAVE, dialogHandler, title)
            insertFragment(fragment, true)
        }
    }

    private fun processDiceEvent(message: DiceDataMessage) {
        if (message.playerId == mPlayerId)
            return
        GlobalScope.launch(Dispatchers.Main) {
            diceData = message
            interactionHandler.rollWithDice(message.playerId)
        }
    }

    private fun processMovingEvent(data: MovingData) {
        GlobalScope.launch(Dispatchers.Main) {
            uiHandler.animatePlayerWalking(data.playerId, playerImagePairs.find { pair -> pair.first.id == data.playerId }!!.first.pos, Position(data.targetPosition.row, data.targetPosition.col))
        }
    }

    private fun openIncriminationDetails(suspect: Suspect) {
        GlobalScope.launch(Dispatchers.Main) {
            val detailsFragment = IncriminationDetailsFragment.newInstance(suspect, dialogHandler)
            insertFragment(detailsFragment)
        }
    }

    fun openAccusationResult(suspect: Suspect) {
        GlobalScope.launch(Dispatchers.Main) {
            gameModels.gameSolution.map { card -> card.name }.apply {
                if (!contains(suspect.room) || !contains(suspect.tool) || !contains(suspect.suspect)) {
                    dialogHandler.setWaitingQueueSize(gameModels.playerList.size - 1)
                }
            }

            val fragment = EndOfGameFragment.newInstance(suspect, dialogHandler)
            insertFragment(fragment)
        }
    }

    suspend fun quitDuringGame() {
        RetrofitInstance.getInstance(context).cluedo.apply {
            notifyPlayerLeaves(channelName, player.card.name)
        }
    }
}