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
import com.example.marc.elephant.gson.YigeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by marc on 17-4-20.
 */

public class YigeFrag extends Fragment {
    private SwipeRefreshLayout yigerefresh;
    private RecyclerView yigeRecyclerView;
    private List<YigeList.DataBean> lists=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.yige_frag,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        yigerefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
    }

    private void initView() {
        yigerefresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.yigerefresh);
        yigeRecyclerView = (RecyclerView) getActivity().findViewById(R.id.yige_recycler_view);
    }

    private void initData() {
        HttpUtil.SendOkHttpReuqest(Api.YIGE_TODAY, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "加载失败，请重试", Toast.LENGTH_SHORT).show();
                        yigerefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    final String responseText = response.body().string();
                    lists = GsonUtil.yigeUtil(responseText).getData();
                    if (lists!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                yigeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
                                yigeRecyclerView.setNestedScrollingEnabled(false);
                                yigeRecyclerView.setFocusable(false);
                                yigeRecyclerView.setAdapter(new YigeRecyclerAdapter(lists));
                                yigerefresh.setRefreshing(false);
                            }
                        });
                    }
                }
            }
        });
    }
    private class YigeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<YigeList.DataBean> yigeLists = new ArrayList<>();

        public YigeRecyclerAdapter(List<YigeList.DataBean> lists) {
            this.yigeLists = lists;
        }

        public class YigeViewHolder extends RecyclerView.ViewHolder {
            private TextView homeItemTextView;
            private ImageView homeItemImageView;

            public YigeViewHolder(View itemView) {
                super(itemView);
                homeItemTextView = (TextView) itemView.findViewById(R.id.item_textView);
                homeItemImageView = (ImageView) itemView.findViewById(R.id.item_imageView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new YigeViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final YigeList.DataBean item = yigeLists.get(position);
            if (item.getShare_info().getImage() == null) {
                Glide.with(getContext()).load(R.drawable.elephant).centerCrop().into(((YigeViewHolder) holder).homeItemImageView);
            } else {
                Glide.with(getContext()).load(item.getShare_info().getImage()).asBitmap().centerCrop().into(((YigeViewHolder) holder).homeItemImageView);
            }
            ((YigeViewHolder) holder).homeItemTextView.setText(item.getShare_info().getTitle());
            ((YigeViewHolder) holder).homeItemTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), Content.class);
                    intent.putExtra("yigeId",item.getShare_info().getUrl());
                    intent.putExtra("yigeImg",item.getShare_info().getImage());
                    intent.putExtra("yigeTitle",item.getShare_info().getTitle());
                    startActivity(intent);
                }
            });
            ((YigeViewHolder) holder).homeItemImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), Content.class);
                    intent.putExtra("yigeId",item.getShare_info().getUrl());
                    intent.putExtra("yigeImg",item.getShare_info().getImage());
                    intent.putExtra("yigeTitle",item.getShare_info().getTitle());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return yigeLists.size();
        }
    }

}
