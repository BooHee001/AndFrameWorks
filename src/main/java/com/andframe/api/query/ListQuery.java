package com.andframe.api.query;

import com.andframe.api.query.hindler.Map;
import com.andframe.api.query.hindler.Where;

import java.util.Collection;
import java.util.List;

/**
 * 列表查询接口
 * Created by SCWANG on 2017/5/11.
 */

public interface ListQuery<T> extends Collection<T> {
    ListQuery<T> remove(Where<T> where);
    ListQuery<T> where(Where<T> where);
    List<T> toList();
    <TT> ListQuery<TT> map(Map<T,TT> map);
}
