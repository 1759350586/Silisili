package my.project.silisili.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.Nullable;
import my.project.silisili.R;
import my.project.silisili.bean.AnimeDescRecommendBean;
import my.project.silisili.util.Utils;

/**
 * 相关推荐适配器
 */
public class AnimeDescRecommendAdapter extends BaseQuickAdapter<AnimeDescRecommendBean, BaseViewHolder> {
    private Context context;

    public AnimeDescRecommendAdapter(Context context, @Nullable List<AnimeDescRecommendBean> data) {
        super(R.layout.item_desc_recommend, data);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, AnimeDescRecommendBean item) {
        Utils.setCardDefaultBg(context, helper.getView(R.id.card_view), helper.getView(R.id.title));
        helper.setText(R.id.title, item.getTitle());
        Utils.setDefaultImage(context, item.getImg(), helper.getView(R.id.img));
        Utils.setCardBg(context, item.getImg(), helper.getView(R.id.card_view), helper.getView(R.id.title));
    }
}