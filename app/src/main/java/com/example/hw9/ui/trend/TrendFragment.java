package com.example.hw9.ui.trend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.hw9.R;

public class TrendFragment extends Fragment {

    private TrendViewModel trendViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trendViewModel =
                ViewModelProviders.of(this).get(TrendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_trending, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        trendViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
