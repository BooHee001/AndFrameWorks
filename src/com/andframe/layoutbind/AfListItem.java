package com.andframe.layoutbind;

import com.andframe.activity.framework.AfView;
import com.andframe.adapter.AfListAdapter.IAfLayoutItem;
import com.andframe.annotation.inject.interpreter.Injecter;
import com.andframe.annotation.interpreter.ViewBinder;

public abstract class AfListItem<T> implements IAfLayoutItem<T>{
	
	private int layoutId;
	
	public AfListItem() {
	}
	
	public AfListItem(int layoutId) {
		this.layoutId = layoutId;
	}

	@Override
	public int getLayoutId() {
		return layoutId;
	}
	
	@Override
	public void onHandle(AfView view) {
		ViewBinder binder = new ViewBinder(this);
		binder.doBind(view.getView());
		Injecter injecter = new Injecter(this);
		injecter.doInject(view.getContext());
	}

}
