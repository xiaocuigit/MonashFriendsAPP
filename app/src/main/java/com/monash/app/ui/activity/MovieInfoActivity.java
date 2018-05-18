package com.monash.app.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.monash.app.R;
import com.monash.app.bean.MovieInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieInfoActivity extends AppCompatActivity {

    @BindView(R.id.iv_movie_image) ImageView imageView;
    @BindView(R.id.tv_movie_name) TextView tvName;
    @BindView(R.id.tv_movie_original_name) TextView tvOriginalName;
    @BindView(R.id.tv_movie_score) TextView tvScore;
    @BindView(R.id.tv_movie_starring) TextView tvStarring;
    @BindView(R.id.tv_movie_director) TextView tvDirector;
    @BindView(R.id.tv_movie_year) TextView tvYear;
    @BindView(R.id.tv_movie_summary) TextView tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initData();
    }

    private void init() {
        setContentView(R.layout.activity_movie_info);
        ButterKnife.bind(this);
    }

    private void initData() {
        MovieInfo movieInfo = (MovieInfo) getIntent().getSerializableExtra("movie");
        if (movieInfo != null){
            Glide.with(this)
                    .load(movieInfo.getImageUrl())
                    .into(imageView);
            tvName.setText(movieInfo.getMovieName());
            tvOriginalName.setText(movieInfo.getOriginalName());
            tvScore.setText(movieInfo.getScore());
            tvStarring.setText(movieInfo.getActors());
            tvDirector.setText(movieInfo.getDirectors());
            tvYear.setText(movieInfo.getYear());
            tvSummary.setText(movieInfo.getSummary());
        }
    }

}
