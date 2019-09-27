package com.zoe.financetable.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.zoe.financetable.R;
import com.zoe.financetable.bean.FinanceReportData;
import com.zoe.financetable.widget.LinkedHorizontalScrollView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.zoe.financetable.constant.Constants.TABLE_CONTEXT_TEXT_SIZE;
import static com.zoe.financetable.constant.Constants.TABLE_NAME_SIZE;
import static com.zoe.financetable.constant.Constants.TABLE_TITLE_TEXT_SIZE;


public class FinanceReportContentAdapter extends RecyclerView.Adapter<FinanceReportContentAdapter.ViewHolder> implements Handler.Callback {
    private HashSet<LinkedHorizontalScrollView> observerList = new HashSet<>();
    private static final int NORMAL_VIEW = 0;
    private static final int FOOT_VIEW = 1;
    private List<FinanceReportData.ListBean> mList;
    private List<Integer> mWidths;
    private Context mContext;
    private LinearLayout.LayoutParams vParams;
    private float density;
    private int index = 0;
    private View footView;
    private Handler mHandler;

    public FinanceReportContentAdapter(Context context, List<FinanceReportData.ListBean> list, List<Integer> widths) {
        mHandler = new Handler(Looper.getMainLooper(),this);
        mContext = context;
        density = context.getResources().getDisplayMetrics().density;
        mList = list;
        mWidths = widths;
        vParams = new LinearLayout.LayoutParams((int) (density + 0.5f), -1);
    }

    public void bindTitleLinkedHorizontalScrollView(LinkedHorizontalScrollView title){
        title.setObserverList(observerList);
    }

    public void setFootView(View view) {
        footView = view;
    }

    public void setIndex(int index){
        this.index = index;
        notifyDataSetChanged();
        for (LinkedHorizontalScrollView rv : observerList) {
            rv.stopFling();
        }
        mHandler.sendEmptyMessageDelayed(MSG_INIT, 100);
    }
    private final int MSG_INIT = 100;
    @Override
    public boolean handleMessage(Message msg) {
        for (LinkedHorizontalScrollView rv : observerList) {
            rv.scrollTo(0, 0);
        }
        LinkedHorizontalScrollView.title_move = 0;
        return false;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOT_VIEW && footView != null) {
            ViewParent parent1 = footView.getParent();
            if (parent1 != null) {
                ((ViewGroup) parent1).removeView(footView);
            }
            return new ViewHolder(footView, viewType, index);
        } else {
            return new ViewHolder(View.inflate(parent.getContext(), R.layout.item_finance_report, null), viewType, index);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == mList.size()) return;
        FinanceReportData.ListBean bean = mList.get(position);
        holder.itemView.setTag(bean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if(tag instanceof FinanceReportData.ListBean){
                    Toast.makeText(mContext, ((FinanceReportData.ListBean)tag).show_time, Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.item_hsv.setTag(bean);
        holder.item_hsv.setOnClickListener(new LinkedHorizontalScrollView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if(tag instanceof FinanceReportData.ListBean){
                    Toast.makeText(mContext, ((FinanceReportData.ListBean)tag).show_time, Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.item_name.setText(bean.show_time);
        if(holder.index == index) {
            for (TextView textView : holder.text_view) {
                textView.setText("");
            }
        }else {
            initCell(holder.text_view, holder.item_hsv);
            holder.index = index;
        }
        List<FinanceReportData.FinanceReportContent> dataBeans = bean.data.get(index);
        int size = dataBeans.size();
        int view_size = holder.text_view.size();
        if(size > view_size) size = view_size;
        for (int j = 0; j < size; j++) {
            FinanceReportData.FinanceReportContent dataBean = dataBeans.get(j);
            TextView textView = holder.text_view.get(j);
            if (textView != null) textView.setText(dataBean.price);
        }
    }

    @Override
    public int getItemCount() {
        if (footView == null) {
            return mList.size();
        }
        return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size()) {
            return FOOT_VIEW;
        }
        return NORMAL_VIEW;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item_name;
        public LinkedHorizontalScrollView item_hsv;
        public List<TextView> text_view = new ArrayList<>();
        public int index = 0;

        public ViewHolder(View itemView, int viewType, int index) {
            super(itemView);
            if (viewType == FOOT_VIEW) {
                return;
            }
            this.index = index;
            this.item_name = (TextView) itemView.findViewById(R.id.item_name);
            View line = itemView.findViewById(R.id.item_line);
            View shade = itemView.findViewById(R.id.item_shade);
            this.item_hsv = (LinkedHorizontalScrollView) itemView.findViewById(R.id.item_hsv);
            this.item_hsv.setObserverList(observerList);
            int i = (int) (TABLE_NAME_SIZE * density);
            FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) item_name.getLayoutParams();
            params1.width = i;
            item_name.setLayoutParams(params1);

            FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) line.getLayoutParams();
            params2.width = (int) (density + 0.5f);
            params2.leftMargin = i;
            line.setLayoutParams(params2);

            FrameLayout.LayoutParams params3 = (FrameLayout.LayoutParams) shade.getLayoutParams();
            params3.leftMargin = (int) (i + density + 0.5f);
            shade.setLayoutParams(params3);

            FrameLayout.LayoutParams params4 = (FrameLayout.LayoutParams) item_hsv.getLayoutParams();
            params4.leftMargin = (int) (i + density + 0.5f);
            item_hsv.setLayoutParams(params4);

            initCell(text_view, item_hsv);
        }
    }

    private void initCell(List<TextView> text_view, LinkedHorizontalScrollView item_hsv){
        int old_size = text_view.size();
        int size = mWidths.size();
        int width = 0;
        //数据重用
        if(old_size < size) {
            for (int i = 0; i < old_size; i++) {
                int bean = mWidths.get(i);
                if (i > 0) {
                    width += (int) (density + 0.5f);
                }
                width += bean;
                TextView textView = text_view.get(i);
                ViewGroup.LayoutParams params = textView.getLayoutParams();
                params.width = bean;
                textView.setLayoutParams(params);
            }
            for (int i = old_size; i < size; i++) {
                int bean = mWidths.get(i);
                if (i > 0) {
                    width += (int) (density + 0.5f);
                    item_hsv.addView(getVLine());
                }
                width += bean;
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(bean, (int) (45 * density));
                TextView textView = getTextView();
                item_hsv.addView(textView, params);
                text_view.add(textView);
            }
        }else {
            for (int i = 0; i < size; i++) {
                int bean = mWidths.get(i);
                if (i > 0) {
                    width += (int) (density + 0.5f);
                }
                width += bean;
                TextView textView = text_view.get(i);
                ViewGroup.LayoutParams params = textView.getLayoutParams();
                params.width = bean;
                textView.setLayoutParams(params);
            }
            for (int i = old_size; i < size; i++) {
                TextView textView = text_view.get(i);
                ViewGroup.LayoutParams params = textView.getLayoutParams();
                params.width = 0;
                textView.setLayoutParams(params);
            }
        }
        width = (int) (width - mContext.getResources().getDisplayMetrics().widthPixels + TABLE_NAME_SIZE * density);
        item_hsv.setWidth(width);
        item_hsv.setOnScrollListener(new LinkedHorizontalScrollView.OnScrollListener() {
            @Override
            public void onScroll(int x) {
                for (LinkedHorizontalScrollView rv : observerList) {
                    rv.scrollTo(x, 0);
                }
                LinkedHorizontalScrollView.title_move = x;
            }
        });
    }

    private View getVLine() {
        View line = new View(mContext);
        line.setLayoutParams(vParams);
        line.setBackgroundColor(mContext.getResources().getColor(R.color.list_divider_color));
        return line;
    }

    private TextView getTextView() {
        TextView tv = new TextView(mContext);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(mContext.getResources().getColor(R.color.color_949799));
        tv.setTextSize(TABLE_CONTEXT_TEXT_SIZE);
        tv.setSingleLine(true);
        return tv;
    }

    private static Paint paint = new Paint();
    private static float min_width = -1;
    public static int getWidth(String text, float density) {
        if(min_width < 0) min_width = measureText("0.000000000", TABLE_CONTEXT_TEXT_SIZE, density);
        float measureText = measureText(text,density);
        if(measureText < min_width - 10 * density){
            return (int) min_width;
        }else {
            return (int) (measureText + 10 * density);
        }
    }

    private static float measureText(String text, int size, float density) {
        if(TextUtils.isEmpty(text)) return 0;
        paint.setTextSize(size*density);
        return paint.measureText(text);
    }
    private static float measureText(String text, float density) {
        return measureText(text, TABLE_TITLE_TEXT_SIZE, density);
    }
}
