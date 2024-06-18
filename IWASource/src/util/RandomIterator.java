package util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;

public class RandomIterator<E>
implements Iterator<E> {
	private LinkedList<E> list;
	private Random random;

	public RandomIterator(Collection<E> list) {
		this.list = new LinkedList<E>(list);
		this.random = new Random();
	}

	public boolean hasNext() {
		return !this.list.isEmpty();
	}

	public E next() {
		if (this.list.isEmpty()) {
			throw new NoSuchElementException();
		}
		int index = this.random.nextInt(this.list.size());
		E value = this.list.get(index);
		this.list.remove(index);
		return value;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
