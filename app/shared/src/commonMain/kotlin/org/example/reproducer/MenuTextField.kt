package org.example.reproducer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    inputTransformation: InputTransformation? = null,
    onAction: () -> Unit = {},
    label: @Composable () -> Unit = {},
    readOnly: Boolean = false,
    secure: Boolean = false,
    error: String? = null,
    textObfuscationMode: TextObfuscationMode = TextObfuscationMode.RevealLastTyped,
    keyboardType: KeyboardType = if (secure) KeyboardType.Password else KeyboardType.Unspecified,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderWidth by animateDpAsState(if (isFocused) 2.dp else 1.dp)
    val borderColor by animateColorAsState(
        when {
            error != null -> MaterialTheme.colors.error
            isFocused -> MaterialTheme.colors.primary
            else -> MaterialTheme.colors.onSurface.copy(alpha = .5f)
        }
    )
    val contentColor = LocalContentColor.current.copy(LocalContentAlpha.current)

    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .height(48.dp)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 2.dp)
        ) {
            leadingContent?.invoke()
            val decorator = TextFieldDecorator {
                if (state.text.isEmpty())
                    CompositionLocalProvider(
                        value = LocalTextStyle provides LocalTextStyle.current.copy(
                            color = LocalContentColor.current.copy(alpha = 0.38f)
                        ),
                        content = label
                    )
                it()
            }
            val textStyle = LocalTextStyle.current.copy(color = contentColor)
            val onKeyboardAction = KeyboardActionHandler {
                onAction()
                it()
            }
            if (secure) {
                BasicSecureTextField(
                    state = state,
                    readOnly = readOnly,
                    decorator = decorator,
                    textStyle = textStyle,
                    cursorBrush = SolidColor(MaterialTheme.colors.secondary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = keyboardType),
                    onKeyboardAction = onKeyboardAction,
                    interactionSource = interactionSource,
                    inputTransformation = inputTransformation,
                    textObfuscationMode = textObfuscationMode,
                    modifier = Modifier.weight(1f)
                )
            } else {
                BasicTextField(
                    state = state,
                    readOnly = readOnly,
                    decorator = decorator,
                    textStyle = textStyle,
                    cursorBrush = SolidColor(MaterialTheme.colors.secondary),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = keyboardType),
                    onKeyboardAction = onKeyboardAction,
                    interactionSource = interactionSource,
                    inputTransformation = inputTransformation,
                    modifier = Modifier.weight(1f)
                )
            }
            trailingContent?.invoke()
        }
        AnimatedVisibility(error != null) {
            val error by rememberLatestNonNull(error)
            Text(
                text = error!!,
                color = MaterialTheme.colors.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun <T> rememberLatest(
    value: T,
    condition: (T) -> Boolean = { true }
): State<T> {
    var lastValidValue by remember { mutableStateOf(value) }
    LaunchedEffect(value) {
        if (condition(value))
            lastValidValue = value
    }
    return remember(value) {
        derivedStateOf {
            when {
                condition(value) -> value
                else -> lastValidValue
            }
        }
    }
}

@Composable
fun <T> rememberLatestNonNull(value: T?): State<T?> = rememberLatest(value) { it != null }
