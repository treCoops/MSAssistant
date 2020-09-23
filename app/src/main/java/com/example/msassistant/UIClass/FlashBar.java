package com.example.msassistant.UIClass;

import android.app.Activity;

import androidx.core.content.ContextCompat;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.example.msassistant.R;

public class FlashBar {

    private Activity activity;

    public FlashBar(Activity activity){
        this.activity = activity;
    }

    public void alertDanger(String message){
         new Flashbar.Builder(activity)
                .gravity(Flashbar.Gravity.BOTTOM)
                .duration(2000)
                .message(message)
                .messageColor(ContextCompat.getColor(activity, R.color.white))
                .backgroundColor(ContextCompat.getColor(activity, R.color.flashBackground))
                .showIcon()
                .iconColorFilterRes(R.color.flashDanger)
                .icon(R.drawable.ic_cross)
                .enterAnimation(FlashAnim.with(activity)
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(activity)
                        .animateBar()
                        .duration(1800)
                        .slideFromLeft()
                        .overshoot())
                .build().show();
    }

    public void alertSuccess(String message){
         new Flashbar.Builder(activity)
                .gravity(Flashbar.Gravity.BOTTOM)
                .duration(2000)
                .message(message)
                .messageColor(ContextCompat.getColor(activity, R.color.white))
                .backgroundColor(ContextCompat.getColor(activity, R.color.flashBackground))
                .showIcon()
                .iconColorFilterRes(R.color.flashSuccess)
                .icon(R.drawable.ic_check)
                .enterAnimation(FlashAnim.with(activity)
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(activity)
                        .animateBar()
                        .duration(1800)
                        .slideFromLeft()
                        .overshoot())
                .build();
    }

    public void alertWarning(String message){
         new Flashbar.Builder(activity)
                .gravity(Flashbar.Gravity.BOTTOM)
                .duration(2000)
                .message(message)
                .messageColor(ContextCompat.getColor(activity, R.color.white))
                .backgroundColor(ContextCompat.getColor(activity, R.color.flashBackground))
                .showIcon()
                .iconColorFilterRes(R.color.flashCaution)
                .icon(R.drawable.ic_caution)
                .enterAnimation(FlashAnim.with(activity)
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(activity)
                        .animateBar()
                        .duration(1800)
                        .slideFromLeft()
                        .overshoot())
                .build();
    }

}
