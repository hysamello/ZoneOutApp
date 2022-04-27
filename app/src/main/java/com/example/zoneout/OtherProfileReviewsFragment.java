package com.example.zoneout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zoneout.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OtherProfileReviewsFragment extends Fragment implements PostListAdapter.ItemClickListener {

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

        try {
            Thread.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(view == null) {
            this.view = inflater.inflate(R.layout.fragment_recommended, container, false);

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

    public void loadPosts(){
        String username = getArguments().getString("username");
        MainActivity.reference.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        for (DataSnapshot ss : snap.getChildren()){
                            if (ss.getKey().equals("owner") && ss.getValue().toString().equals(username)){
                                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                                Map<String,Object> title = snap.getValue(genericTypeIndicator);
                                getPostFromDatabase(title);

                                liveData.setValue(posts);
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

    private void getPostFromDatabase(Map title) {
        String postName = (String) title.get("title");
        String desc = (String) title.get("description");
        String owner = (String) title.get("owner");
        //int rating = Math.toIntExact((long)title.get("rating"));

        Post post = new Post(desc, owner, postName);
        posts.add(post);
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

        navController.navigate(R.id.action_otherProfileReviewsFragment_to_navigation_post, bundle);
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
