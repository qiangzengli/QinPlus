package com.weiyin.qinplus.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 帮助
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class HelpActivity extends BaseActivity implements View.OnClickListener {

    private ImageView detailTabLine;

    private TextView textViewContent;

    private Bitmap bitmap;

    /**
     * 屏幕寬度, 坐标
     */
    private int screenWidth, screenIndex = 1;
    private String pianoContent = "    非常感谢您购买智能钢琴 ！\n" +
            "    不论您是专业人士还是钢琴爱好者，只要您喜欢音乐，那么这款钢琴将会是您最好的选择。它不仅能胜任钢琴学习 MIDI 制作等, 还可以满足您音响聆听等娱乐需求。同时 这款乐视智能钢琴的高性能分阶带重锤力度键盘，还能给您带来专业演奏般的完美音质和感觉。时尚的外形设计和全新的钢琴音源会给您留下深刻的印象。\n" +
            "    如果您喜欢这款智能钢琴，请推荐给您的朋友！";
    private String appContent = "    APP通过蓝牙连接智能钢琴，实现与钢琴的实时交互。\n" +
            "    APP中的教程由上海音乐学院资深教授领衔设计，内容包含丰富的曲目。其中曲目分三大类：经典曲谱（启蒙、初级、中级、指法与音阶）、流行音乐（儿童、通俗、名曲）、考级专区（第1级、第2级、第3级、第4级、第5级）。\n\n" +
            "APP特点\n\t• \t上海音乐学院资深教授领衔设计教程\n" +
            "\t• \t通过蓝牙无线连接平板，与APP一体互动\n" +
            "\t• \t智能教学展示，让人人都会弹钢琴\n" +
            "\t• \t强大的APP教学互动功能\n" +
            "\t• \t曲谱分类齐全，适合男女老少";

    private String blueContent = "\t• \t1、打开曲谱馆；\n" +
            "\t• \t2、进入曲谱；\n" +
            "\t• \t3、点击蓝牙按钮；\n" +
            "\t• \t4、点击连接；\n" +
            "\t• \t5、如果是已连接用户第二次进入APP会自动连接蓝牙，蓝牙连接未成功重新走第1    步，连接成功正常使用；";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        LayoutHelper layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.activity_help));
        initView();

        initTabLineWidth();
        textViewContent.setText(pianoContent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    public void initView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_help);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        detailTabLine = (ImageView) findViewById(R.id.activity_help_iv_sboard_detail_tabline);
        ImageView backImageView = (ImageView) findViewById(R.id.help_activity_back_imageview);

        RelativeLayout oneTabRl = (RelativeLayout) findViewById(R.id.activity_help_onetab_rl);
        RelativeLayout twoTabRl = (RelativeLayout) findViewById(R.id.activity_help_twotab_rl);
        RelativeLayout threeTabRl = (RelativeLayout) findViewById(R.id.activity_help_threetab_rl);

        textViewContent = (TextView) findViewById(R.id.activity_help_textview_content);

        oneTabRl.setOnClickListener(this);
        twoTabRl.setOnClickListener(this);
        threeTabRl.setOnClickListener(this);

        backImageView.setOnClickListener(this);
    }

    /**
     * 设置tabLine属性
     */
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) detailTabLine
                .getLayoutParams();
        lp.width = screenWidth / 6;
        lp.height = 5;
        lp.leftMargin = screenWidth / 3 * screenIndex - (screenWidth / 3 - screenWidth / 6) / 2 - screenWidth / 6;
        detailTabLine.setLayoutParams(lp);
    }

    public void updateLp(RelativeLayout.LayoutParams lp) {
        lp.leftMargin = screenWidth / 3 * screenIndex - (screenWidth / 3 - detailTabLine.getWidth()) / 2
                - detailTabLine.getWidth();
    }

    @Override
    public void onClick(View v) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) detailTabLine
                .getLayoutParams();
        int id = v.getId();

        if (id == R.id.activity_help_onetab_rl) {
            screenIndex = 1;
            updateLp(lp);
            textViewContent.setText(pianoContent);
        } else if (id == R.id.activity_help_twotab_rl) {
            screenIndex = 2;
            updateLp(lp);
            textViewContent.setText(appContent);
        } else if (id == R.id.activity_help_threetab_rl) {
            screenIndex = 3;
            updateLp(lp);
            textViewContent.setText(blueContent);
        } else if (id == R.id.help_activity_back_imageview) {
            finish();
        }
        detailTabLine.setLayoutParams(lp);
    }
}
