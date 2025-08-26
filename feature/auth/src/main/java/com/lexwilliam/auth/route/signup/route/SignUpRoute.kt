package com.lexwilliam.auth.route.signup.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.auth.route.signup.navigation.SignUpNavigationTarget
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel = hiltViewModel(),
    onBackStack: () -> Unit,
    toHome: () -> Unit
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.navigation) {
        when (it) {
            is SignUpNavigationTarget.BackStack -> onBackStack()
            SignUpNavigationTarget.Home -> toHome()
        }
    }

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(onClick = {
                        viewModel.onEvent(SignUpUiEvent.BackClicked)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                    Text(text = "Sign Up", style = InvenceTheme.typography.brand)
                }
                uiState.error?.let { Text(text = it, style = InvenceTheme.typography.bodyMedium) }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Email", style = InvenceTheme.typography.bodyMedium)
                    InvenceOutlineTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.email,
                        onValueChange = {
                            viewModel.onEvent(SignUpUiEvent.EmailChanged(it))
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
                    Text(text = "Username", style = InvenceTheme.typography.bodyMedium)
                    InvenceOutlineTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.name,
                        onValueChange = {
                            viewModel.onEvent(SignUpUiEvent.NameChanged(it))
                        },
                        placeholder = {
                            Text(
                                "Username",
                                style = InvenceTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
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
                        value = uiState.password,
                        onValueChange = {
                            viewModel.onEvent(SignUpUiEvent.PasswordChanged(it))
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
                                when (uiState.isPasswordShowing) {
                                    true -> Icons.Default.Visibility
                                    false -> Icons.Default.VisibilityOff
                                }
                            IconButton(
                                onClick = {
                                    viewModel.onEvent(
                                        SignUpUiEvent.PasswordVisibilityChanged
                                    )
                                }
                            ) {
                                Icon(icon, contentDescription = "visibility icon")
                            }
                        },
                        visualTransformation =
                            when (uiState.isPasswordShowing) {
                                true -> VisualTransformation.None
                                false -> PasswordVisualTransformation()
                            },
                        singleLine = true,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            )
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Confirm Password", style = InvenceTheme.typography.bodyMedium)
                    InvenceOutlineTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.confirmPassword,
                        onValueChange = {
                            viewModel.onEvent(SignUpUiEvent.PasswordConfirmChanged(it))
                        },
                        placeholder = {
                            Text(
                                "Confirm Password",
                                style = InvenceTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "password icon")
                        },
                        trailingIcon = {
                            val icon =
                                when (uiState.isConfirmPasswordShowing) {
                                    true -> Icons.Default.Visibility
                                    false -> Icons.Default.VisibilityOff
                                }
                            IconButton(
                                onClick = {
                                    viewModel.onEvent(
                                        SignUpUiEvent.ConfirmPasswordVisibilityChanged
                                    )
                                }
                            ) {
                                Icon(icon, contentDescription = "visibility icon")
                            }
                        },
                        visualTransformation =
                            when (uiState.isConfirmPasswordShowing) {
                                true -> VisualTransformation.None
                                false -> PasswordVisualTransformation()
                            },
                        singleLine = true,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            )
                    )
                }
            }
            Column {
                InvencePrimaryButton(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    onClick = {
                        viewModel.onEvent(SignUpUiEvent.SignUpClicked)
                    },
                    enabled = uiState.isValid
                ) {
                    Text(text = "Sign Up", style = InvenceTheme.typography.bodyMedium)
                }
            }
        }
    }
}