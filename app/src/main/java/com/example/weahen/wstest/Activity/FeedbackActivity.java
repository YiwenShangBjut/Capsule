package com.example.weahen.wstest.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.Config.GlobalConfig;
import com.example.weahen.wstest.Config.URLConfig;
import com.example.weahen.wstest.Model.FeedBack;
import com.example.weahen.wstest.Model.Tidings;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.Utils.DateUtils;
import com.example.weahen.wstest.Utils.FastJsonTools;
import com.example.weahen.wstest.Utils.ScreenUtils;
import com.example.weahen.wstest.Utils.SharedPreferencesUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import org.xutils.x;

public class FeedbackActivity extends BaseActivity {

    @BindView(R.id.tv_advice)
    EditText tv_advice;

    @BindView(R.id.rg_feedback_type)
    RadioGroup rg_feedback_type;

    @BindView(R.id.rb_a)
    RadioButton rb_a;

    @BindView(R.id.rb_b)
    RadioButton rb_b;

    @BindView(R.id.rb_c)
    RadioButton rb_c;

    @BindView(R.id.rb_d)
    RadioButton rb_d;

    @BindView(R.id.btn_submit)
    Button btn_submit;
    String feedbackType = "";//反馈内容类型
    String content = "";//反馈内容类型
    ScreenUtils utils;
    SharedPreferencesUtil spUtils;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        //绑定该布局
        ButterKnife.bind(this);
        initTitle("服务与支持");
        //实例化工具类
        utils = new ScreenUtils(this);
        spUtils = new SharedPreferencesUtil(this);
        rg_feedback_type = findViewById(R.id.rg_feedback_type);
        rg_feedback_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.rb_a:
                        feedbackType = rb_a.getText().toString().trim();
                        break;
                    case R.id.rb_b:
                        feedbackType = rb_b.getText().toString().trim();
                        break;
                    case R.id.rb_c:
                        feedbackType = rb_c.getText().toString().trim();
                        break;
                    case R.id.rb_d:
                        feedbackType = rb_d.getText().toString().trim();
                        break;

                }
            }
        });
    }

    /**
     * 提交意见反馈
     * @param v
     */
    @OnClick(R.id.btn_submit)
    public void onClick(View v) {
        String contact = tv_advice.getText().toString().trim();
        if (TextUtils.isEmpty(contact)) {
            ScreenUtils.showToast("您忘记写您的宝贵意见啦~");
        } else {
            utils.showLoading();
            //获取手机型号：
            String   model= android.os.Build.MODEL;
            //  获取手机厂商：
            String carrier= android.os.Build.MANUFACTURER;
            FeedBack userFeedback = new FeedBack( model+carrier+GlobalConfig.getUSERNO()+GlobalConfig.getUSERNAME()+GlobalConfig.getUIMEI(),
                    feedbackType+contact, DateUtils.getNowDateTime() );
            RequestParams params = new RequestParams("");
            params.setAsJsonContent(true);
            params.setBodyContent(FastJsonTools.createJsonString(userFeedback));
            params.addQueryStringParameter("json", FastJsonTools.createJsonString(userFeedback));
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    if (result != null) {
                        Tidings tidings = FastJsonTools.createJsonBean(result,Tidings.class);
                        utils.createConfirmDialog("提示", tidings.getMsg(),
                                "知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
//                            ScreenUtils.showToast(tidings.getMsg());
                        if (GlobalConfig.SUCCESS_FLAG.equals(tidings.getStatus())) {//反馈成功，则关闭反馈界面
                            utils.finishCurrentViewInTimes(1200);
                        } else {//否则不做任何操作，扔交给用户处理

                        }
                    }else{
                        utils.ToastInfo("网络错误");
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ScreenUtils.showToast("服务器又开小差>_<"+ex.getMessage());
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    utils.cancleLoading();
                }
            });

        }

    }

    public static void newInstance(Context context) {
        Intent intent = new Intent(context,FeedbackActivity.class);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}
