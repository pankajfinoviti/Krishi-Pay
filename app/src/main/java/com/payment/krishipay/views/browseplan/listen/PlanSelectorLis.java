package com.payment.krishipay.views.browseplan.listen;

import com.payment.krishipay.views.browseplan.model.DataItem;

public interface PlanSelectorLis {
        void onButtonSelect(int p, DataItem model);

        void onImgExpand(int p, DataItem model);
    }