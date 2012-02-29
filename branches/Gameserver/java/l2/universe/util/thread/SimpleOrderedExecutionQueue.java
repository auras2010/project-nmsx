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

final class SimpleOrderedExecutionQueue
{
	private final SimpleOrderedExecutionQueueNode _head;
	private SimpleOrderedExecutionQueueNode _tail;
	
	public SimpleOrderedExecutionQueue()
	{
		_head = new SimpleOrderedExecutionQueueNode();
		_tail = new SimpleOrderedExecutionQueueNode();
		_head.next = _tail;
	}
	
	public final void addLast(final OrderedExecutionTask task)
	{
		_tail.task = task;
		_tail = _tail.next = new SimpleOrderedExecutionQueueNode();
	}
	
	public final OrderedExecutionTask removeFirst()
	{
		final SimpleOrderedExecutionQueueNode node = _head.next;		
		if (node == _tail)
			return null;
		
		_head.next = node.next;
		return node.task;
	}
	
	final class SimpleOrderedExecutionQueueNode
	{
		SimpleOrderedExecutionQueueNode next;
		OrderedExecutionTask task;
	}
}