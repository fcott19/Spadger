package com.fcott.spadger.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fcott.spadger.R;

/**
 * Created by Administrator on 2017/8/15.
 */

public class PageController extends LinearLayout implements View.OnClickListener{

    private Context context;
    private TextView firstPage,prePage,nextPage,lastPage;
    private EditText etPageNumber;
    private ObserverPageListener pageListener;
    private int currentPageIndex = 1;
    private int maxPageIndex = 0;

    public PageController(Context context) {
        this(context,null);
    }

    public PageController(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    private void init(final Context context){
        View.inflate(context, R.layout.widegt_page_controller, this);
        firstPage = (TextView)findViewById(R.id.tv_first_page);
        prePage = (TextView)findViewById(R.id.tv_pre_page);
        nextPage = (TextView)findViewById(R.id.tv_next_page);
        lastPage = (TextView)findViewById(R.id.tv_last_page);
        etPageNumber = (EditText)findViewById(R.id.et_page_number);

        firstPage.setOnClickListener(this);
        prePage.setOnClickListener(this);
        nextPage.setOnClickListener(this);
        lastPage.setOnClickListener(this);

        //监听软键盘完成按钮
        etPageNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    /*隐藏软键盘*/
                InputMethodManager imm = (InputMethodManager) v
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(
                            v.getApplicationWindowToken(), 0);
                }
                if(pageListener != null && maxPageIndex != 0){
                    int pageNumber = 0;
                    try {
                        pageNumber = Integer.valueOf(etPageNumber.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (pageNumber < 1 || pageNumber > maxPageIndex) {
                        Toast.makeText(context, getResources().getString(R.string.input_error), Toast.LENGTH_SHORT).show();
                    } else {
                        currentPageIndex = pageNumber;
                        pageListener.goPage(currentPageIndex);
                    }
                }
                //etPageNumber失去焦点，隐藏光标
                etPageNumber.clearFocus();
                etPageNumber.setFocusable(false);
                return true;
            }
        });
        //触摸etPageNumber时获取焦点
        etPageNumber.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                etPageNumber.setFocusable(true);
                etPageNumber.setFocusableInTouchMode(true);
                etPageNumber.requestFocus();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(pageListener == null || maxPageIndex == 0)
            return;
        switch (v.getId()){
            case R.id.tv_first_page:
                if (currentPageIndex != 1) {
                    currentPageIndex = 1;
                    pageListener.goPage(currentPageIndex);
                } else {
                    Toast.makeText(context, getResources().getString(R.string.already_first), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_pre_page:
                if (currentPageIndex > 1) {
                    currentPageIndex--;
                    pageListener.goPage(currentPageIndex);
                } else {
                    Toast.makeText(context, getResources().getString(R.string.already_first), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_next_page:
                if (currentPageIndex < maxPageIndex) {
                    currentPageIndex++;
                    pageListener.goPage(currentPageIndex);
                } else {
                    Toast.makeText(context, getResources().getString(R.string.already_last), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_last_page:
                if (currentPageIndex != maxPageIndex) {
                    currentPageIndex = maxPageIndex;
                    pageListener.goPage(currentPageIndex);
                } else {
                    Toast.makeText(context, getResources().getString(R.string.already_last), Toast.LENGTH_LONG).show();
                }
                break;
        }
        etPageNumber.setText(String.valueOf(currentPageIndex));
    }

    public void setMaxPageIndex(int maxPageIndex) {
        if(this.maxPageIndex != 0)
            return;
        this.maxPageIndex = maxPageIndex;
    }

    public void setObserverPageListener(ObserverPageListener pageListener) {
        this.pageListener = pageListener;
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public interface ObserverPageListener {
        void goPage(int page);
    }
}
