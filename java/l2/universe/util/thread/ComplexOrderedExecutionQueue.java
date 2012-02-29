/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2.universe.util.thread;

final class ComplexOrderedExecutionQueue
{
	private final ComplexOrderedExecutionQueueNode _head;
	private ComplexOrderedExecutionQueueNode _tail;
	
	public ComplexOrderedExecutionQueue()
	{
		_head = new ComplexOrderedExecutionQueueNode(null);
		_tail = new ComplexOrderedExecutionQueueNode(_head);
		_head.next = _tail;
	}
	
	public final void addLast(final OrderedExecutionTask task)
	{
		_tail.task = task;
		_tail = _tail.next = new ComplexOrderedExecutionQueueNode(_tail);
	}
	
	public final ComplexOrderedExecutionQueueNode head()
	{
		return _head;
	}
	
	public final ComplexOrderedExecutionQueueNode tail()
	{
		return _tail;
	}
	
	public final boolean isEmpty()
	{
		return _head.next == _tail;
	}
	
	public final void remove(final ComplexOrderedExecutionQueueNode node)
	{
		node.prev.next = node.next;
		node.next.prev = node.prev;
	}
	
	final class ComplexOrderedExecutionQueueNode
	{
		ComplexOrderedExecutionQueueNode next;
		ComplexOrderedExecutionQueueNode prev;
		OrderedExecutionTask task;
		
		ComplexOrderedExecutionQueueNode(final ComplexOrderedExecutionQueueNode vPrev)
		{
			prev = vPrev;
		}
	}
}