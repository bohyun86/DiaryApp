package com.example.diaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.MutableState
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
                val viewModel: DiaryViewModel = viewModel(
                    factory = DiaryViewModelFactory(userRepository, contentRepository)
                )
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier) {

    val id = remember { mutableStateOf("") }
    val pw = remember { mutableStateOf("") }

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
        TextFieldSample1(stringResource(R.string.id_ko), id)
        TextFieldSample1(stringResource(R.string.pw_ko), pw)
        ButtonSample1(name = "로그인")
        Spacer(modifier = Modifier.height(10.dp))
        ButtonSample2(name = stringResource(R.string.userEnroll))
        ButtonSample2(name = stringResource(R.string.pwInitialize))
    }
}

@Composable
fun TextFieldSample1(lableName: String, name: MutableState<String>, placeHolder: String = "") {
    OutlinedTextField(
        value = name.value,
        onValueChange = { name.value = it },
        label = {
            Text(
                lableName,
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
fun ButtonSample1(name: String) {
    Button(
        onClick = { /*TODO*/ },
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
fun UserRegisterScreen() {

    val id = remember { mutableStateOf("") }
    val pw = remember { mutableStateOf("") }
    val pw2 = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            ScreenTitle(title = "사용자 등록")
            Icon(painter = painterResource(R.drawable.baseline_arrow_back_24),
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
            TextFieldSample1(stringResource(R.string.id_ko), id, "(길이 8~15자,  !@#\$ 및 영문숫자)")
            TextFieldSample1(stringResource(R.string.pw_ko), pw, "(길이 6~15자,  !@#\$ 및 영문숫자)")
            TextFieldSample1("비밀번호 확인", pw2, "(길이 6~15자,  !@#\$ 및 영문숫자)")
            TextFieldSample1("이메일 주소", email)
            ButtonSample1(name = "사용자 등록")
        }
    }
}

@Composable
fun PasswordResetScreen() {
    val pw = remember { mutableStateOf("") }
    val pw2 = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
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
            TextFieldSample1("이메일 주소", email, "(등록하신 이메일 주소를 입력해주세요.)")
            ButtonSample1(name = "확인")
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
                TextFieldSample1(stringResource(R.string.pw_ko), pw, "(길이 6~15자,  !@#\$ 및 영문숫자)")
                TextFieldSample1("비밀번호 확인", pw2, "(길이 6~15자,  !@#\$ 및 영문숫자)")
                ButtonSample1(name = "확인")
            }
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
            SampleData(9, "2024-05-09", "소고기를 먹고 회도 먹고 맛있는거", "qhgusdlsp")
        )
    }

    val filteredList = contentList.filter { it.userId == "qhgusdlsp" }.reversed()
    val groupedByMonth = filteredList.groupBy { it.date.substring(0, 7) }

    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)

    ) {
        groupedByMonth.forEach { (month, entries) ->
            stickyHeader {
                Row(verticalAlignment = Alignment.CenterVertically,
                   modifier = Modifier
                       .fillMaxWidth()
                       .background(color = MaterialTheme.colorScheme.primary)
                       .clip(RoundedCornerShape(8.dp))) {
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
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    // Divider 추가
                    if (index < entries.size - 1) {
                        Divider(color = Color.Gray, thickness = 1.dp)
                    }
                }
            }
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


/*@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    DiaryAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            MainScreen(Modifier.padding(innerPadding))
        }
    }
}

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
        UserRegisterScreen()
    }
}
*/

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PasswordResetScreenPreview() {
    DiaryAppTheme {

        PasswordResetScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DiaryScreenPreview() {
    DiaryAppTheme {
        DiaryScreen()
    }
}

