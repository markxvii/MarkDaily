package com.example.marc.elephant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.marc.elephant.R;
import com.example.marc.elephant.util.GsonUtil;
import com.example.marc.elephant.util.HttpUtil;
import com.example.marc.elephant.api.Api;
import com.example.marc.elephant.homepage.Content;
import com.example.marc.elephant.gson.GuoKeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by marc on 17-4-20.
 */

public class GuokeFrag extends Fragment {
    private SwipeRefreshLayout guokrrefresh;
    private RecyclerView guokrRecycler;
    private List<GuoKeList.ResultBean> guokrList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.guoke_frag,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        guokrrefresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.guokrrefresh);
        guokrRecycler = (RecyclerView) getActivity().findViewById(R.id.guokr_recycler_view);
        initData();
        guokrrefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
    }
    private void initData() {
        HttpUtil.SendOkHttpReuqest(Api.GUOKR_ARTICLES, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "加载失败，请重试", Toast.LENGTH_SHORT).show();
                        guokrrefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    final String responseText = response.body().string();
                    guokrList = GsonUtil.GuokeUtil(responseText).getResult();
                    if (guokrList!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                guokrRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
                                guokrRecycler.setNestedScrollingEnabled(false);
                                guokrRecycler.setFocusable(false);
                                guokrRecycler.setAdapter(new GuokrRecyclerAdapter(guokrList));
                                guokrrefresh.setRefreshing(false);
                            }
                        });
                    }
                }
            }
        });
    }
    private class GuokrRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<GuoKeList.ResultBean> guokrLists = new ArrayList<>();

        public GuokrRecyclerAdapter(List<GuoKeList.ResultBean> lists) {
            this.guokrLists = lists;
        }

        public class GuokrViewHolder extends RecyclerView.ViewHolder {
            private TextView homeItemTextView;
            private ImageView homeItemImageView;

            public GuokrViewHolder(View itemView) {
                super(itemView);
                homeItemTextView = (TextView) itemView.findViewById(R.id.item_textView);
                homeItemImageView = (ImageView) itemView.findViewById(R.id.item_imageView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GuokrViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final GuoKeList.ResultBean item = guokrLists.get(position);
            if (item.getHeadline_img()!=null) {
                Glide.with(getContext()).load(item.getHeadline_img()).asBitmap().centerCrop().into(((GuokrViewHolder) holder).homeItemImageView);
            }else{
                Glide.with(getContext()).load(R.drawable.elephant).centerCrop().into(((GuokrViewHolder) holder).homeItemImageView);
            }
            ((GuokrViewHolder) holder).homeItemTextView.setText(item.getTitle());
            ((GuokrViewHolder) holder).homeItemImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), Content.class);
                    intent.putExtra("id",item.getLink());
                    intent.putExtra("img",item.getHeadline_img());
                    intent.putExtra("title",item.getTitle());
                    startActivity(intent);
                }
            });
            ((GuokrViewHolder) holder).homeItemTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), Content.class);
                    intent.putExtra("id",item.getLink());
                    intent.putExtra("img",item.getHeadline_img());
                    intent.putExtra("title",item.getTitle());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return guokrLists.size();
        }
    }
}
