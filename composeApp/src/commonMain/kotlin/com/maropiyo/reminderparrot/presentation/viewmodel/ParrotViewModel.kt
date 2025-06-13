package com.maropiyo.reminderparrot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maropiyo.reminderparrot.domain.usecase.GetParrotUseCase
import com.maropiyo.reminderparrot.presentation.state.ParrotState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * インコのビューモデル
 *
 * @property getParrotUseCase インコ取得ユースケース
 */
class ParrotViewModel(
    private val getParrotUseCase: GetParrotUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ParrotState())
    val state: StateFlow<ParrotState> = _state.asStateFlow()

    init {
        loadParrot()
    }

    /**
     * インコの状態を読み込む
     */
    private fun loadParrot() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getParrotUseCase()
                .onSuccess { parrot ->
                    _state.update { it.copy(parrot = parrot, isLoading = false) }
                }.onFailure { exception ->
                    _state.update { it.copy(error = exception.message, isLoading = false) }
                }
        }
    }
}
