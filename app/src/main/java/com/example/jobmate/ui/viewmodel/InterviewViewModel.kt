package com.example.jobmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobmate.data.models.InterviewDTO
import com.example.jobmate.data.repository.InterviewRepository
import com.example.jobmate.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterviewViewModel @Inject constructor(
    private val repository: InterviewRepository
) : ViewModel() {

    private val _interviews = MutableStateFlow<Resource<List<InterviewDTO>>>(Resource.Empty())
    val interviews: StateFlow<Resource<List<InterviewDTO>>> = _interviews

    fun fetchInterviews() {
        viewModelScope.launch {
            repository.getMyInterviews().collect {
                _interviews.value = it
            }
        }
    }
}
