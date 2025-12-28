package com.example.test_lab_week_12

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_12.model.Movie
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.movie_list)

        val movieAdapter = MovieAdapter(object : MovieAdapter.MovieClickListener {
            override fun onMovieClick(movie: Movie) {
                val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.EXTRA_TITLE, movie.title)
                intent.putExtra(DetailsActivity.EXTRA_RELEASE, movie.releaseDate)
                intent.putExtra(DetailsActivity.EXTRA_OVERVIEW, movie.overview)
                intent.putExtra(DetailsActivity.EXTRA_POSTER, movie.posterPath)
                startActivity(intent)
            }
        })

        recyclerView.adapter = movieAdapter

        val movieRepository = (application as MovieApplication).movieRepository
        val movieViewModel = ViewModelProvider(
            this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MovieViewModel(movieRepository) as T
                }
            })[MovieViewModel::class.java]

        // fetch movies from the API
        // LifecycleScope is a lifecycle-aware coroutine scope
        lifecycleScope.launch {
            // repeatOnLifecycle is a Lifecycle-aware coroutine builder
            // Lifecycle.State.STARTED means that the coroutine will run
            // when the activity is started
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    // collect the list of movies from the StateFlow
                    movieViewModel.popularMovies.collect { movies ->
                        // add the List of movies to the adapter
                        movieAdapter.addMovies(movies)
                    }
                }
                launch {
                    // collect the error message from the StateFlow
                    movieViewModel.error.collect { error ->
                        // if an error occurs, show a Snackbar with the error
                        if (error.isNotEmpty()) {
                            Snackbar.make(
                                recyclerView, error, Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
}