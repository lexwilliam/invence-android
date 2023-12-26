package com.lexwilliam.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.lexwilliam.auth.route.login.LoginRoute
import com.lexwilliam.auth.route.signup.SignUpRoute
import com.lexwilliam.core.navigation.Screen

fun NavGraphBuilder.signUpNavigation(

) {
    composable(route = Screen.SIGNUP) {
        SignUpRoute()
    }
}