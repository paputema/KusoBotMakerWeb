package com.kusobotmaker;

import java.util.ArrayList;
import java.util.Collection;

class LimitedArrayList<T> extends ArrayList<T>
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int listSize;

	public LimitedArrayList(int listSize) {
		super();
		this.listSize = listSize;
	}
	@Override
	public boolean add(T e) {
		// TODO 自動生成されたメソッド・スタブ
		boolean bool =   super.add(e);
		if(super.size() > listSize )
		{
			super.remove(0);
		}
		return bool;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		if(c.size() > listSize)
		{
			return false;
		}
		boolean bool =  super.addAll(index,c);
		while (super.size() > listSize) {
			super.remove(0);
		}
		return bool;
	}
	@Override
	public boolean addAll(Collection<? extends T> c) {
		return addAll(1,c);
	}
	@Override
	public void add(int index, T element) {
		// TODO 自動生成されたメソッド・スタブ
		super.add(index, element);
		if(super.size() > listSize )
		{
			super.remove(0);
		}
	}


}