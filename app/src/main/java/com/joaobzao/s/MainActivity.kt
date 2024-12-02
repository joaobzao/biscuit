package com.joaobzao.s

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.joaobzao.s.ui.theme.BirthdayQuizAppTheme
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BirthdayQuizAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    JourneyQuizScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun JourneyQuizScreen() {
    val questions = listOf(
        "In a court where love first hit the net, round and small, with spins we met. Bouncing back with every sway, What am I that led to this day?",
        "The first neck kiss,\n" +
                "Sweaty and sweet, with a cereal treat.\n" +
                "I have wheels and I'm always outside. \n" +
                "What am I it's up to you to decide",
        "A testament to effort, honest and true,\n" +
                "A hidden gem, much like my Biscuit, you 🧡." +
                "\n" +
                "What am I?"
    )
    val answers = listOf(
        "ball",
        "car",
        "true star"
    )
    val hints = listOf(
        "You can find me in the word 'football'",
        "I was once scratched but now I'm painted like new.",
        "JZ recognition, Biscuit 🍪 version"
    )
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf(TextFieldValue("")) }
    var feedback by remember { mutableStateOf("") }
    var showQuestion by remember { mutableStateOf(true) }
    var showHint by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var showFinalScreen by remember { mutableStateOf(false) }

    val onSubmitAnswer: () -> Unit = {
        if (userAnswer.text.equals(answers[currentQuestionIndex], ignoreCase = true)) {
            feedback = "Correct! On to the next 🎅🎁..."
            score += 1
            showQuestion = false
            showHint = false
        } else {
            feedback = "Oops! Kiss me to try again."
            showQuestion = false
            showHint = true
        }
    }

    val onNextQuestion: () -> Unit = {
        if (currentQuestionIndex + 1 < questions.size) {
            currentQuestionIndex = (currentQuestionIndex + 1) % questions.size
            userAnswer = TextFieldValue("") // Reset answer
            feedback = ""
            showQuestion = true
        } else {
            showFinalScreen = true
        }
    }

    if (showFinalScreen) {
        FinalScreen(score = score, totalQuestions = questions.size)
    } else {
        AnimatedContent(
            targetState = showQuestion,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) with fadeOut(animationSpec = tween(500))
            }
        ) { targetState ->
            if (targetState) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val progress = currentQuestionIndex / questions.size.toFloat()
                    val cookieCount = (progress * 10).toInt() // Change this to adjust number of cookies displayed
                    Text(
                        text = "My biscuit ${"🍪".repeat(currentQuestionIndex + 1)}",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    QuizQuestion(
                        question = questions[currentQuestionIndex],
                        userAnswer = userAnswer,
                        onAnswerChange = { userAnswer = it },
                        onSubmitAnswer = onSubmitAnswer
                    )
                }
            } else {
                val drawableId = listOf(R.drawable.xmas1, R.drawable.xmas2, R.drawable.xmas3)

                QuizFeedback(
                    feedback = feedback,
                    showHint = showHint,
                    hint = hints[currentQuestionIndex],
                    drawableId = drawableId[currentQuestionIndex],
                    onRetryQuestion = {
                        userAnswer = TextFieldValue("") // Reset answer
                        feedback = ""
                        showQuestion = true
                    },
                    onNextQuestion = onNextQuestion
                )
            }
        }
    }
}

@Composable
fun QuizQuestion(
    question: String,
    userAnswer: TextFieldValue,
    onAnswerChange: (TextFieldValue) -> Unit,
    onSubmitAnswer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding(), // To make sure the keyboard doesn't cover the content
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = userAnswer,
            onValueChange = onAnswerChange,
            label = { Text("Your Answer") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = onSubmitAnswer,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.weight(1f)) // Spacer to push the button to the bottom
    }
}

@Composable
fun QuizFeedback(
    feedback: String,
    showHint: Boolean,
    hint: String,
    drawableId: Int,
    onRetryQuestion: () -> Unit,
    onNextQuestion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding(), // To make sure the keyboard doesn't cover the content
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = feedback,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (showHint) {
            Text(
                text = "Hint: $hint",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (feedback.startsWith("Correct")) {

            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "Celebration",
                modifier = Modifier
                    .size(500.dp)
                    .padding(16.dp)
            )
        }

        Button(
            onClick = {
                if (feedback.startsWith("Correct")) {
                    onNextQuestion()
                } else {
                    onRetryQuestion()
                }
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(if (feedback.startsWith("Correct")) "Next" else "Retry")
        }

        Spacer(modifier = Modifier.weight(1f)) // Spacer to push the button to the bottom
    }
}

@Composable
fun FinalScreen(score: Int, totalQuestions: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Adoro-te my biscuit 🍪!!",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFFFF6F00),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Every mile's right n' light",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.sjzxmas),
            contentDescription = "Celebration",
            modifier = Modifier
                .size(500.dp)
                .padding(16.dp)
        )
    }
}
