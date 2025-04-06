package com.example.planstudy.ui.screens.Books


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


// Data Models
data class BookResponse(
    val works: List<Book>
)

data class Book(
    val key: String,
    val title: String,
    val authors: List<Author>,
    val cover_id: Int?,
    val first_publish_year: Int?,
    val ratings: Ratings?
)

data class Author(
    val key: String,
    val name: String
)

data class Ratings(
    val average: Double?,
    val count: Int?
)

// API Service
interface OpenLibraryService {
    @GET("subjects/fiction.json")
    suspend fun getFictionBooks(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): BookResponse

    companion object {
        const val BASE_URL = "https://openlibrary.org/"

        fun create(): OpenLibraryService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BASIC
                        })
                        .build()
                )
                .build()
            return retrofit.create(OpenLibraryService::class.java)
        }
    }
}

// ViewModel
class BookRecommendationViewModel : ViewModel() {
    private val _books = mutableStateOf<List<Book>>(emptyList())
    val books: List<Book> get() = _books.value

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading.value

    private val _error = mutableStateOf<String?>(null)
    val error: String? get() = _error.value

    private val service = OpenLibraryService.create()

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = service.getFictionBooks()
                _books.value = response.works
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// Composable Screens
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookRecommendationScreen(viewModel: BookRecommendationViewModel = viewModel()) {
    Scaffold(

    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (viewModel.isLoading) {
                LoadingIndicator()
            } else if (viewModel.error != null) {
                ErrorMessage(error = viewModel.error!!, onRetry = { viewModel.loadBooks() })
            } else {
                BookList(books = viewModel.books)
            }
        }
    }
}

@Composable
fun BookList(books: List<Book>) {
    LazyColumn {
        items(books) { book ->
            BookItem(book = book)
            Divider(color = Color.LightGray, thickness = 0.5.dp)
        }
    }
}

@Composable
fun BookItem(book: Book) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Book cover image
        val imageUrl = if (book.cover_id != null) {
            "https://covers.openlibrary.org/b/id/${book.cover_id}-M.jpg"
        } else {
            null
        }

        AsyncImage(
            model = imageUrl,
            contentDescription = "Cover of ${book.title}",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(4.dp)),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (book.authors.isNotEmpty()) {
                Text(
                    text = "by ${book.authors.joinToString { it.name }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (book.ratings?.average != null) {
                    RatingBar(rating = book.ratings.average / 2) // Assuming API rating is out of 5
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "%.1f".format(book.ratings.average),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "No ratings",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (book.first_publish_year != null) {
                Text(
                    text = "Published: ${book.first_publish_year}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun RatingBar(rating: Double, maxStars: Int = 5) {
    val fullStars = rating.toInt()
    val hasHalfStar = rating - fullStars >= 0.5

    Row {
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(16.dp)
            )
        }

        if (hasHalfStar) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(16.dp)
            )
        }

        repeat(maxStars - fullStars - if (hasHalfStar) 1 else 0) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
//        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            Text(
                text = "Error: $error",
//                modifier = Modifier.
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}