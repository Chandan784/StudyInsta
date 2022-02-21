package com.studyinsta.studyinsta.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.classes.notificaion.LocalDatabase;
import com.studyinsta.studyinsta.classes.notificaion.NotificationModel;
import com.studyinsta.studyinsta.classes.notificaion.NotificatiosAdapter;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class NotificationsFragment extends Fragment {


    public NotificationsFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private NotificatiosAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.notificationsRec);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LocalDatabase db = LocalDatabase.getDbInstance(getContext());
        List<NotificationModel> list = db.notificationDao().getAllNotificaitons();
        Collections.reverse(list);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        adapter = new NotificatiosAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

}

