package by.nguyencongson.quiz_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import by.nguyencongson.quiz_app.common.Common;
import by.nguyencongson.quiz_app.interfaces.IItemClickListener;
import by.nguyencongson.quiz_app.model.Category;
import by.nguyencongson.quiz_app.viewholder.CategoryViewHolder;

public class CategoryFragment extends Fragment {

    private View myFragment;
    // Tên view nên có thêm tiền tố để dễ debug
    private RecyclerView rvListCategory;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference categories;
    private SharedPreferences sharedPreferences;
    private ConstraintLayout backgroundView;

    @NonNull
    public static CategoryFragment newInstance() {
        CategoryFragment categoryFragment = new CategoryFragment();
        return categoryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);// ở đây ạ
        // a vẫn thấy có firebase mà nhỉ

        firebaseDatabase = FirebaseDatabase.getInstance("https://quizapp-7cf64-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        categories = firebaseDatabase.getReference("category");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_category_fargment, container, false);
        rvListCategory = myFragment.findViewById(R.id.listCategory);
        backgroundView=myFragment.findViewById(R.id.background_view);
        sharedPreferences = getContext().getSharedPreferences("THEME", Context.MODE_PRIVATE);
        Common.is_night = sharedPreferences.getBoolean("is_night", false);
        if (Common.is_night == true) {
            Drawable drawable = getResources().getDrawable(R.drawable.background_btn_menu);
            backgroundView.setBackground(drawable);
        }
        else {
            Drawable drawable = getResources().getDrawable(R.drawable.background_banner);
            backgroundView.setBackground(drawable);
        }

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);// em custom laij thif xayr ra looix
        // Chỗ này dùng khi mình biết chắc chắn kích thước của các item trong recycler view
        // thì đặt là true => recycler view k phải tính toán lại kích thước cho các view
        // Dùng StaggeredGridLayoutManager thì các view có kích thước khác nhau => conflict với cái setHasFixedSize
        //rvListCategory.setHasFixedSize(true);// chh
        // để line cũng vẫn lỗi mà e
        // hình như do tốc độ mạng

//        layoutManager = new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false);
        rvListCategory.setLayoutManager(staggeredGridLayoutManager);
        loadCategories();
        return myFragment;
    }


    private void loadCategories() {

        Query query = firebaseDatabase.getReference().child("category");

        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(query, Category.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {

            @Override
            public int getItemCount() {
                Log.d("TESTING", "getItemCount: "+super.getItemCount());
                // Chỗ này nó đang bị delay khi trả về kết quả
//                D/TESTING: getItemCount: 4
                return super.getItemCount();
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_layout, parent, false);

                return new CategoryViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category model) {
                //Trong này chỉ load data của 1 item
                holder.category_name.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.category_image);

                holder.setiItemClickListener(new IItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(getContext(), String.format("%d|%s", position, adapter.getRef(position).getKey()), Toast.LENGTH_LONG).show();
                        Intent startGame = new Intent(getActivity(), StartActivity.class);
                        Common.CategoryId = adapter.getRef(position).getKey();
                        Common.categoryName = model.getName();
                        Log.e("loi1", Common.CategoryId);
                        startGame.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(startGame);
//                        getActivity().finish();
                    }
                });

            }
        };

        //Query params ở đây đang trả ra {} không có data
        rvListCategory.setAdapter(adapter);
        adapter.startListening();// :)))))

        Log.d("TESTING", "loadCategories: "+getActivity().getSupportFragmentManager().getBackStackEntryCount());
    }
}