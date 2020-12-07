package neptun.jxy1vz.hp_cluedo.domain.util

import com.google.common.truth.Truth.assertThat
import neptun.jxy1vz.hp_cluedo.data.network.api.RetrofitInstance
import org.junit.Test
import java.net.Inet4Address

class HelperKtTest {

    @Test
    fun `server must be reachable`() {
        val ipAddress = Inet4Address.getByName(RetrofitInstance.DOMAIN).hostAddress
        val result = isServerReachable(ipAddress)
        assertThat(result).isTrue()
    }

    @Test
    fun `invalid address should return false`() {
        val ipAddress = "192.168.1.1"
        val result = isServerReachable(ipAddress)
        assertThat(result).isFalse()
    }
}