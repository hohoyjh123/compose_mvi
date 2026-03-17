package kr.jhsh.testcompose.presentation.settings.editnickname

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 닉네임 변경 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNicknameScreen(
    viewModel: EditNicknameViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is EditNicknameEffect.NavigateBack -> {
                    onNavigateBack()
                }

                is EditNicknameEffect.ShowToast -> {
                    // Toast 메시지 처리 (SnackBar 등으로 대체 가능)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.handleIntent(EditNicknameIntent.OnNavigateBack) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로 가기",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = "닉네임 변경",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            //

            //
        }
    ) { paddingValues ->
        EditNicknameFormContent(
            state = state,
            onNewNicknameChanged = { newValue ->
                viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged(newValue))
            },
            onClearNickname = {
                viewModel.handleIntent(EditNicknameIntent.OnClearNickname)
            },
            onSaveNickname = {
                viewModel.handleIntent(EditNicknameIntent.OnSaveNickname)
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun EditNicknameFormContent(
    state: EditNicknameState,
    onNewNicknameChanged: (String) -> Unit,
    onClearNickname: () -> Unit,
    onSaveNickname: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 현재 닉네임 섹션
        Text(
            text = "현재 닉네임",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.currentNickname.ifEmpty { "설정되지 않음" },
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = false,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                disabledContainerColor = Color.Transparent,
                disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                disabledTextColor = if (state.currentNickname.isEmpty()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 새 닉네임 입력 섹션
        Text(
            text = "변경할 닉네임",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.newNickname,
            onValueChange = onNewNicknameChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("새 닉네임을 입력하세요") },
            trailingIcon = {
                if (state.newNickname.isNotEmpty()) {
                    IconButton(onClick = onClearNickname) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "입력 내용 지우기"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            isError = state.validationMessage.isNotEmpty() && !state.isValidationSuccess,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = if (state.isValidationSuccess) {
                    Color.Green
                } else {
                    MaterialTheme.colorScheme.primary
                },
                unfocusedIndicatorColor = if (state.isValidationSuccess) {
                    Color.Green.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )
        )

        // 유효성 검사 메시지
        if (state.validationMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.validationMessage,
                style = MaterialTheme.typography.bodySmall,
                color = if (state.isValidationSuccess) {
                    Color(0xFF2E7D32)
                } else {
                    MaterialTheme.colorScheme.error
                },
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // 규칙 설명
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "• 4~12자의 영문자와 숫자만 사용 가능",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "• 공백 사용 불가",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.weight(1f))

        // 저장 버튼
        Button(
            onClick = onSaveNickname,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isValidationSuccess && !state.isLoading
        ) {
            Text(
                text = if (state.isLoading) "저장 중..." else "저장"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun EditNicknameFormContentPreview() {
    MaterialTheme {
        EditNicknameFormContent(
            state = EditNicknameState(
                currentNickname = "JSONPlaceholder App",
                newNickname = "NewNick123",
                validationMessage = "사용 가능한 닉네임입니다",
                isValidationSuccess = true
            ),
            onNewNicknameChanged = {},
            onClearNickname = {},
            onSaveNickname = {}
        )
    }
}
