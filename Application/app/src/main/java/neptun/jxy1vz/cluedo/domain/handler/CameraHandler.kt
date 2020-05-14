package neptun.jxy1vz.cluedo.domain.handler

import kotlinx.android.synthetic.main.activity_map.view.*
import neptun.jxy1vz.cluedo.ui.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mContext
import neptun.jxy1vz.cluedo.ui.map.MapViewModel.Companion.mapRoot

class CameraHandler(private val map: MapViewModel.Companion) {
    fun moveCameraToPlayer(playerId: Int) {
        val x =
            map.playerHandler.getPairById(playerId).second.left.toFloat() - mContext!!.resources.displayMetrics.widthPixels / 2
        val y =
            map.playerHandler.getPairById(playerId).second.top.toFloat() - mContext!!.resources.displayMetrics.heightPixels / 2

        mapRoot.panTo(-x, -y, true)
    }

    fun moveCameraToCorner(house: StateMachineHandler.HogwartsHouse) {
        val x = when (house) {
            StateMachineHandler.HogwartsHouse.SLYTHERIN -> mapRoot.mapLayout.ivMap.left.toFloat()
            StateMachineHandler.HogwartsHouse.RAVENCLAW -> mapRoot.mapLayout.ivMap.right.toFloat()
            StateMachineHandler.HogwartsHouse.GRYFFINDOR -> mapRoot.mapLayout.ivMap.right.toFloat()
            else -> mapRoot.mapLayout.ivMap.left.toFloat()
        }
        val y = when (house) {
            StateMachineHandler.HogwartsHouse.SLYTHERIN -> mapRoot.mapLayout.ivMap.top.toFloat()
            StateMachineHandler.HogwartsHouse.RAVENCLAW -> mapRoot.mapLayout.ivMap.top.toFloat()
            StateMachineHandler.HogwartsHouse.GRYFFINDOR -> mapRoot.mapLayout.ivMap.bottom.toFloat()
            else -> mapRoot.mapLayout.ivMap.bottom.toFloat()
        }

        mapRoot.panTo(-x, -y, true)
    }

    fun moveCameraToPosition(top: Float, left: Float) {
        val x = left - mContext!!.resources.displayMetrics.widthPixels / 2
        val y = top - mContext!!.resources.displayMetrics.heightPixels / 2
        mapRoot.panTo(-x, -y, true)
    }
}