package com.example.ajikapps.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ajikapps.data.remote.RetrofitClient
import kotlinx.coroutines.launch

sealed class NewsState {
    object Loading : NewsState()
    data class Success(val news: List<NewsModel>) : NewsState()
    data class Error(val message: String) : NewsState()
}

class NewsViewModel : ViewModel() {

    private val _newsState = MutableLiveData<NewsState>()
    val newsState: LiveData<NewsState> get() = _newsState

    fun fetchNews() {
        _newsState.value = NewsState.Loading
        viewModelScope.launch {
            try {
                val posts = RetrofitClient.apiService.getPosts()
                val newsList = posts.map { post ->
                    // Custom Unsplash images for news categories
                    val imageList = listOf(
                        "https://images.unsplash.com/photo-1572949645841-094f3a9c4c94?w=600&auto=format&fit=crop&q=80",
                        "https://images.unsplash.com/photo-1506784983877-45594efa4cbe?w=600&auto=format&fit=crop&q=80",
                        "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=600&auto=format&fit=crop&q=80",
                        "https://images.unsplash.com/photo-1472289065668-ce650ac443d2?w=600&auto=format&fit=crop&q=80",
                        "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=600&auto=format&fit=crop&q=80"
                    )
                    val imageUrl = imageList[post.id % imageList.size]
                    
                    // Generate Indonesian day name and month name
                    val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
                    val months = listOf(
                        "Januari", "Februari", "Maret", "April", "Mei", "Juni", 
                        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
                    )
                    
                    val dayName = days[post.id % days.size]
                    val dayNum = (post.id % 28) + 1
                    val monthName = months[(post.id - 1) % months.size]
                    val formattedDate = "$dayName, $dayNum $monthName 2026"

                    NewsModel(
                        id = post.id,
                        title = post.title.replaceFirstChar { it.uppercase() },
                        description = post.body.replace("\n", " ").replaceFirstChar { it.uppercase() },
                        imageUrl = imageUrl,
                        date = formattedDate
                    )
                }
                
                // Limit the number of news items to 20 for optimal rendering
                _newsState.value = NewsState.Success(newsList.take(20))
            } catch (e: Exception) {
                _newsState.value = NewsState.Error(
                    e.localizedMessage ?: "Gagal memuat berita. Periksa koneksi internet Anda."
                )
            }
        }
    }
}
