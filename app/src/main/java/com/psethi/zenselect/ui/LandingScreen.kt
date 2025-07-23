package com.psethi.zenselect.ui

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.psethi.zenselect.R
import com.psethi.zenselect.manager.ZenManager
import com.psethi.zenselect.ui.vm.LandingViewModel
import kotlinx.coroutines.delay

@Composable
fun LandingScreen() {

    val viewModel: LandingViewModel = viewModel()
    var input by remember { mutableStateOf("") }

    val context = LocalContext.current
    val zenManager = ZenManager(context)

    var timeLeft by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var showTimeLeft by remember { mutableStateOf("") }

    val color = remember { Animatable(Color.Blue) }
    var isAnimating by remember { mutableStateOf(false) }
    var mediaPlayer: MediaPlayer? by remember {
        mutableStateOf(null)
    }

    // LaunchedEffect composable to animate when start button is clicked
    LaunchedEffect(isAnimating) {
        while (isAnimating) {
            // each animation has a duration of 100 ms
            color.animateTo(Color.Red, animationSpec = tween(1000))
            color.animateTo(Color.Green, animationSpec = tween(1000))
            color.animateTo(Color.Blue, animationSpec = tween(1000))
            color.animateTo(Color.Magenta, animationSpec = tween(1000))
            color.animateTo(Color.Cyan, animationSpec = tween(1000))
        }
    }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            zenManager.enableZenMode()
            var timeInSecs = timeLeft * 60
            while (timeInSecs > 0) {
                delay(1000L)
                timeInSecs--
                showTimeLeft = formatTime(timeInSecs)

                //timeLeft = timeInSecs/60
                Log.i("Landing Screen","Time left: $showTimeLeft")
            }
            Log.i("Landing Screen","Time left before disable zen mode: $showTimeLeft")
            zenManager.disableZenMode()
            isRunning = false
            isAnimating = false
        }
    }
    if(!isRunning) {
        if(mediaPlayer?.isPlaying == true) {
            Log.i("Landing Screen","Time left before stop media player: $showTimeLeft")
            mediaPlayer!!.release()
            mediaPlayer = null
            input = ""
        }

    }
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(color.value),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment =  Alignment.CenterHorizontally) {

        Text(
            text = "Welcome to Zen Select",
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Once started, All incoming calls/notifications will be silent temporarily and you will not be to cancel zen mode",
            color = Color.White,
            modifier = Modifier
                .padding(16.dp)
        )

        if(isRunning) {
            //val seconds = timeLeft * 60
            Text(
                text = "Time left: $showTimeLeft",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }else {
            //input = ""
            //mediaPlayer.reset()
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedBorderColor = Color.White

                ),
                label = {
                    Text(
                        text = "Minutes",
                        color = Color.White)
                },
                placeholder = {
                    Text(
                        text = "How long should zen mode continue?(in mins)",
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )

            )

            OutlinedButton(
                onClick = {
                    isRunning = true
                    isAnimating = true
                    timeLeft = input.toInt()
                    mediaPlayer = setupMediaPlayer(context = context)

                },
                modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Start",
                    color = Color.White)
            }
        }


    }

}

private fun setupMediaPlayer(context: Context): MediaPlayer {
    val mediaPlayer = MediaPlayer.create(context, R.raw.calm_music).apply {
        start()
        isLooping = true
    }
    return mediaPlayer
}


fun formatTime(seconds: Int): String {
    val (mins, secs) = secondsToMinutesAndSeconds(seconds)
    return "%02d:%02d".format(mins, secs)
}

fun secondsToMinutesAndSeconds(seconds: Int): Pair<Int, Int> {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return Pair(minutes, remainingSeconds)
}

fun requestDndPermission(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (!notificationManager.isNotificationPolicyAccessGranted) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        context.startActivity(intent)
    }
}

fun setZenMode(enabled: Boolean, context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (notificationManager.isNotificationPolicyAccessGranted) {
        notificationManager.setInterruptionFilter(
            if (enabled) NotificationManager.INTERRUPTION_FILTER_NONE
            else NotificationManager.INTERRUPTION_FILTER_ALL
        )
    } else {
        requestDndPermission(context)
    }
}


@Preview
@Composable
fun PreviewScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        LandingScreen()
    }
}