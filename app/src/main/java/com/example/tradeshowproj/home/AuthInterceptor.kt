package com.example.tradeshowproj.home

import com.example.tradeshowproj.zCatalystSDK.ZAuthSDK
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(private val token: String) : Interceptor  {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .method(original.method, original.body)

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

//class AuthInterceptor (
//    private val authSDK: ZAuthSDK
//) : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//        val builder = request.newBuilder()
//
//        runBlocking {
//            authSDK.getCurrentUser()
//        }.let {
//            builder.addHeader(
//                "Authorization",
//                String.format("%s %s", "AuthInterceptorConsts.BEARER", it)
//            )
//        }
//        return chain.proceed(builder.build())
//    }
//}
