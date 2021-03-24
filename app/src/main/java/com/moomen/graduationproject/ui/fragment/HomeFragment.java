package com.moomen.graduationproject.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.AdsPagerAdapter;
import com.moomen.graduationproject.adapter.CategoryAdapter;
import com.moomen.graduationproject.model.Ads;
import com.moomen.graduationproject.model.Category;
import com.moomen.graduationproject.viewModel.HomeViewModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private AdsPagerAdapter adsPagerAdapter;
    private ViewPager viewPager;
    private ArrayList<Ads> adsArrayList = new ArrayList<>();
    private Timer timer ;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    private HomeViewModel homeViewModel;
    private RecyclerView categoryRecyclerView;

    private int current_position = 0 ;
    private LinearLayout linearLayout_dot;
    private int custum_position = 0 ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        categoryRecyclerView = view.findViewById(R.id.recyclerView);
        linearLayout_dot = view.findViewById(R.id.dot_ads);
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        viewPager = view.findViewById(R.id.viewPager_ads);
        getAllCategory();
        getAllAds();
        createSlideshow();
        prepareDots(custum_position++);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (custum_position > 4)
                    custum_position = 0;
                prepareDots(custum_position++);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void getAllCategory() {
        Query query = FirebaseFirestore.getInstance().collection("Category");
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();
        fillRecycleAdapter(options);
    }

    private void fillRecycleAdapter(FirestoreRecyclerOptions<Category> options) {
        CategoryAdapter categoryAdapter = new CategoryAdapter(options);

       /* newsAdapter.onUserNameSetOnClickListener(new NewsAdapter.OnUserNameClickListener() {
            @Override
            public void onUserNameClick(String userID, int position) {
                Intent intent = new Intent(getContext(), OpenUserProfile.class);
                intent.putExtra(USER_ID, userID);
                startActivity(intent);
            }
        });*/

        categoryAdapter.setContext(getContext());
        categoryAdapter.setAdmin(false);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);
        //recyclerViewCategory.setHasFixedSize(true);
        categoryAdapter.startListening();
    }

    private void getAllAds() {
        firebaseFirestore.collection("Ads").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    adsArrayList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Ads ads = document.toObject(Ads.class);
                        adsArrayList.add(ads);

                    }
                    adsPagerAdapter = new AdsPagerAdapter(getContext(), adsArrayList);
                    viewPager.setAdapter(adsPagerAdapter);
                }
            }
        });

    }

    private void createSlideshow(){

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (current_position == Integer.MAX_VALUE)
                    current_position = 0 ;
                viewPager.setCurrentItem(current_position++ , true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
              handler.post(runnable);
            }
        },250,2500);
    }

    private void prepareDots(int SlidePosition ){

        if (linearLayout_dot.getChildCount()>0)
            linearLayout_dot.removeAllViews();
        ImageView dots[] = new ImageView[5];

        for (int i =0 ; i<5 ; i++){
            dots[i] = new ImageView(getContext());
            if (i== SlidePosition)
                dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.active_dot));
            else
                dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.inactive_dot));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(4,0,4,0);
            linearLayout_dot.addView(dots[i],layoutParams);


        }
    }
}