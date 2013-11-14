package ac.hw.mytv;

import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedManager;

public class ActivityFeedRetriever implements Runnable {
	
	private IActivityFeedManager activityFeedManager;
	
	public ActivityFeedRetriever(IActivityFeedManager activityFeedManager)
	{
		this.activityFeedManager = activityFeedManager;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
