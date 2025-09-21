package com.sploiter.xploiter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sploiter.xploiter.ui.theme.button
import com.sploiter.xploiter.ui.theme.pressed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore("details")
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false }
        setContent {
            LoginScreen()
        }
    }
}
@Preview(showBackground = true)
@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val colorStops = arrayOf(
        0.0f to Color(0x8D5E5C5C),
        0.2f to Color(0xC9212121),
        0.5f to Color(0xC60E0E0E),
        0.8f to Color(0xC9212121),
        1f to Color(0x8D5E5C5C)
    )

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("Incorrect Username or Password") }
    var incorrect by remember { mutableFloatStateOf(0.0f) }
    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.login_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.875f)
                .fillMaxHeight(0.4f)
                .align(Alignment.Center)
                .background(
                    Brush.verticalGradient(colorStops = colorStops),
                    RoundedCornerShape(30.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.skull_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.75f) // responsive width
                    .offset(y = (-170).dp)
                    .aspectRatio(390f / 293f)
                    .shadow(
                        elevation = 20.dp,
                        ambientColor = Color.White.copy(alpha = 0.6f),
                        spotColor = Color.Red.copy(alpha = 0.6f),
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LOGIN",
                    style = TextStyle(brush = Brush.linearGradient(listOf(Color.Red, Color.White)),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = error,
                    style = TextStyle(
                        color = Color.Red,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .alpha(incorrect)
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomTextField(
                    id = "username",
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "Username",
                    modifier = Modifier
                        .focusRequester(usernameFocusRequester),
                    onNext = {
                        if (username.isEmpty()) {
                            incorrect = 1f
                            error = "*Required"
                        }
                        else{
                            incorrect = 0.0f
                            error = "Incorrect Username or Password"
                            passwordFocusRequester.requestFocus()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomTextField(
                    id = "password",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    isPassword = true,
                    modifier = Modifier
                        .focusRequester(passwordFocusRequester),
                    onDone = {
                        if (!validateInputs(username, password)) {
                            if (username.isEmpty()) {
                                usernameFocusRequester.requestFocus()
                                error = "*Required"
                                incorrect = 1f
                            }
                            else if(password.isEmpty()){
                                passwordFocusRequester.requestFocus()
                                error = "*Required"
                                incorrect = 1f
                            }
                        } else {
                            login(context, username, password) { success ->
                                if (success) {
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.putExtra("username", username)
                                    context.startActivity(intent)
                                }
                                else {
                                    usernameFocusRequester.requestFocus()
                                    username = ""
                                    password = ""
                                    error = "Incorrect Username or Password"
                                    incorrect = 1f
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Forgot Password?",
                    style = TextStyle(color = Color.White, fontSize = 15.sp),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 10.dp)
                        .clickable { /* handle */ }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(space = 40.dp)) {
                    CustomButton("Login") {
                        if (!validateInputs(username, password)) {
                            if (username.isEmpty()) {
                                usernameFocusRequester.requestFocus()
                                error = "*Required"
                                incorrect = 1f
                            }
                            else if(password.isEmpty()){
                                passwordFocusRequester.requestFocus()
                                error = "*Required"
                                incorrect = 1f
                            }
                        } else {
                            incorrect = 0.0f

                            login(context, username, password) { success ->
                                if (success) {
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.putExtra("username", username)
                                    context.startActivity(intent)
                                }
                                else {
                                    usernameFocusRequester.requestFocus()
                                    username = ""
                                    password = ""
                                    error = "Incorrect Username or Password"
                                    incorrect = 1f
                                }
                            }
                        }
                    }
                    CustomButton("Sign In") {
                        if (!validateInputs(username, password)) {
                            if (username.isEmpty()) {
                                usernameFocusRequester.requestFocus()
                                error = "*Required"
                                incorrect = 1f
                            }
                            else if(password.isEmpty()){
                                passwordFocusRequester.requestFocus()
                                error = "*Required"
                                incorrect = 1f
                            }
                        }
                        else {
                            signIn(context, username, password)
                            val intent = Intent(context, MainActivity::class.java)
                            intent.putExtra("username", username)
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CustomTextField(
    id: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    onNext: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White.copy(alpha = 0.1f),
                    RoundedCornerShape(24.dp)
                )
        )

        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 16.dp)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 20.sp
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = if (id == "username") ImeAction.Next else ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {onDone?.invoke()},
                onNext = {onNext?.invoke()}
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp).then(modifier)
        )
    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Box(
        modifier = Modifier
            .height(if (isPressed) 60.dp else 50.dp)
            .width(if (isPressed) 110.dp else 110.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                color = if (isPressed) pressed else button
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
    }
}

fun validateInputs(username: String, password: String): Boolean {
    return username.isNotEmpty() && password.isNotEmpty()
}

suspend fun saveData(context: Context, username: String, password:String){
    context.dataStore.edit{ prefs ->
        prefs[stringPreferencesKey(username)] = password
    }
}
suspend fun readData(context: Context, username:String):String?{
    val prefs = context.dataStore.data.first()
    return prefs[stringPreferencesKey(username)]

}
fun signIn(context: Context, username: String, password: String){
    CoroutineScope(Dispatchers.IO).launch {
        val storedPassword = readData(context, username)
        if (storedPassword != password){
            saveData(context, username, password)
        }
    }
}

fun login(context:Context, username: String, password: String, onResult: (Boolean) -> Unit){
    CoroutineScope(Dispatchers.IO).launch {
        val storedPassword = readData(context, username)
        val success = storedPassword == password

        CoroutineScope(Dispatchers.Main).launch {
            onResult(success)
        }
    }
}