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

import java.lang.Thread.UncaughtExceptionHandler;

import l2.universe.util.thread.ComplexOrderedExecutionQueue.ComplexOrderedExecutionQueueNode;

final class OrderedExecutionThread implements Runnable
{
	private final ComplexOrderedExecutionQueue _queue;
	private final UncaughtExceptionHandler _exceptionHandler;
	
	public OrderedExecutionThread(final ComplexOrderedExecutionQueue queue, final UncaughtExceptionHandler exceptionHandler)
	{
		_queue = queue;
		_exceptionHandler = exceptionHandler;
	}

	@Override
	public final void run()
	{
		ComplexOrderedExecutionQueueNode node1;
		OrderedExecutionTask task1;
		OrderedExecutionTask task2;
		
		while (true)
		{
			synchronized (_queue)
			{
				while (_queue.isEmpty())
				{
					try
					{
						_queue.wait();
					}
					catch (final InterruptedException e)
					{
						return;
					}
				}
				
				task1 = null;
				node1 = _queue.head();
				while ((node1 = node1.next) != _queue.tail())
				{
					if (node1.task.getAndSetProcessingTask())
					{
						_queue.remove(node1);
					task1 = node1.task;
						break;
					}
				}
			}
			
			if (task1 != null)
			{
				try
				{
					while ((task2 = task1.removeFirst()) != null)
					{
						try
						{
							task2.run();
						}
						catch (final Throwable e)
						{
							_exceptionHandler.uncaughtException(Thread.currentThread(), e);
						}
					}
				}
				finally
				{
					task1.unsetProcessingTask();
				}
			}
		}
	}
}