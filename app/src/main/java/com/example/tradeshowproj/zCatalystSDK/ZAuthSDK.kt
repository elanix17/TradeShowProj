package com.example.tradeshowproj.zCatalystSDK

import android.content.Context
import android.util.Log
import com.zoho.catalyst.org.ZCatalystUser
import com.zoho.catalyst.setup.ZCatalystApp
import com.zoho.catalyst.setup.ZCatalystSDKConfigs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object ZAuthSDK {
    private lateinit var catalystSDK: ZCatalystApp
    val zApiSDK: ZApiSDK by lazy { ZApiSDK(catalystSDK) }

    private var isInitialized = false

    private val _currentUser: MutableStateFlow<ZCatalystUser?> = MutableStateFlow(null)
    val currentUser = _currentUser.asStateFlow()


    fun initialize(context: Context) {
        catalystSDK = ZCatalystApp.init(
            context = context,
            environment = ZCatalystSDKConfigs.Environment.DEVELOPMENT
        )
        isInitialized = true

    }

    fun initiateUserLogin(
        onLoad: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        Log.d("catalystlogin","login called")
        onLoad()
        catalystSDK.login(
            customParams = hashMapOf(),
            success = {
                configureCurrentUser()
                onSuccess()
                Log.d("catalystlogin","login success")
            }, failure = {
                onFailure()
                Log.d("catalystlogin","login failure")
            }
        )
    }

    fun initiateUserSignUp(
        name: String, email: String,
        onSuccess: (ZCatalystUser) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("catalystlogin","signup called")
        catalystSDK.signUp(
            newUser = catalystSDK.newUser(firstName = name, email = email),
            success = {
                Log.d("catalystlogin","signup success")
                onSuccess(it)
            }, failure = {
                onError(it.getErrorMsg().orEmpty())
                Log.d("catalystlogin","signup failure")
            }
        )

    }

    fun logOutUser(onSuccess: () -> Unit, onError: () -> Unit) {
        catalystSDK.logout(
            success = {
                _currentUser.value = null
                onSuccess()
            }, failure = {
                onError()
            }
        )
    }

    fun isUserSignedIn(): Boolean = isInitialized && catalystSDK.isUserSignedIn()

    suspend fun getCurrentUser(): ZCatalystUser? {
        return suspendCoroutine { cont ->
            if (isUserSignedIn())
                catalystSDK.getCurrentUser(
                    success = { zUser ->
                        _currentUser.value = zUser
                        cont.resume(zUser)
                        Log.e("Test", "User:S")
                    }, failure = {
                        _currentUser.value = null
                        cont.resume(null)
                        Log.e("Test", "User:F")
                    })
            else
                cont.resume(null)
        }
    }

    private fun configureCurrentUser() {
        if (isUserSignedIn())
            catalystSDK.getCurrentUser(
                success = { zUser ->
                    _currentUser.value = zUser
                }, failure = {
                    _currentUser.value = null
                })
        else
            _currentUser.value = null
    }

}