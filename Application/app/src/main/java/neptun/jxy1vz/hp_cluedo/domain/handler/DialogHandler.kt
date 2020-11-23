package neptun.jxy1vz.hp_cluedo.domain.handler

import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.domain.model.Player
import neptun.jxy1vz.hp_cluedo.domain.model.Suspect
import neptun.jxy1vz.hp_cluedo.domain.model.card.DarkCard
import neptun.jxy1vz.hp_cluedo.domain.util.removePlayer
import neptun.jxy1vz.hp_cluedo.network.pusher.PusherInstance
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.activityListener
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.channelName
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.finishedCardCheck
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.gameModels
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.isGameAbleToContinue
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.isGameModeMulti
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.isGameRunning
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.mPlayerId
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.pause
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.player
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.playerHandler
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.playerInTurn
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.playerInTurnDied
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.retrofit
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel.Companion.unusedMysteryCards
import neptun.jxy1vz.hp_cluedo.ui.fragment.accusation.AccusationFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.cards.mystery.UnusedMysteryCardsFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.endgame.EndOfGameFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.note.NoteFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.notes_or_dice.NotesOrDiceFragment
import neptun.jxy1vz.hp_cluedo.ui.fragment.player_dies.PlayerDiesOrLeavesFragment

class DialogHandler(private val map: MapViewModel.Companion) : DialogDismiss {
    private var waitForPlayers = 0

    fun subscribeToEvent() {
        PusherInstance.getInstance().getPresenceChannel(channelName)
            .bind("note-closed", object : PresenceChannelEventListener {
                override fun onEvent(channelName: String?, eventName: String?, message: String?) {
                    waitForPlayers--
                    if (waitForPlayers == 0)
                        moveToNextPlayer()
                }

                override fun onSubscriptionSucceeded(p0: String?) {}
                override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
                override fun onUsersInformationReceived(p0: String?, p1: MutableSet<User>?) {}
                override fun userSubscribed(p0: String?, p1: User?) {}
                override fun userUnsubscribed(p0: String?, p1: User?) {}
            })
    }

    fun setWaitingQueueSize(size: Int) {
        waitForPlayers = size
    }

    private fun moveToNextPlayer() {
        GlobalScope.launch(Dispatchers.Main) {
            map.gameSequenceHandler.moveToNextPlayer()
        }
    }

    override fun onIncriminationDetailsDismiss() {
        val fragment = NoteFragment.newInstance(player, this)
        map.insertFragment(fragment)
    }

    override fun onCardRevealDismiss() {
        map.enableScrolling()
        map.uiHandler.emptySelectionList()
        if (isGameModeMulti())
            setWaitingQueueSize(1)
        val fragment = NoteFragment.newInstance(player, this)
        map.insertFragment(fragment)
    }

    override fun onDarkCardDismiss(card: DarkCard?) {
        map.enableScrolling()

        finishedCardCheck = true
        isGameAbleToContinue = true
        if (playerInTurnDied)
            map.gameSequenceHandler.moveToNextPlayer()
        else
            map.gameSequenceHandler.continueGame()
        playerInTurnDied = false
    }

    override fun onAccusationDismiss(suspect: Suspect) {
        map.enableScrolling()
        val fragment = EndOfGameFragment.newInstance(suspect, this)
        map.insertFragment(fragment)
        isGameRunning = false
    }

    override fun onEndOfGameDismiss() {
        if (!isGameModeMulti() || (isGameModeMulti() && playerInTurn == mPlayerId) || EndOfGameFragment.goodSolution) {
            activityListener.exitToMenu(false)
            map.onDestroy()
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                val player = playerHandler.getPlayerById(playerInTurn)
                val playerName =
                    CluedoDatabase.getInstance(map.mContext!!).playerDao().getPlayers()!!
                        .find { p -> p.characterName == player.card.name }!!.playerName
                val title = "$playerName ${map.mContext!!.resources.getString(R.string.wrong_solution)} ${map.mContext!!.resources.getString(R.string.he_lost_the_game)}"
                withContext(Dispatchers.Main) {
                    removePlayer(player)
                    val playerLeavesFragment = PlayerDiesOrLeavesFragment.newInstance(
                        player,
                        PlayerDiesOrLeavesFragment.ExitScenario.WRONG_SOLUTION,
                        this@DialogHandler,
                        title
                    )
                    map.insertFragment(playerLeavesFragment)
                }
            }
        }
    }

    override fun onPlayerDiesDismiss(player: Player?) {
        if (player == null)
            activityListener.exitToMenu()
        else {
            setWaitingQueueSize(gameModels.playerList.size)
            val fragment = NoteFragment.newInstance(MapViewModel.player, this)
            map.insertFragment(fragment)
        }
    }

    override fun onNoteDismiss() {
        map.enableScrolling()
        if (!isGameAbleToContinue)
            return

        if (isGameModeMulti()) {
            GlobalScope.launch(Dispatchers.IO) {
                retrofit.cluedo.notifyNoteClosed(channelName)
            }
        } else {
            if (pause)
                map.gameSequenceHandler.continueGame()
            else
                map.gameSequenceHandler.moveToNextPlayer()
        }
    }

    override fun onOptionsDismiss(accusation: Boolean) {
        if (accusation) {
            val fragment = AccusationFragment.newInstance(playerInTurn, this)
            map.insertFragment(fragment)
        } else {
            val fragment =
                UnusedMysteryCardsFragment.newInstance(
                    this,
                    unusedMysteryCards
                )
            map.insertFragment(fragment)
        }
    }

    override fun onShowOptionsDismiss(option: NotesOrDiceFragment.Option) {
        when (option) {
            NotesOrDiceFragment.Option.NOTES -> {
                isGameAbleToContinue = false
                val noteFragment = NoteFragment.newInstance(player, this)
                map.insertFragment(noteFragment)
            }
            NotesOrDiceFragment.Option.DICE -> {
                isGameAbleToContinue = true
                map.interactionHandler.rollWithDice(mPlayerId!!)
            }
        }
    }

    override fun onBackPressedDismiss(quit: Boolean) {
        if (quit) {
            activityListener.exitToMenu()
            map.onDestroy()
        }
    }

    override fun onPlayerLeavesDismiss(setWaitingQueue: Boolean) {
        if (setWaitingQueue)
            setWaitingQueueSize(gameModels.playerList.size)
        val fragment = NoteFragment.newInstance(player, this)
        map.insertFragment(fragment)
    }
}