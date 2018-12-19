package com.tj.order.card.layoutmanager.utils;

import android.animation.TypeEvaluator;
import android.graphics.PointF;


/**
 *
 * Created by TJbaobao on 2017/10/19.
 */

public class PointFEvaluator implements TypeEvaluator<PointF> {
    private final PointF pointF = new PointF();
    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        float x = startValue.x + fraction * (endValue.x - startValue.x);
        float y = startValue.y+ fraction * (endValue.y - startValue.y);
        pointF.set(x,y);
        return pointF;
    }
}
