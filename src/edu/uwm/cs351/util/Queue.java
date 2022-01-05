package edu.uwm.cs351.util;

import java.util.NoSuchElementException;
import edu.uwm.cs.junit.LockedTestCase;

public class Queue<E> implements Cloneable {

	/** Constants */
	private static final int DEFAULT_CAPACITY = 1; // force more frequent resizing
	
	/** Fields */
	private E[] _data;
	private int _head, _tail;
	
	private boolean report(String s) {
		System.out.println("invariant error: " + s);
		return false;
	}
	
	/** Invariant */
	private boolean wellFormed() {
		// The invariant:
		// 0. data cannot be null
		if(_data == null || _data.length == 0) return report("data is null");
		
		// 1. head and tail must be in range
		int range = _data.length;
		if(_head < 0 || _head > range - 1) return report("_head is not in range");
		if(_tail < 0 || _tail > range - 1) return report("_tail is not in range");
		
		// 2. If head doesn't equal tail, there are no null elements in range [head, tail)
		//			NB: This range *may* wrap around end of array.
		if(_head != _tail)
		{
			int size = size();
			for(int i = 0; i < size; ++i)
			{
				int n = (i + _head) % _data.length;
				
				if(n != _tail)
				{
					if(_data[n] == null) return report("there is null data in the array");
				}
			}
		}
		return true;
	}
	
	private Queue(boolean ignored) {} // do not change: used by invariant checker.
	
	/** Create an empty Queue with capacity DEFAULT_CAPACITY. */
	public Queue() {
		_data = makeArray(DEFAULT_CAPACITY);
	}
	
	@SuppressWarnings("unchecked")
	private E[] makeArray(int s) {
		return (E[]) new Object[s];
	}
	
	/**
	 * Determine whether the queue is empty.
	 * @return true if queue is empty
	 */
	public boolean isEmpty() {
		// (no loops, no ifs!)
		return _data[_head] == null;
	}
	
	/**
	 * Compute how many elements are in the queue.
	 * @return how many elements are in this queue
	 */
	public int size() {
		// (no loops, one "if" permitted)
		int size = 0;
		if(_head <= _tail) {
			size = _tail - _head;
		}
		else {
			size = _tail - _head + 1 +(_data.length - 1);
		}
			
		return size;
	}
	
	/**
	 * Add an element to the queue,
	 * @param x the element to add, must not be null
	 */
	public void enqueue(E x) {
		// (no loops, no ifs)
		ensureCapacity(size() + 1);
		
		_data[_tail] = x;
		_tail = nextIndex(_tail);
	}
	
	/**
	 * Return (but do not remove) the front element of this queue.
	 * @return element at front of queue
	 * @exception NoSuchElementException if the queue is empty
	 */
	public E front() {
		// (no loops, "if" only for error)
		if(isEmpty()) throw new NoSuchElementException("there are no elements");
		
		return _data[_head];
	}
	
	/**
	 * Remove and return the front element from the queue.
	 * @return element formerly at front of queue
	 * @exception NoSuchElementException if the queue is empty
	 */
	public E dequeue() {
		// (no loops, "if" only for error)
		if(isEmpty())throw new NoSuchElementException("there are no elements to remove");
		
		E fElement = _data[_head];
		_data[_head] = null;
		_head = nextIndex(_head);
		
		return fElement;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public Queue<E> clone()
	{
		Queue<E> result = null;
		try {
			result = (Queue<E>) super.clone( );
		}
		catch (CloneNotSupportedException e) {  
			// Shouldn't happen
		}
		result._data = _data.clone();
		return result;
	}
	
	/**
	 * Helper function to advance an index through the circular array.
	 * NB: Do not use / or % operations.
	 * @param i the index
	 * @return the next index after i
	 */
	private int nextIndex(int i) {
		if(i == _data.length - 1) { 
			return 0;
		}
		else {
			return ++i;
		}
	}
	
	/**
	 * Ensure that the capacity of the array is such that
	 * at least minCap elements can be in queue.  If necessary,
	 * the capacity is doubled and the elements are arranged
	 * in the queue correctly. Remember that the capacity of
	 * the queue is one less than the array length because we must
	 * always reserve an array slot for the next added element.
	 * @param minCap the minimum capacity
	 */
	@SuppressWarnings("unchecked")
	private void ensureCapacity(int minCap) {
		if(_data.length <= minCap)
		{
			if(_data.length * 2 > minCap) {
				minCap = _data.length * 2;
			}
			else {
				minCap = minCap + 1;
			}
			E[] biggerArray = (E[]) new Object[minCap];
			
			if(_tail < _head) {
				int s = size();
				for(int i = 0; i < s;) {
					for(int j = _head; j != _tail; j = nextIndex(j)) {
						biggerArray[i] = _data[j];
						++i;
					}
				}
				_tail = s;
				_head = 0;
			}
			else {
				for(int i = 0; i < _data.length; ++i) {
					biggerArray[i] = _data[i];
				}
			}
			
			_data = biggerArray;
		}
	}
	
	public static class TestInvariant extends LockedTestCase {
		private Queue<Object> self;
		
		protected void setUp() {
			self = new Queue<Object>(false);
		}
		
		public void test00() {
			assertFalse(self.wellFormed());
		}
		
		public void test01() {
			self._data = new Object[0];
			assertFalse(self.wellFormed());
			self._data = new Object[DEFAULT_CAPACITY];
			assertTrue(self.wellFormed());
		}
		
		public void test02() {
			self._data = new Object[DEFAULT_CAPACITY];
			
			self._head = -1;
			assertFalse(self.wellFormed());
			self._head = DEFAULT_CAPACITY;
			assertFalse(self.wellFormed());
			self._head = 0;
			
			self._tail = -1;
			assertFalse(self.wellFormed());
			self._tail = DEFAULT_CAPACITY;
			assertFalse(self.wellFormed());
			self._tail = 0;
			
			assertTrue(self.wellFormed());
		}
		
		public void test03() {
			self._data = new Object[] { null, null, null, null };
			
			self._head = self._tail = 1;
			assertEquals(Tb(743576227), self.wellFormed());
			
			self._tail = 2;
			assertEquals(Tb(1218654085), self.wellFormed());
			
			self._tail = 0;
			assertEquals(Tb(279166937), self.wellFormed());
		}
		
		public void test04() {
			self._data = new Object[] { null, null, 6, null };
			
			self._head = self._tail = 1;
			assertEquals(Tb(825333284), self.wellFormed());
			
			self._head = self._tail = 2;
			assertEquals(Tb(1891366333), self.wellFormed());
			
			self._tail = 3;
			assertEquals(Tb(935261471), self.wellFormed());
		}
		
		public void test05() {
			self._data = new Object[] { 2, null, 6, 0 };
			
			self._head = self._tail = 0;
			assertTrue(self.wellFormed());
			self._head = self._tail = 1;
			assertTrue(self.wellFormed());
			self._head = self._tail = 2;
			assertTrue(self.wellFormed());
			self._head = self._tail = 3;
			assertTrue(self.wellFormed());
		}
		
		public void test06() {
			self._data = new Object[] { 2, null, 6, 0 };
			
			self._head = 2;
			self._tail = 1;
			assertEquals(Tb(2119192456), self.wellFormed());
			
			self._tail = 3;
			assertEquals(true, self.wellFormed());
		}
		
		public void test07() {
			self._data = new Object[] { 3, 0, 8, 8 };
			
			self._head = 2;
			self._tail = 1;
			assertEquals(true, self.wellFormed());
			
			self._tail = self._head;
			assertEquals(true, self.wellFormed());
		}
		
		public void test08() {
			self._data = new Object[] { 2, null, null, 0 };
			
			self._head = 2;
			self._tail = 3;
			assertEquals(Tb(530478379), self.wellFormed());
			
			self._head = 3;
			self._tail = 2;
			assertEquals(Tb(28890223), self.wellFormed());
			
			self._head = 3;
			self._tail = 1;
			assertEquals(Tb(1122720828), self.wellFormed());
		}

		public void test09() {
			self._data = new Object[] { null };
			self._head = self._tail = 0;
			self.ensureCapacity(1);
			assertEquals(2, self._data.length);
		}
		
		public void test10() {
			self._data = new Object[] { 1, null };
			self._head = 0;
			self._tail = 1;
			self.ensureCapacity(1);
			assertEquals(2, self._data.length);
			self.ensureCapacity(2);
			assertEquals(4, self._data.length);
		}
		
		public void test11() {
			self._data = new Object[] { null, 1, 2 };
			self._head = 1;
			self._tail = 0;
			self.ensureCapacity(3);
			assertEquals(Ti(410468541), self._data.length);
			
			self._head = self._tail = 4;
			self.ensureCapacity(6);
			assertEquals(12, self._data.length);
			
			self._tail = 5;
			assertFalse("null element in queue", self.wellFormed());
		}
		
		public void test12() {
			self._data = new Object[4];
			self._tail = self._tail = 0;
			self.ensureCapacity(300);
			assertEquals(Ti(1420935520), self._data.length);
		}
		
		public void test13() {
			self._data = new Object[100];
			self._head = self._tail = 0;
			self.ensureCapacity(100);
			assertEquals(200, self._data.length);
		}

		public void test14() {
			self._data = new Object[8];
			assertEquals(1, self.nextIndex(0));
			assertEquals(Ti(235014792), self.nextIndex(6));
			assertEquals(Ti(814609720), self.nextIndex(7));
		}
	}
}
