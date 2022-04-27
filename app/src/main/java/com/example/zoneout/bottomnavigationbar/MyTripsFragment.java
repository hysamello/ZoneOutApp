package com.example.zoneout.bottomnavigationbar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zoneout.MainActivity;
import com.example.zoneout.PostListAdapter;
import com.example.zoneout.R;
import com.example.zoneout.model.Post;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MyTripsFragment extends Fragment implements PostListAdapter.ItemClickListener {

    private PostListAdapter postsAdapter;
    private ArrayList<Post> posts;
    private Bundle bundle;

    private MutableLiveData<List<Post>> liveData;

    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        liveData = new MutableLiveData<>();

        posts = new ArrayList<>();
        loadPosts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*try {
            Thread.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        if(view == null) {
            this.view = inflater.inflate(R.layout.fragment_recommended, container, false);
            getActivity().setTheme(R.style.Theme_ZoneOut);

            liveData.observe(getViewLifecycleOwner(),posts1 -> {

                posts.sort((p1,p2) ->
                        p1.getRating());

                RecyclerView recyclerView = view.findViewById(R.id.rvPosts);
                recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
                postsAdapter = new PostListAdapter(this.getContext(), posts);
                postsAdapter.setClickListener(this);
                recyclerView.setAdapter(postsAdapter);
            });
        }

        setupToolBar();

        return view;
    }

    private void getPostFromDatabase(Map title) {
        String postName = (String) title.get("title");
        String desc = (String) title.get("description");
        String owner = (String) title.get("owner");
        int rating = Math.toIntExact((long)title.get("rating"));

        Post post = new Post(desc, owner, rating, postName);
        posts.add(post);
    }

    public void loadPosts(){
        MainActivity.reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        if(snap.getChildren().iterator().next().getValue().equals(MainActivity.auth.getCurrentUser().getEmail())){
                            for (DataSnapshot s : snap.getChildren()) {
                                if (s.getKey().equals("name")) {
                                    String username = s.getValue().toString();
                                    DatabaseReference postsDatabase = MainActivity.reference.child("Users").child(username).child("trips");
                                    postsDatabase.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                            GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                                            Map<String,Object> title = snapshot.getValue(genericTypeIndicator);
                                            getPostFromDatabase(title);

                                            liveData.setValue(posts);
                                        }

                                        @Override
                                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                        }

                                        @Override
                                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        String postName = posts.get(position).getTitle();
        String owner = posts.get(position).getOwner();
        String description = posts.get(position).getDescription();
        ArrayList<Integer> images = posts.get(position).getImages();

        this.bundle = new Bundle();

        bundle.putString("name",postName);
        bundle.putString("owner",owner);
        bundle.putString("description",description);
        bundle.putIntegerArrayList("images", images);

        this.setArguments(bundle);

        NavController navController = Navigation.findNavController(view);

        navController.navigate(R.id.action_navigation_MyTrips_to_navigation_post, bundle);
    }

    private void setupToolBar(){
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
}