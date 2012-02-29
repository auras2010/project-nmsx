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

public final class OrderedExecutionTarget
{
	private final SimpleOrderedExecutionQueue _queue;
	private final Object _processingLock;
	private boolean _processingTask;
	
	public OrderedExecutionTarget()
	{
		_queue = new SimpleOrderedExecutionQueue();
		_processingLock = new Object();
	}
	
	final boolean getAndSetProcessingTask()
	{
		synchronized (_processingLock)
		{
			if (_processingTask)
				return false;
			
			_processingTask = true;
			return true;
		}
	}
	
	final void unsetProcessingTask()
	{
		synchronized (_processingLock)
		{
			_processingTask = false;
		}
	}
	
	final void addLast(final OrderedExecutionTask task)
	{
		synchronized (_queue)
		{
			_queue.addLast(task);
		}
	}
	
	final OrderedExecutionTask removeFirst()
	{
		synchronized (_queue)
		{
			return _queue.removeFirst();
		}
	}
}