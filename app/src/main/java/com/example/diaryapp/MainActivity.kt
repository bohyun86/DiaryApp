package com.example.diaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diaryapp.ui.theme.DiaryAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiaryAppTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val userDao = remember { AppDatabase.getDatabase(context).userDao() }
                val contentDao = remember { AppDatabase.getDatabase(context).contentDao() }
                val userRepository = remember { UserRepository(userDao) }
                val contentRepository = remember { ContentRepository(contentDao) }
                val diaryViewModel: DiaryViewModel = viewModel(
                    factory = DiaryViewModelFactory(contentRepository)
                )
                val userRegisterViewModel: UserRegisterViewModel = viewModel(
                    factory = UserRegisterViewModelFactory(userRepository)
                )
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "main_screen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("main_screen") {
                            MainScreen(navController, userRegisterViewModel)
                        }
                        composable("write_diary_screen") {
                            WriteDiaryScreen(navController, diaryViewModel, userRegisterViewModel)
                        }
                        composable("diary_screen") {
                            DiaryScreen(navController, userRegisterViewModel, diaryViewModel)
                        }
                        composable("user_register_screen") {
                            UserRegisterScreen(navController, userRegisterViewModel)
                        }
                        composable("password_reset_screen") {
                            PasswordResetScreen(navController, userRegisterViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    userRegisterViewModel: UserRegisterViewModel
) {
    val id = userRegisterViewModel.id
    val pw = userRegisterViewModel.pw

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(150.dp))
        Text(
            text = stringResource(R.string.diary),
            fontSize = 40.sp,
            fontStyle = FontStyle.Italic,
        )
        Spacer(modifier = Modifier.height(100.dp))
        TextFieldSample1(stringResource(R.string.id_ko), id) {
            userRegisterViewModel.onIdChange(it)
        }
        AsteriskPasswordTextField(stringResource(R.string.pw_ko), pw) {
            userRegisterViewModel.onPwChange(it)
        }
        ButtonSample1(name = "로그인") {
            userRegisterViewModel.login(id, pw) { success ->
                if (success) {
                    navController.navigate("diary_screen")
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        ButtonSample2(name = stringResource(R.string.userEnroll)) {
            navController.navigate("user_register_screen")
        }
        ButtonSample2(name = stringResource(R.string.pwInitialize)) {
            navController.navigate("password_reset_screen")
        }
        Text(
            userRegisterViewModel.errorMessage,
            fontSize = 16.sp,
            color = Color.Red,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp, start = 60.dp, end = 60.dp)
        )
    }
}

@Composable
fun PasswordResetScreen(
    navController: NavHostController,
    userRegisterViewModel: UserRegisterViewModel
) {
    val pw = userRegisterViewModel.pw
    val pw2 = userRegisterViewModel.pw2
    val email = userRegisterViewModel.email
    val isValid = remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            ScreenTitle("비밀번호 초기화")
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back_24),
                contentDescription = "back",
                modifier = Modifier
                    .clickable {
                        navController.navigateUp()
                        userRegisterViewModel.onIdChange("")
                        userRegisterViewModel.onPwChange("")
                        userRegisterViewModel.onPw2Change("")
                        userRegisterViewModel.onEmailChange("")
                    }
                    .padding(end = 32.dp)
                    .size(32.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextFieldSample2(
                "이메일 주소",
                email,
                "(등록하신 이메일 주소를 입력해주세요.)",
                isValid.value,
                userRegisterViewModel
            ) {
                userRegisterViewModel.onEmailChange(it)
            }
            ButtonSample1(name = "확인") {
                userRegisterViewModel.isExitingUserEmail(email) { result ->
                    isValid.value = result
                }
            }
            Text(
                userRegisterViewModel.errorMessage,
                fontSize = 16.sp,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp, start = 40.dp, end = 40.dp)
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 20.dp))

        if (isValid.value) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                ScreenTitle("비밀번호 재설정")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsteriskPasswordTextField(
                    stringResource(R.string.pw_ko),
                    pw,
                    "(길이 6~15자,  !@#$ 및 영문숫자)"
                ) {
                    userRegisterViewModel.onPwChange(it)
                }
                AsteriskPasswordTextField("비밀번호 확인", pw2, "(길이 6~15자,  !@#$ 및 영문숫자)") {
                    userRegisterViewModel.onPw2Change(it)
                }
                ButtonSample1(name = "확인") {
                    userRegisterViewModel.updateUser {
                        if (it) {
                            navController.navigate("main_screen")
                            userRegisterViewModel.onIdChange("")
                            userRegisterViewModel.onPwChange("")
                            userRegisterViewModel.onPw2Change("")
                            userRegisterViewModel.onEmailChange("")
                        }
                    }
                }
                Text(
                    userRegisterViewModel.passwordErrorMsg,
                    fontSize = 16.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp, start = 40.dp, end = 40.dp)
                )
            }
        }
    }
}


@Composable
fun UserRegisterScreen(
    navController: NavHostController,
    userRegisterViewModel: UserRegisterViewModel
) {
    val id = userRegisterViewModel.id
    val pw = userRegisterViewModel.pw
    val pw2 = userRegisterViewModel.pw2
    val email = userRegisterViewModel.email

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            ScreenTitle(title = "사용자 등록")
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back_24),
                contentDescription = "back",
                modifier = Modifier
                    .clickable {
                        navController.navigate("main_screen")
                        userRegisterViewModel.onIdChange("")
                        userRegisterViewModel.onPwChange("")
                        userRegisterViewModel.onPw2Change("")
                        userRegisterViewModel.onEmailChange("")
                    }
                    .padding(end = 32.dp)
                    .size(32.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFieldSample1(
                stringResource(R.string.id_ko),
                id,
                "(길이 8~15자,  !@#$ 및 영문숫자)"
            ) { newId ->
                userRegisterViewModel.onIdChange(newId)
            }
            AsteriskPasswordTextField(
                stringResource(R.string.pw_ko),
                pw,
                "(길이 6~15자,  !@#$ 및 영문숫자)"
            ) { newPw ->
                userRegisterViewModel.onPwChange(newPw)
            }
            AsteriskPasswordTextField("비밀번호 확인", pw2, "(길이 6~15자,  !@#$ 및 영문숫자)") { newPw2 ->
                userRegisterViewModel.onPw2Change(newPw2)
            }
            TextFieldSample1("이메일 주소", email) { newEmail ->
                userRegisterViewModel.onEmailChange(newEmail)
            }
            ButtonSample1(name = "사용자 등록") {
                userRegisterViewModel.registerUser {
                    if (it) {
                        navController.navigate("Main_screen")
                    }
                }
            }
            Text(
                userRegisterViewModel.errorMessage,
                fontSize = 16.sp,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp, start = 40.dp, end = 40.dp)
            )
        }
    }
}

@Composable
fun TextFieldSample1(
    labelName: String,
    value: String,
    placeHolder: String = "",
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = labelName,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold
            )
        },
        placeholder = {
            Text(
                text = placeHolder,
                fontSize = 12.sp
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black
        ),
        modifier = Modifier
            .size(280.dp, 60.dp)
    )
}

@Composable
fun TextFieldSample2(
    labelName: String,
    value: String,
    placeHolder: String = "",
    readOnly: Boolean = false,
    userRegisterViewModel: UserRegisterViewModel = viewModel(),
    onValueChange: (String) -> Unit

) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = labelName,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold
            )
        },
        placeholder = {
            Text(
                text = placeHolder,
                fontSize = 12.sp
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black
        ),
        modifier = Modifier
            .size(280.dp, 60.dp)
            .background(Color(userRegisterViewModel.changeColor(readOnly))),
        enabled = !readOnly,
        singleLine = true
    )
}

@Composable
fun asteriskVisualTransformation(): VisualTransformation {
    return VisualTransformation { text ->
        val transformedText = AnnotatedString(text.text.map { '*' }.joinToString(""))
        TransformedText(transformedText, OffsetMapping.Identity)
    }
}

@Composable
fun AsteriskPasswordTextField(
    label: String,
    value: String,
    placeHolder: String = "",
    onValueChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold
            )
        },
        placeholder = {
            Text(
                placeHolder,
                fontSize = 12.sp
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
        ),
        visualTransformation = if (passwordVisible) VisualTransformation.None else asteriskVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                painterResource(id = R.drawable.baseline_visibility_off_24)
            else
                painterResource(id = R.drawable.baseline_visibility_24)

            IconButton(onClick = {
                passwordVisible = !passwordVisible
            }) {
                Icon(painter = image, contentDescription = "Toggle Password Visibility")
            }
        }
    )
}


@Composable
fun ButtonSample1(name: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .size(width = 280.dp, height = 60.dp)
            .padding(top = 10.dp)
    ) {
        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun ButtonSample2(name: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .size(width = 280.dp, height = 50.dp)
    ) {
        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryScreen(
    navController: NavHostController,
    userRegisterViewModel: UserRegisterViewModel,
    diaryViewModel: DiaryViewModel
) {
    val userId = remember { userRegisterViewModel.id }

    var contents by remember { mutableStateOf(emptyList<Content>()) }

    LaunchedEffect(Unit) {
        diaryViewModel.getContentsByUserId(userId)
        diaryViewModel.contents.collect { newContents ->
            contents = newContents
        }
    }

    val filteredList = contents.filter { it.userId == userId }.reversed()
    val groupedByMonth = filteredList.groupBy { it.contentDate.substring(0, 7) }

    Column {
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groupedByMonth.forEach { (month, entries) ->
                stickyHeader {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${month.substring(0, 4)}년 ${month.substring(5, 7)}월",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                itemsIndexed(entries) { index, entry ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${entry.contentDate.substring(8, 10)}일",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Text(
                                text = entry.contentDetail,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                        if (index < entries.size - 1) {
                            Divider(
                                color = Color.Gray,
                                thickness = 1.dp,
                                modifier = Modifier.padding(start = 57.dp, end = 16.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(0.01f))
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_circle_24),
                    contentDescription = "addContent",
                    modifier = Modifier
                        .clickable { navController.navigate("write_diary_screen") }
                        .size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                TextButton(onClick = { navController.navigate("write_diary_screen") }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "새일기 작성",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 7.dp)
                        )
                        Divider(
                            color = Color.Gray,
                            thickness = 1.dp,
                            modifier = Modifier.size(width = 80.dp, height = 1.dp)
                        )
                    }
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.baseline_menu_24),
                contentDescription = "extraMenu",
                modifier = Modifier
                    .clickable { }
                    .size(48.dp)
                    .align(Alignment.BottomEnd),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun ScreenTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(16.dp),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textDecoration = TextDecoration.Underline
    )
}

@Composable
fun WriteDiaryScreen(
    navController: NavHostController,
    viewModel: DiaryViewModel,
    userRegisterViewModel: UserRegisterViewModel
) {
    var content by remember { mutableStateOf("") }
    val currentDate = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(Date())
    val currentUser = userRegisterViewModel.currentUser?.userId
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {

                    }
                ) {
                    Text(
                        "...",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                TextButton(
                    onClick = {
                        viewModel.insertContent(content, currentUser!!)
                        navController.navigate("diary_screen")
                    }
                ) {
                    Text(
                        "완료",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    DiaryAppTheme {
        val navController = rememberNavController()
        val context = LocalContext.current
        val userDao = remember { AppDatabase.getDatabase(context).userDao() }
        val contentDao = remember { AppDatabase.getDatabase(context).contentDao() }
        val userRepository = remember { UserRepository(userDao) }
        val contentRepository = remember { ContentRepository(contentDao) }
        val diaryViewModel: DiaryViewModel = viewModel(
            factory = DiaryViewModelFactory(contentRepository)
        )
        val userRegisterViewModel: UserRegisterViewModel = viewModel(
            factory = UserRegisterViewModelFactory(userRepository)
        )
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "main_screen",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("main_screen") {
                    MainScreen(navController, userRegisterViewModel)
                }
                composable("write_diary_screen") {
                    WriteDiaryScreen(navController, diaryViewModel, userRegisterViewModel)
                }
                composable("diary_screen") {
                    DiaryScreen(navController, userRegisterViewModel, diaryViewModel)
                }
                composable("user_register_screen") {
                    UserRegisterScreen(navController, userRegisterViewModel)
                }
                composable("password_reset_screen") {
                    PasswordResetScreen(navController, userRegisterViewModel)
                }
            }
        }
    }
}


/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ContentPreview() {
    DiaryAppTheme {
        val navController = rememberNavController()
        val context = LocalContext.current
        val userDao = AppDatabase.getDatabase(context).userDao()
        val contentDao = AppDatabase.getDatabase(context).contentDao()
        val userRepository = UserRepository(userDao)
        val contentRepository = ContentRepository(contentDao)
        val diaryViewModel: DiaryViewModel = viewModel(
            factory = DiaryViewModelFactory(contentRepository)
        )
        val userRegisterViewModel: UserRegisterViewModel = viewModel(
            factory = UserRegisterViewModelFactory(userRepository)
        )
        WriteDiaryScreen(navController, diaryViewModel, userRegisterViewModel)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserRegisterScreenPreview() {
    DiaryAppTheme {
        val navController = rememberNavController()
        val context = LocalContext.current
        val userDao = AppDatabase.getDatabase(context).userDao()
        val contentDao = AppDatabase.getDatabase(context).contentDao()
        val userRepository = UserRepository(userDao)
        val contentRepository = ContentRepository(contentDao)
        val diaryViewModel: DiaryViewModel = viewModel(
            factory = DiaryViewModelFactory(contentRepository)
        )
        val userRegisterViewModel: UserRegisterViewModel = viewModel(
            factory = UserRegisterViewModelFactory(userRepository)
        )
        UserRegisterScreen(navController, userRegisterViewModel)
    }
}

/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PasswordResetScreenPreview() {
    DiaryAppTheme {
        val navController = rememberNavController()
        val context = LocalContext.current
        val userDao = AppDatabase.getDatabase(context).userDao()
        val contentDao = AppDatabase.getDatabase(context).contentDao()
        val userRepository = UserRepository(userDao)
        val contentRepository = ContentRepository(contentDao)
        val diaryViewModel: DiaryViewModel = viewModel(
            factory = DiaryViewModelFactory(userRepository, contentRepository)
        )
        val userRegisterViewModel: UserRegisterViewModel = viewModel(
            factory = UserRegisterViewModelFactory(userRepository)
        )
        PasswordResetScreen(userRegisterViewModel)
    }
}

}*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DiaryScreenPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val userDao = AppDatabase.getDatabase(context).userDao()
    val contentDao = AppDatabase.getDatabase(context).contentDao()
    val userRepository = UserRepository(userDao)
    val contentRepository = ContentRepository(contentDao)
    val diaryViewModel: DiaryViewModel = viewModel(
        factory = DiaryViewModelFactory(contentRepository)
    )
    val userRegisterViewModel: UserRegisterViewModel = viewModel(
        factory = UserRegisterViewModelFactory(userRepository)
    )
    DiaryAppTheme {
        DiaryScreen(navController, userRegisterViewModel, diaryViewModel)
    }
}
*/