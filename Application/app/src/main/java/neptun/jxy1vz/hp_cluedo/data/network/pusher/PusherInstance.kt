package neptun.jxy1vz.hp_cluedo.data.network.pusher

import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.util.HttpAuthorizer

class PusherInstance {
    companion object {
        private const val PUSHER_APP_KEY = "5027f870d695dcedaa00"
        private const val PUSHER_APP_CLUSTER = "eu"

        private const val SERVER_NAME = "pedro.sch.bme.hu"
        private const val PORT = "443"

        private lateinit var pusher: Pusher

        fun getInstance(): Pusher {
            return if (this::pusher.isInitialized)
                pusher
            else {
                val pusherOptions = PusherOptions().setAuthorizer(getPresenceAuthorizer()).setCluster(PUSHER_APP_CLUSTER)
                pusher = Pusher(PUSHER_APP_KEY, pusherOptions)
                pusher
            }
        }

        private fun getPresenceAuthorizer(): HttpAuthorizer {
            return HttpAuthorizer("https://$SERVER_NAME:$PORT/pusher/auth/presence")
        }
    }
}