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
import android.widget.TextView;
import android.widget.Toast;

import com.example.marc.elephant.R;
import com.example.marc.elephant.api.Api;
import com.example.marc.elephant.homepage.Content;
import com.example.marc.elephant.gson.Zhihu2List;
import com.example.marc.elephant.util.GsonUtil;
import com.example.marc.elephant.util.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by marc on 17-4-27.
 */

public class Zhihu2Frag extends Fragment {
    private SwipeRefreshLayout zhihu2refresh;
    private RecyclerView zhihu2RecyclerView;
    private List<Zhihu2List.StoriesBean> lists=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.zhihu2_frag,container,false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        zhihu2refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
    }

    private void initView() {
        zhihu2refresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.zhihu2refresh);
        zhihu2RecyclerView = (RecyclerView) getActivity().findViewById(R.id.zhihu2_recycler_view);
    }
    private void initData() {
        HttpUtil.SendOkHttpReuqest(Api.ZHIHU2_LIST, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "加载失败，请重试", Toast.LENGTH_SHORT).show();
                        zhihu2refresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    final String responseText = response.body().string();
                    lists=GsonUtil.zhihu2Util(responseText).getStories();
                    if (lists!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                zhihu2RecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
                                zhihu2RecyclerView.setNestedScrollingEnabled(false);
                                zhihu2RecyclerView.setFocusable(false);
                                zhihu2RecyclerView.setAdapter(new Zhihu2RecyclerAdapter(lists));
                                zhihu2refresh.setRefreshing(false);
                            }
                        });
                    }
                }
            }
        });
    }
    private class Zhihu2RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Zhihu2List.StoriesBean> lists = new ArrayList<>();

        public Zhihu2RecyclerAdapter(List<Zhihu2List.StoriesBean> lists) {
            this.lists = lists;
        }

        public class NormalViewHolder extends RecyclerView.ViewHolder {
            private TextView homeItemTextView;

            public NormalViewHolder(View itemView) {
                super(itemView);
                homeItemTextView = (TextView) itemView.findViewById(R.id.item_textView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final Zhihu2List.StoriesBean item=lists.get(position);
            ((NormalViewHolder) holder).homeItemTextView.setText(item.getTitle());
            ((NormalViewHolder) holder).homeItemTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), Content.class);
                    intent.putExtra("id2",item.getId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }
    }
}
