package neptun.jxy1vz.cluedo.network.call_adapter

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class Simple<R>(private val call: Call<R>) {
    fun run(responseHandler: (R?, Throwable?) -> Unit) {
        // run in the same thread
        try {
            // call and handle response
            val response = call.execute()
            handleResponse(response, responseHandler)

        } catch (t: IOException) {
            responseHandler(null, t)
        }
    }

    fun process(responseHandler: (R?, Throwable?) -> Unit) {
        // define callback
        val callback = object : Callback<R> {

            override fun onFailure(call: Call<R>?, t: Throwable?) =
                responseHandler(null, t)

            override fun onResponse(call: Call<R>?, r: Response<R>?) =
                handleResponse(r, responseHandler)
        }

        // enqueue network call
        call.enqueue(callback)
    }

    private fun handleResponse(response: Response<R>?, handler: (R?, Throwable?) -> Unit) {
        response?.let {
            if (response.isSuccessful) {
                handler(response.body(), null)
                println(response.body())
            }
            else {
                println("${response.code()}: ${response.message()}")
                if (response.code() in 400..511)
                    handler(null, HttpException(response))
                else
                    handler(response.body(), null)
            }
        }
    }
}