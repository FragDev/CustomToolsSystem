package me.fragment.customtools.utils;

public class TriPair<K, T, V> {

	private K first;
	private T second;
	private V third;

	public TriPair(K first, T second, V third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public TriPair<K, T, V> of(K first, T second, V third) {
		return new TriPair<K, T, V>(first, second, third);
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

	public V getThird() {
		return third;
	}

	public void setThird(V third) {
		this.third = third;
	}

}
