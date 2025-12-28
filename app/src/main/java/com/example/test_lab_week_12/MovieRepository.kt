package com.example.test_lab_week_12

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.test_lab_week_12.model.Movie

class MovieRepository(private val movieService: MovieService) {
    // API Key Anda tetap dipertahankan
    private val apiKey = "187d47d909dd5c22a8674496eaf62be9"

    // LiveData that contains a List of movies
    // Modifikasi: Mengubah _movieLiveData menjadi movieLiveData sesuai modul
    private val movieLiveData = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>>
        get() = movieLiveData

    // LiveData that contains an error message
    // Modifikasi: Mengubah _errorLiveData menjadi errorLiveData sesuai modul
    private val errorLiveData = MutableLiveData<String>()
    val error: LiveData<String>
        get() = errorLiveData

    // fetch movies from the API
    suspend fun fetchMovies() {
        try {
            // get the List of popular movies from the API
            val popularMovies = movieService.getPopularMovies(apiKey)
            movieLiveData.postValue(popularMovies.results)
        } catch (exception: Exception) {
            // if an error occurs, post the error message to the
            // errorLiveData
            errorLiveData.postValue("An error occurred: ${exception.message}")
        }
    }
}