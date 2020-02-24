package my.project.silisili.main.favorite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import my.project.silisili.R;
import my.project.silisili.adapter.FavoriteListAdapter;
import my.project.silisili.bean.AnimeDescHeaderBean;
import my.project.silisili.database.DatabaseUtil;
import my.project.silisili.main.base.BaseActivity;
import my.project.silisili.main.desc.DescActivity;
import my.project.silisili.util.SwipeBackLayoutUtil;
import my.project.silisili.util.Utils;
import my.project.silisili.util.VideoUtils;
import butterknife.BindView;

public class FavoriteActivity extends BaseActivity<FavoriteContract.View, FavoritePresenter> implements FavoriteContract.View {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    private FavoriteListAdapter adapter;
    @BindView(R.id.mSwipe)
    SwipeRefreshLayout mSwipe;
    private List<AnimeDescHeaderBean> favoriteList = new ArrayList<>();

    @Override
    protected FavoritePresenter createPresenter() {
        return new FavoritePresenter(this);
    }

    @Override
    protected void loadData() {
        mPresenter.loadData(true);
    }

    @Override
    protected int setLayoutRes() {
        return R.layout.activity_anime;
    }

    @Override
    protected void init() {
        Slidr.attach(this,Utils.defaultInit());
        initToolbar();
        initSwipe();
        initAdapter();
    }

    @Override
    protected void initBeforeView() {
        SwipeBackLayoutUtil.convertActivityToTranslucent(this);
    }

    public void initToolbar(){
        toolbar.setTitle(Utils.getString(R.string.favorite_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    public void initSwipe(){
        //不启用下拉刷新
        mSwipe.setEnabled(false);
    }

    public void initAdapter(){
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new FavoriteListAdapter(this, favoriteList);
        adapter.openLoadAnimation();
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!Utils.isFastClick()) return;
            AnimeDescHeaderBean bean = (AnimeDescHeaderBean) adapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putString("name", bean.getName());
            String url = VideoUtils.getSiliUrl(bean.getUrl());
            bundle.putString("url", url);
            startActivityForResult(new Intent(FavoriteActivity.this, DescActivity.class).putExtras(bundle),3000);
        });
        adapter.setOnItemLongClickListener((adapter, view, position) -> {
            View v = adapter.getViewByPosition(mRecyclerView, position, R.id.img);
            final PopupMenu popupMenu = new PopupMenu(FavoriteActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.favorite_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.remove_favorite:
                        removeFavorite(position);
                        break;
                }
                return true;
            });
            popupMenu.show();
            return true;
        });
        if (Utils.checkHasNavigationBar(this)) mRecyclerView.setPadding(0,0,0, Utils.getNavigationBarHeight(this) - 5);
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * 移除收藏
     */
    private void removeFavorite(int position){
        DatabaseUtil.deleteFavorite(favoriteList.get(position).getName());
        adapter.remove(position);
        application.showCustomToastMsg(Utils.getString(R.string.join_error),
                R.drawable.ic_remove_favorite_48dp, R.color.red300);
        if (favoriteList.size() <= 0){
            errorTitle.setText(Utils.getString(R.string.empty_favorite));
            adapter.setEmptyView(errorView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200 && requestCode == 3000) {
            mPresenter.loadData(true);
        }
    }

    @Override
    public void showLoadingView() {
        favoriteList.clear();
        adapter.setNewData(favoriteList);
    }

    @Override
    public void showLoadErrorView(String msg) {
        errorTitle.setText(msg);
        adapter.setEmptyView(errorView);
    }

    @Override
    public void showEmptyVIew() {
        adapter.setEmptyView(emptyView);
    }

    @Override
    public void showSuccessView(List<AnimeDescHeaderBean> list) {
        favoriteList = list;
        adapter.setNewData(favoriteList);
    }
}
