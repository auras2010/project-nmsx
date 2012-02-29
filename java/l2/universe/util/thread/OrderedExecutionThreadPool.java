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
import java.util.concurrent.ThreadFactory;

public final class OrderedExecutionThreadPool
{
	private final ComplexOrderedExecutionQueue _queue;
	private final Thread[] _threads;
	
	public OrderedExecutionThreadPool(final int poolSize, final ThreadFactory factory, final UncaughtExceptionHandler exceptionHandler)
	{
		if (poolSize <= 0)
			throw new IllegalArgumentException("Invalid pool size: " + poolSize);
		
		_queue = new ComplexOrderedExecutionQueue();
		_threads = new Thread[poolSize];
		for (int i = _threads.length; i-- > 0;)
		{
			_threads[i] = factory.newThread(new OrderedExecutionThread(_queue, exceptionHandler));
			_threads[i].start();
		}
	}
	
	public final void execute(final OrderedExecutionTarget target, final Runnable runnable)
	{
		final OrderedExecutionTask task = new OrderedExecutionTask(target, runnable);
		target.addLast(task);
		
		synchronized (_queue)
		{
			_queue.addLast(task);
			_queue.notify();
		}
	}
	
	public final void shutdown()
	{
		for (int i = _threads.length; i-- > 0;)
		{
			_threads[i].interrupt();
		}
	}
}