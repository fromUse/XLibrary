package com.library.db.I;

import java.util.List;

/**
 * Created by chen on 2016/9/22.
 */
public interface IBatchable<T> {

    /**
     * 批量更新数据
     *
     * @param people
     */
    void batchUpdate(List<T> people);

    /**
     * 批量插入数据
     *
     * @param people
     */
    void batchInsert(List<T> people) throws Exception;

    /**
     * 清空数据
     */
    void clear();

    void setInsertListener(OnBatchInertListener insertListener);

    void setClearListener(OnBatcClearListener clearListener);

    public interface OnBatcClearListener {
        void onClearSuccess();


        void onClearFail(Exception e);
    }

    public interface OnBatchInertListener {
        void onInsertSuccess();

        void onInsertFail(Exception e);
    }

}
