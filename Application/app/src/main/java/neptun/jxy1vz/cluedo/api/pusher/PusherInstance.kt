package neptun.jxy1vz.cluedo.api.pusher

import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.util.HttpAuthorizer

class PusherInstance {
    companion object {
        private val PUSHER_APP_ID = "1077009"
        private val PUSHER_APP_KEY = "5027f870d695dcedaa00"
        private val PUSHER_APP_SECRET = "2753fceda0424e185446"
        private val PUSHER_APP_CLUSTER = "eu"

        private val SERVER_IP = "127.0.0.1"
        private val PORT = "5000"

        private lateinit var pusher: Pusher

        fun getInstance(): Pusher {
            return if (this::pusher.isInitialized)
                pusher
            else {
                val pusherOptions = PusherOptions().setAuthorizer(getPrivateAuthorizer()).setCluster(PUSHER_APP_CLUSTER)
                pusher = Pusher(PUSHER_APP_KEY, pusherOptions)
                pusher
            }
        }

        fun getPrivateAuthorizer(): HttpAuthorizer {
            return HttpAuthorizer("http://$SERVER_IP:$PORT/pusher/auth/private")
        }

        fun getPresenceAuthorizer(): HttpAuthorizer {
            return HttpAuthorizer("http://$SERVER_IP:$PORT/pusher/auth/presence")
        }
    }
}