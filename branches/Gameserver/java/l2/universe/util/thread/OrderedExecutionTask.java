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

final class OrderedExecutionTask implements Runnable
{
	private final OrderedExecutionTarget _target;
	private final Runnable _runnable;
	
	public OrderedExecutionTask(final OrderedExecutionTarget target, final Runnable runnable)
	{
		_target = target;
		_runnable = runnable;
	}
	
	public boolean getAndSetProcessingTask()
	{
		return _target.getAndSetProcessingTask();
	}
	
	public void unsetProcessingTask()
	{
		_target.unsetProcessingTask();
	}
	
	@Override
	public final void run()
	{
		_runnable.run();
	}

	@Override
	public final String toString()
	{
		return _target.toString() + ' ' + _runnable.hashCode();
	}
	
	public final void addLast(final OrderedExecutionTask task)
	{
		_target.addLast(task);
	}
	
	public final OrderedExecutionTask removeFirst()
	{
		return _target.removeFirst();
	}
}