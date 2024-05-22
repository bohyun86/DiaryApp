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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(Modifier.padding(innerPadding), userRegisterViewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier,
               userRegisterViewModel: UserRegisterViewModel) {

    val id = userRegisterViewModel.id
    val pw = userRegisterViewModel.pw

    Column(
        modifier = modifier.fillMaxSize(),
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
            userRegisterViewModel.id
        }
        TextFieldSample1(stringResource(R.string.pw_ko), pw) {
            userRegisterViewModel.pw
        }
        ButtonSample1(name = "로그인") {
            if (id.isEmpty() || pw.isEmpty()) {
                return@ButtonSample1
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        ButtonSample2(name = stringResource(R.string.userEnroll))
        ButtonSample2(name = stringResource(R.string.pwInitialize))
    }
}

@Composable
fun PasswordResetScreen(userRegisterViewModel: UserRegisterViewModel) {
    val pw = userRegisterViewModel.pw
    val pw2 = userRegisterViewModel.pw2
    val email = userRegisterViewModel.email
    val isValid = remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
    Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            ScreenTitle("비밀번호 초기화")
            Icon(painter = painterResource(R.drawable.baseline_arrow_back_24),
                contentDescription = "back",
                modifier = Modifier
                    .clickable { /*TODO*/ }
                    .padding(end = 32.dp)
                    .size(32.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        )
        {
            TextFieldSample1("이메일 주소", email, "(등록하신 이메일 주소를 입력해주세요.)") {
                userRegisterViewModel.email
            }
            ButtonSample1(name = "확인") {
                isValid.value = userRegisterViewModel.isExitingUserEmail(email)
            }
            Text(userRegisterViewModel.errorMessage,
                fontSize = 20.sp,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp))
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
            )
            {
                TextFieldSample1(stringResource(R.string.pw_ko), pw, "(길이 6~15자,  !@#\$ 및 영문숫자)") {
                    userRegisterViewModel.pw
                }
                TextFieldSample1("비밀번호 확인", pw2, "(길이 6~15자,  !@#\$ 및 영문숫자)") {
                    userRegisterViewModel.pw2
                }
                ButtonSample1(name = "확인") {
                    userRegisterViewModel.updateUser()
                }
                Text(userRegisterViewModel.errorMessage,
                    fontSize = 20.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(10.dp))
            }
        }
    }
}

@Composable
fun TextFieldSample1(
    labelName: String,
    value: String,
    placeHolder: String = "",
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                labelName,
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
        modifier = Modifier.size(280.dp, 60.dp)
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
fun ButtonSample2(name: String) {
    TextButton(
        onClick = { /*TODO*/ },
        modifier = Modifier
            .size(width = 280.dp, height = 50.dp)
    )
    {
        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun UserRegisterScreen(
    userRegisterViewModel: UserRegisterViewModel,
    diaryViewModel: DiaryViewModel
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
                    .clickable { }
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
            TextFieldSample1(stringResource(R.string.id_ko), id, "(길이 8~15자,  !@#$ 및 영문숫자)") { newId ->
                userRegisterViewModel.onIdChange(newId)
            }
            TextFieldSample1(stringResource(R.string.pw_ko), pw, "(길이 6~15자,  !@#$ 및 영문숫자)") { newPw ->
                userRegisterViewModel.onPwChange(newPw)
            }
            TextFieldSample1("비밀번호 확인", pw2, "(길이 6~15자,  !@#$ 및 영문숫자)") { newPw2 ->
                userRegisterViewModel.onPw2Change(newPw2)
            }
            TextFieldSample1("이메일 주소", email) { newEmail ->
                userRegisterViewModel.onEmailChange(newEmail)
            }
            ButtonSample1(name = "사용자 등록") {
                userRegisterViewModel.registerUser()
            }
            Text(userRegisterViewModel.errorMessage,
                fontSize = 20.sp,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp))
        }
    }
}

data class SampleData(
    val contentId: Int,
    val date: String,
    val contentDetail: String,
    val userId: String
)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryScreen() {
    val contentList = remember {
        mutableListOf(
            SampleData(1, "2024-04-01", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp"),
            SampleData(2, "2024-04-04", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp"),
            SampleData(3, "2024-04-05", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp1"),
            SampleData(4, "2024-04-23", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp"),
            SampleData(5, "2024-04-25", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp"),
            SampleData(6, "2024-05-01", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp1"),
            SampleData(7, "2024-05-05", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp"),
            SampleData(8, "2024-05-08", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp"),
            SampleData(9, "2024-05-09", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp"),
            SampleData(10, "2024-05-01", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp1"),
            SampleData(11, "2024-05-05", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp"),
            SampleData(12, "2024-05-08", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp"),
            SampleData(13, "2024-05-09", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp"),
            SampleData(11, "2024-05-05", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp"),
            SampleData(12, "2024-05-08", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp"),
            SampleData(13, "2024-05-09", "소고기를 먹고 회도 먹고 맛있는거 많이 먹어서 너무너무", "qhgusdlsp")
        )
    }

    val filteredList = contentList.filter { it.userId == "qhgusdlsp" }.reversed()
    val groupedByMonth = filteredList.groupBy { it.date.substring(0, 7) }

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
                            .background(color = MaterialTheme.colorScheme.primary)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "${month.substring(0, 4)}년 ${month.substring(5, 7)}월",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                itemsIndexed(entries) { index, entry ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${entry.date.substring(8, 10)}일",
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
                        // Divider 추가
                        if (index < entries.size - 1) {
                            Divider(color = Color.Gray, thickness = 1.dp,
                                modifier = Modifier.padding(start = 57.dp, end = 16.dp))
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
                Icon(painter = painterResource(id = R.drawable.baseline_add_circle_24),
                    contentDescription = "addContent",
                    modifier = Modifier
                        .clickable { }
                        .size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                TextButton(onClick = { /*TODO*/ }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "새일기 작성",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 7.dp)
                        )
                        Divider(
                            color = Color.Gray, thickness = 1.dp,
                            modifier = Modifier.size(width = 80.dp, height = 1.dp)
                        )
                    }
                }
            }
            Icon(painter = painterResource(id = R.drawable.baseline_menu_24),
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
fun WriteDiaryScreen(viewModel: DiaryViewModel) {
    var content by remember { mutableStateOf("") }
    val currentDate = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(Date())

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
                        viewModel.insertContent(content)
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
                        viewModel.insertContent(content)
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
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            MainScreen(Modifier.padding(innerPadding), userRegisterViewModel)
        }
    }
}
/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ContentPreview() {
    DiaryAppTheme {
        val context = LocalContext.current
        val userDao = AppDatabase.getDatabase(context).userDao()
        val contentDao = AppDatabase.getDatabase(context).contentDao()
        val userRepository = UserRepository(userDao)
        val contentRepository = ContentRepository(contentDao)
        val viewModel: DiaryViewModel = viewModel(
            factory = DiaryViewModelFactory(userRepository, contentRepository)
        )
        WriteDiaryScreen(viewModel)
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserRegisterScreenPreview() {
    DiaryAppTheme {
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
        UserRegisterScreen(userRegisterViewModel, diaryViewModel)
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PasswordResetScreenPreview() {
    DiaryAppTheme {
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DiaryScreenPreview() {
    DiaryAppTheme {
        DiaryScreen()
    }
}*/

