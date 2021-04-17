package com.moomen.graduationproject.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moomen.graduationproject.R;
import com.moomen.graduationproject.adapter.AdsPagerAdapter;
import com.moomen.graduationproject.adapter.CategoryAdapter;
import com.moomen.graduationproject.adapter.ServicesAdapter;
import com.moomen.graduationproject.model.Ads;
import com.moomen.graduationproject.model.Category;
import com.moomen.graduationproject.model.Service;
import com.moomen.graduationproject.viewModel.HomeViewModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private AdsPagerAdapter adsPagerAdapter;
    private ViewPager viewPager;
    private ArrayList<Ads> adsArrayList = new ArrayList<>();

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    private HomeViewModel homeViewModel;
    private RecyclerView categoryRecyclerView;
    private RecyclerView servicesRecyclerView;
    private Timer timer;
    private LinearLayout linearLayout_dot;
    private int current_position = 0;
    private int custum_position = 0;
    private TabLayout tabLayoutIndicator;
    private String categoryType = "Services";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        categoryRecyclerView = view.findViewById(R.id.recyclerView_category);
        servicesRecyclerView = view.findViewById(R.id.recyclerView_services);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        viewPager = view.findViewById(R.id.viewPager_ads);
        tabLayoutIndicator = view.findViewById(R.id.indicator_tab_slid_page_id);
        getAllCategory();
        getAllAds();
        createSlideshow();
        prepareViewPagerAds();
        getAllServices();
    }

    private void getAllServices() {
        Query query = FirebaseFirestore.getInstance()
                .collection(categoryType)
                .whereEqualTo("status", true);
        FirestoreRecyclerOptions<Service> options = new FirestoreRecyclerOptions.Builder<Service>()
                .setQuery(query, Service.class)
                .build();
        fillServicesRecycleAdapter(options);
    }

    private void fillServicesRecycleAdapter(FirestoreRecyclerOptions<Service> options) {
        ServicesAdapter servicesAdapter = new ServicesAdapter(options);

       /* newsAdapter.onUserNameSetOnClickListener(new NewsAdapter.OnUserNameClickListener() {
            @Override
            public void onUserNameClick(String userID, int position) {
                Intent intent = new Intent(getContext(), OpenUserProfile.class);
                intent.putExtra(USER_ID, userID);
                startActivity(intent);
            }
        });*/

        servicesAdapter.setContext(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        servicesRecyclerView.setLayoutManager(gridLayoutManager);
        servicesRecyclerView.setAdapter(servicesAdapter);
        servicesRecyclerView.setItemViewCacheSize(20);
        servicesRecyclerView.setDrawingCacheEnabled(true);
        servicesRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        servicesAdapter.startListening();
    }

    private void prepareViewPagerAds() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (custum_position > adsArrayList.size() - 1)
                    custum_position = 0;
                //prepareDots(custum_position++);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getAllCategory() {
        Query query = FirebaseFirestore.getInstance().collection("Category").orderBy("name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();
        fillCategoryRecycleAdapter(options);
    }

    private void fillCategoryRecycleAdapter(FirestoreRecyclerOptions<Category> options) {
        CategoryAdapter categoryAdapter = new CategoryAdapter(options);

        categoryAdapter.onItemSetOnClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, int id) {
                Category category = documentSnapshot.toObject(Category.class);
                categoryType = category.getName();
                if (categoryType.equals("All"))
                    categoryType = "Services";
                getAllServices();
            }
        });

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
                    tabLayoutIndicator.setupWithViewPager(viewPager, true);
                }
            }
        });

    }

    private void createSlideshow() {

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (current_position == adsArrayList.size())
                    current_position = 0;
                viewPager.setCurrentItem(current_position++, true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 250, 2500);
    }
}