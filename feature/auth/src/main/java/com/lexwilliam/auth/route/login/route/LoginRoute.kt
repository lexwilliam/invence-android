package com.lexwilliam.auth.route.login.route

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.identity.Identity
import com.lexwilliam.auth.route.login.navigation.LoginNavigationTarget
import com.lexwilliam.auth.util.GoogleAuthUiClient
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.GoogleSignInButton
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.theme.InvenceTheme
import kotlinx.coroutines.launch

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    toCompanySearch: () -> Unit,
    toHome: () -> Unit,
    toSignUp: () -> Unit,
    toForgotPassword: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val googleAuthUiClient = GoogleAuthUiClient(context, Identity.getSignInClient(context))
    val coroutineScope = rememberCoroutineScope()

    ObserveAsEvents(flow = viewModel.navigation) { target ->
        when (target) {
            LoginNavigationTarget.CompanySearch -> toCompanySearch()
            LoginNavigationTarget.Home -> toHome()
            LoginNavigationTarget.ForgotPassword -> toForgotPassword()
            LoginNavigationTarget.SignUp -> toSignUp()
        }
    }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if (result.resultCode == RESULT_OK) {
                    coroutineScope.launch {
                        val signInResult =
                            googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                        viewModel.onEvent(LoginUiEvent.SignInWithGoogle(signInResult))
                    }
                }
            }
        )

    Scaffold(
        modifier =
            Modifier
                .fillMaxSize(),
        containerColor = InvenceTheme.colors.secondary
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Login", style = InvenceTheme.typography.brand)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Email", style = InvenceTheme.typography.bodyMedium)
                    InvenceOutlineTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.email,
                        onValueChange = {
                            viewModel.onEvent(LoginUiEvent.EmailChanged(it))
                        },
                        placeholder = {
                            Text(
                                "your@email.com",
                                style = InvenceTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "email icon"
                            )
                        },
                        singleLine = true,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Password", style = InvenceTheme.typography.bodyMedium)
                    InvenceOutlineTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.password,
                        onValueChange = {
                            viewModel.onEvent(LoginUiEvent.PasswordChanged(it))
                        },
                        placeholder = {
                            Text(
                                "Password",
                                style = InvenceTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "password icon")
                        },
                        trailingIcon = {
                            val icon =
                                when (state.isPasswordShowing) {
                                    true -> Icons.Default.Visibility
                                    false -> Icons.Default.VisibilityOff
                                }
                            IconButton(
                                onClick = {
                                    viewModel.onEvent(
                                        LoginUiEvent.PasswordVisibilityChanged
                                    )
                                }
                            ) {
                                Icon(icon, contentDescription = "visibility icon")
                            }
                        },
                        visualTransformation =
                            when (state.isPasswordShowing) {
                                true -> VisualTransformation.None
                                false -> PasswordVisualTransformation()
                            },
                        singleLine = true,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                        keyboardActions =
                            KeyboardActions(
                                onDone = {
                                    viewModel.onEvent(LoginUiEvent.SignInClicked)
                                }
                            )
                    )
                }
                Text(
                    modifier =
                        Modifier
                            .align(Alignment.End)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { /*TODO*/ }
                            ),
                    text = "Forgot password?",
                    style = InvenceTheme.typography.bodyMedium
                )
            }
            Column {
                InvencePrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.onEvent(LoginUiEvent.SignInClicked) },
                    enabled = state.isValid
                ) {
                    Text("Sign in", style = InvenceTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(8.dp))
                GoogleSignInButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch
                                ).build()
                            )
                        }
                    }
                )
//                AppleSignInButton(
//                    modifier = Modifier.fillMaxWidth(),
//                    onClick = { }
//                )
                TextButton(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    onClick = {
                        viewModel.onEvent(LoginUiEvent.SignUpClicked)
                    },
                    colors =
                        ButtonDefaults.textButtonColors(
                            contentColor = InvenceTheme.colors.primary
                        )
                ) {
                    Text(
                        text = "Don't have an account? Sign up",
                        style = InvenceTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}