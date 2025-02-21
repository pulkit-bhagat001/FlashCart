package com.example.flash.ui
import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@Composable
fun NumberScreen(flashViewModel: FlashViewModel,callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
    val context= LocalContext.current
    val phoneNumber by flashViewModel.phoneNumber.collectAsState()
    Text(text = "LOGIN",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
    Text(text = "Enter your phone number",
        fontSize = 20.sp,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center

        )
    Text(text = "This phone number will be used for the purpose of all communication. You shall receive an SMS with a code for verification",
        fontSize = 12.sp,
        color = Color(105,103,100)

    )                                                                                                                 // here n is the new text entered by the user
    TextField(value =phoneNumber , keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), onValueChange ={n: String ->flashViewModel.setPhoneNumber(n)},label={Text(text="Your Number")},
        modifier = Modifier.fillMaxWidth(), singleLine = true)
    Button(onClick = {val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber("+91${phoneNumber}") // Phone number to verify
        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
        .setActivity(context as Activity) // Activity (for callback binding)
        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        if(phoneNumber.length==10) {
            flashViewModel.setLoading(true)
        }}, modifier = Modifier.fillMaxWidth()) {
        Text(text = "Send OTP")

    }
}//this text field will take only single line input