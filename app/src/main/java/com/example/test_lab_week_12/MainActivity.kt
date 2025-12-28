package com.example.test_lab_week_12

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_12.model.Movie
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.movie_list)

        // Inisialisasi Adapter (diperlukan agar Source 127 'recyclerView.adapter = movieAdapter' bekerja)
        val movieAdapter = MovieAdapter(object : MovieAdapter.MovieClickListener {
            override fun onMovieClick(movie: Movie) {
                // Implementasi klik untuk membuka DetailsActivity (sesuai Source 158)
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

        // Modifikasi: Kode disesuaikan persis dengan Source 137-148 pada modul
        movieViewModel.popularMovies.observe(this) { popularMovies ->
            val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
            movieAdapter.addMovies(
                popularMovies
                    .filter { movie ->
                        movie.releaseDate?.startsWith(currentYear) == true
                    }
                    .sortedByDescending { it.popularity }
            )
        }

        movieViewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                Snackbar.make(recyclerView, error, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}