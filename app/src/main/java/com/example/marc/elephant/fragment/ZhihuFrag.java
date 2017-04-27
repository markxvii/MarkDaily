package com.example.marc.elephant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.example.marc.elephant.api.Api;
import com.example.marc.elephant.homepage.Content;
import com.example.marc.elephant.gson.ZhihuList;
import com.example.marc.elephant.util.GsonUtil;
import com.example.marc.elephant.util.HttpUtil;
import com.stx.xhb.xbanner.XBanner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by marc on 17-4-20.
 */

public class ZhihuFrag extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout zhihurefresh;
    //private RollPagerView roll;
    private List<ZhihuList.StoriesBean> todayList = new ArrayList<>();
    private List<ZhihuList.TopStoriesBean> topTodayList=new ArrayList<>();
    private XBanner banner1;
    private FloatingActionButton floatingButton;
    private int mYear = Calendar.getInstance().get(Calendar.YEAR);
    private int mMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.zhihu_frag, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initImage();
        initFloatButton();
        zhihurefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
    }

    private void initFloatButton() {
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "点击确认选择知乎日报期数", Snackbar.LENGTH_LONG)
                        .setAction("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Calendar now = Calendar.getInstance();
                                now.set(mYear, mMonth, mDay);
                                final DatePickerDialog dialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                        mYear = year;
                                        mMonth = monthOfYear;
                                        mDay = dayOfMonth;
                                        Calendar temp = Calendar.getInstance();
                                        temp.clear();
                                        temp.set(year, monthOfYear, dayOfMonth);
                                        SimpleDateFormat f=new SimpleDateFormat("yyyyMMdd");
                                        String formattedDate=f.format(temp.getTime());
                                        initBeforeData(formattedDate);
                                    }
                                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                                dialog.setVersion(DatePickerDialog.Version.VERSION_1);
                                dialog.setMaxDate(Calendar.getInstance());
                                Calendar minDate = Calendar.getInstance();
                                // 2013.5.20是知乎日报api首次上线
                                minDate.set(2013, 5, 20);
                                dialog.setMinDate(minDate);
                                dialog.vibrate(false);

                                dialog.show(getActivity().getFragmentManager(), "DatePickerDialog");
                            }
                        }).show();
            }
        });
    }
    private void initBeforeData(String address){
        HttpUtil.SendOkHttpReuqest(Api.ZHIHU_HISTORY+address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "加载失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    final String responseText = response.body().string();
                    todayList = GsonUtil.TodayUtil(responseText).getStories();
                    if (todayList!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(new ZhihuRecyclerAdapter(todayList));
                            }
                        });
                    }
                }
            }
        });
    }
    private void initData() {
        HttpUtil.SendOkHttpReuqest(Api.ZHIHU_TODAY, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "加载失败，请重试", Toast.LENGTH_SHORT).show();
                        zhihurefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    final String responseText = response.body().string();
                    todayList = GsonUtil.TodayUtil(responseText).getStories();
                    if (todayList!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
                                recyclerView.setNestedScrollingEnabled(false);
                                recyclerView.setFocusable(false);
                                recyclerView.setAdapter(new ZhihuRecyclerAdapter(todayList));
                                zhihurefresh.setRefreshing(false);
                            }
                        });
                    }
                }
            }
        });
    }
    private void initImage() {
        HttpUtil.SendOkHttpReuqest("http://news-at.zhihu.com/api/4/news/latest", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response!=null){
                    String responseText=response.body().string();
                    topTodayList=GsonUtil.TodayUtil(responseText).getTop_stories();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final List<String> imgs=new ArrayList<String>();
                            List<String> tips=new ArrayList<String>();
                            for (int i=0;i<topTodayList.size();i++){
                                imgs.add(topTodayList.get(i).getImage());
                                tips.add(topTodayList.get(i).getTitle());
                            }
                            banner1.setData(imgs,tips);
                            banner1.setmAdapter(new XBanner.XBannerAdapter() {
                                @Override
                                public void loadBanner(XBanner banner, Object model, View view, int position) {
                                    Glide.with(getContext()).load(imgs.get(position)).asBitmap().centerCrop().into((ImageView) view);
                                }
                            });
                            banner1.setOnItemClickListener(new XBanner.OnItemClickListener() {
                                @Override
                                public void onItemClick(XBanner banner, int position) {
                                    Intent intent=new Intent(getContext(), Content.class);
                                    intent.putExtra("id",topTodayList.get(position).getId());
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        floatingButton = (FloatingActionButton) getActivity().findViewById(R.id.floatingButton);
        zhihurefresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.zhihurefresh);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        //roll = (RollPagerView) getActivity().findViewById(R.id.roll);
        banner1 = (XBanner) getActivity().findViewById(R.id.banner_1);
    }

    private class ZhihuRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<ZhihuList.StoriesBean> lists = new ArrayList<>();

        public ZhihuRecyclerAdapter(List<ZhihuList.StoriesBean> lists) {
            this.lists = lists;
        }

        public class NormalViewHolder extends RecyclerView.ViewHolder {
            private TextView homeItemTextView;
            private ImageView homeItemImageView;

            public NormalViewHolder(View itemView) {
                super(itemView);
                homeItemTextView = (TextView) itemView.findViewById(R.id.item_textView);
                homeItemImageView = (ImageView) itemView.findViewById(R.id.item_imageView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ZhihuList.StoriesBean item = lists.get(position);
            if (item.getImages().get(0) == null) {
                Glide.with(getContext()).load(R.drawable.elephant).centerCrop().into(((NormalViewHolder) holder).homeItemImageView);
            } else {
                Glide.with(getContext()).load(item.getImages().get(0)).asBitmap().centerCrop().into(((NormalViewHolder) holder).homeItemImageView);
            }
            ((NormalViewHolder) holder).homeItemTextView.setText(item.getTitle());
            ((NormalViewHolder) holder).homeItemImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), Content.class);
                    intent.putExtra("id",item.getId());
                    startActivity(intent);
                }
            });
            ((NormalViewHolder) holder).homeItemTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), Content.class);
                    intent.putExtra("id",item.getId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        banner1.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        banner1.stopAutoPlay();
    }
}
