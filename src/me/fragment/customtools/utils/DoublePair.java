package me.fragment.customtools.utils;

public class DoublePair<K, T> {

	private K first;
	private T second;

	public DoublePair(K first, T second) {
		this.first = first;
		this.second = second;
	}

	public DoublePair<K, T> of(K first, T second) {
		return new DoublePair<K, T>(first, second);
	}

	public K getFirst() {
		return first;
	}

	public void setFirst(K first) {
		this.first = first;
	}

	public T getSecond() {
		return second;
	}

	public void setSecond(T second) {
		this.second = second;
	}

}
